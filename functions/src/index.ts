const functionsFramework = require('@google-cloud/functions-framework');
const admin = require('firebase-admin');
const express = require('express');
const cors = require('cors');
const { OAuth2Client } = require('google-auth-library');

if (admin.apps.length === 0) {
  admin.initializeApp();
}

const firestore = admin.firestore();
const oauthClient = process.env.GOOGLE_OAUTH_CLIENT_ID
  ? new OAuth2Client(process.env.GOOGLE_OAUTH_CLIENT_ID)
  : null;

const app = express();
app.use(cors({ origin: true }));
app.use(express.json());
app.options('*', cors());

const unauthorized = (res) => res.status(401).json({ error: 'Unauthorized' });

const asyncHandler = (handler) => (req, res, next) =>
  Promise.resolve(handler(req, res, next)).catch(next);

async function authenticate(req, res, next) {
  const header = req.get('authorization') || '';
  if (!header.toLowerCase().startsWith('bearer ')) {
    return unauthorized(res);
  }

  const token = header.split(' ')[1];
  let decoded;
  let tokenSource = 'firebase';

  try {
    decoded = await admin.auth().verifyIdToken(token);
  } catch (firebaseError) {
    if (oauthClient) {
      try {
        const ticket = await oauthClient.verifyIdToken({
          idToken: token,
          audience: process.env.GOOGLE_OAUTH_CLIENT_ID,
        });
        decoded = ticket.getPayload();
        tokenSource = 'google';
      } catch (googleError) {
        return unauthorized(res);
      }
    } else {
      return unauthorized(res);
    }
  }

  req.user = {
    uid: decoded.uid || decoded.sub,
    email: decoded.email,
    name: decoded.name || decoded.displayName || decoded.alias,
    tokenSource,
  };
  return next();
}

function sanitizeAlias(alias) {
  return alias.trim().toLowerCase().replace(/[^a-z0-9-_]/gi, '_');
}

function toPrizeDto(data) {
  if (!data) {
    return { descripcion: '', valor: 0, updatedAt: null };
  }
  return {
    descripcion: data.descripcion || '',
    valor: Number.isFinite(data.valor) ? data.valor : 0,
    updatedAt: data.updatedAt ? data.updatedAt.toMillis?.() || data.updatedAt : null,
  };
}

function toScoreDto(doc, position) {
  const data = doc.data();
  return {
    id: doc.id,
    alias: data.alias,
    score: data.score,
    position,
    prizeName: data.prizeName || null,
    prizeDescription: data.prizeDescription || null,
  };
}

app.post('/login', asyncHandler(async (req, res) => {
  const { alias } = req.body || {};
  if (!alias || typeof alias !== 'string') {
    return res.status(400).json({ error: 'Alias requerido' });
  }

  const normalizedAlias = sanitizeAlias(alias);
  const uid = `alias_${normalizedAlias}`.slice(0, 128);

  try {
    await admin.auth().getUser(uid);
  } catch (getUserError) {
    await admin.auth().createUser({ uid, displayName: alias }).catch(() => {});
  }

  const customToken = await admin.auth().createCustomToken(uid, { alias });
  return res.json({ token: customToken, alias });
}));

app.post('/matches', authenticate, asyncHandler(async (req, res) => {
  const matchRef = await firestore.collection('matches').add({
    status: 'PENDING',
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    createdBy: req.user.uid,
  });

  const snapshot = await matchRef.get();
  return res.status(201).json({ id: matchRef.id, ...snapshot.data() });
}));

app.post('/matches/:id/ready', authenticate, asyncHandler(async (req, res) => {
  const { id } = req.params;
  const { playerNumber } = req.body || {};
  if (playerNumber === undefined) {
    return res.status(400).json({ error: 'playerNumber requerido' });
  }

  const readyRef = firestore
    .collection('matches')
    .doc(id)
    .collection('ready')
    .doc(String(playerNumber));

  await readyRef.set({
    playerNumber,
    userId: req.user.uid,
    readyAt: admin.firestore.FieldValue.serverTimestamp(),
  });

  return res.status(204).send();
}));

app.post('/matches/:id/roll', authenticate, asyncHandler(async (req, res) => {
  const { id } = req.params;
  const { playerNumber, roll } = req.body || {};
  if (playerNumber === undefined || roll === undefined) {
    return res.status(400).json({ error: 'playerNumber y roll requeridos' });
  }

  const matchRef = firestore.collection('matches').doc(id);
  const rollRef = matchRef.collection('rolls').doc(String(playerNumber));

  await firestore.runTransaction(async (transaction) => {
    const matchSnapshot = await transaction.get(matchRef);
    if (!matchSnapshot.exists) {
      throw new Error('Match no encontrado');
    }

    transaction.set(rollRef, {
      playerNumber,
      roll,
      userId: req.user.uid,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    transaction.set(
      matchRef,
      { [`scores.${playerNumber}`]: roll },
      { merge: true }
    );
  });

  return res.status(204).send();
}));

app.get('/matches/:id', authenticate, asyncHandler(async (req, res) => {
  const { id } = req.params;
  const matchRef = firestore.collection('matches').doc(id);
  const [matchSnapshot, readySnapshot, rollsSnapshot] = await Promise.all([
    matchRef.get(),
    matchRef.collection('ready').get(),
    matchRef.collection('rolls').get(),
  ]);

  if (!matchSnapshot.exists) {
    return res.status(404).json({ error: 'Match no encontrado' });
  }

  const ready = readySnapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
  const rolls = rollsSnapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));

  return res.json({ id: matchSnapshot.id, ...matchSnapshot.data(), ready, rolls });
}));

async function getLeaderboard(limit) {
  const effectiveLimit = Number.isFinite(limit) ? Math.min(Math.max(limit, 1), 100) : 10;
  const snapshot = await firestore
    .collection('scores')
    .orderBy('score', 'desc')
    .orderBy('createdAt', 'asc')
    .limit(effectiveLimit)
    .get();

  return snapshot.docs.map((doc, index) => toScoreDto(doc, index + 1));
}

app.get('/leaderboard/top10', authenticate, asyncHandler(async (req, res) => {
  const leaderboard = await getLeaderboard(10);
  return res.json(leaderboard);
}));

app.get('/leaderboard', authenticate, asyncHandler(async (req, res) => {
  const limit = parseInt(req.query.limit, 10) || 10;
  const leaderboard = await getLeaderboard(limit);
  return res.json(leaderboard);
}));

app.post('/scores', authenticate, asyncHandler(async (req, res) => {
  const { alias, score } = req.body || {};
  if (!alias || !Number.isFinite(score)) {
    return res.status(400).json({ error: 'alias y score requeridos' });
  }

  const entry = {
    alias,
    score,
    userId: req.user.uid,
    prizeName: req.body.prizeName || null,
    prizeDescription: req.body.prizeDescription || null,
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
  };

  const docRef = await firestore.collection('scores').add(entry);
  return res.status(201).json({ id: docRef.id });
}));

async function fetchPrize() {
  const prizeRef = firestore.collection('prizes').doc('common');
  const snapshot = await prizeRef.get();
  return { prizeRef, snapshot };
}

app.get('/prize/common', authenticate, asyncHandler(async (req, res) => {
  const { snapshot } = await fetchPrize();
  return res.json(toPrizeDto(snapshot.data()));
}));

app.get('/commonPrize', authenticate, asyncHandler(async (req, res) => {
  const { snapshot } = await fetchPrize();
  return res.json(toPrizeDto(snapshot.data()));
}));

app.put('/prize/common', authenticate, asyncHandler(async (req, res) => {
  const { descripcion, valor } = req.body || {};
  if (descripcion === undefined || valor === undefined) {
    return res.status(400).json({ error: 'descripcion y valor requeridos' });
  }

  const { prizeRef } = await fetchPrize();
  const payload = {
    descripcion,
    valor,
    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
  };

  await prizeRef.set(payload, { merge: true });
  const snapshot = await prizeRef.get();
  return res.json(toPrizeDto(snapshot.data()));
}));

app.use((error, req, res, next) => {
  // eslint-disable-next-line no-console
  console.error('Unhandled error', error);
  const message = error?.message || 'Error interno';
  return res.status(500).json({ error: message });
});

functionsFramework.http('api', app);