import functionsFramework from '@google-cloud/functions-framework';
import admin from 'firebase-admin';
import express, {
  ErrorRequestHandler,
  NextFunction,
  Request,
  RequestHandler,
  Response,
} from 'express';
import cors from 'cors';
import { OAuth2Client, TokenPayload } from 'google-auth-library';

if (admin.apps.length === 0) {
  admin.initializeApp();
}

const firestore = admin.firestore();
const oauthClient = process.env.GOOGLE_OAUTH_CLIENT_ID
  ? new OAuth2Client(process.env.GOOGLE_OAUTH_CLIENT_ID)
  : null;

type TokenSource = 'firebase' | 'google';

interface AuthenticatedUser {
  uid: string;
  email?: string;
  name?: string;
  tokenSource: TokenSource;
}

type AuthenticatedRequest = Request & { user?: AuthenticatedUser };

type DecodedToken = admin.auth.DecodedIdToken | TokenPayload;


const app = express();
app.use(cors({ origin: true }));
app.use(express.json());
app.options('*', cors());

const unauthorized = (res: Response) => res.status(401).json({ error: 'Unauthorized' });

const asyncHandler = (handler: RequestHandler) =>
  (req: Request, res: Response, next: NextFunction) =>
    Promise.resolve(handler(req, res, next)).catch(next);

async function authenticate(
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction,
): Promise<Response | void> {
  const header = req.get('authorization') || '';
  if (!header.toLowerCase().startsWith('bearer ')) {
    return unauthorized(res);
  }

  const token = header.split(' ')[1];
  let decoded: DecodedToken | undefined;
  let tokenSource: TokenSource = 'firebase';

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

const uid = decoded && 'uid' in decoded && decoded.uid
    ? decoded.uid
    : (decoded as TokenPayload | undefined)?.sub || '';

  const user: AuthenticatedUser = {
    uid,
    email: decoded?.email || undefined,
    name:
      decoded?.name ||
      (decoded as admin.auth.DecodedIdToken | undefined)?.displayName ||
      (decoded as TokenPayload | undefined)?.name ||
      (decoded as { alias?: string } | undefined)?.alias,
    tokenSource,
  };
  req.user = user;
  return next();
}

function sanitizeAlias(alias: string): string {
  return alias.trim().toLowerCase().replace(/[^a-z0-9-_]/gi, '_');
}

function toPrizeDto(data?: FirebaseFirestore.DocumentData | null) {
  if (!data) {
    return { descripcion: '', valor: 0, updatedAt: null };
  }
  return {
    descripcion: (data.descripcion as string) || '',
    valor: Number.isFinite(data.valor) ? (data.valor as number) : 0,
    updatedAt: data.updatedAt ? data.updatedAt.toMillis?.() || data.updatedAt : null,
  };
}

function toScoreDto(
  doc: FirebaseFirestore.QueryDocumentSnapshot<FirebaseFirestore.DocumentData>,
  position: number,
) {
  const data = doc.data();
  return {
    id: doc.id,
    alias: data.alias as string,
    score: data.score as number,
    position,
    prizeName: (data.prizeName as string) || null,
    prizeDescription: (data.prizeDescription as string) || null,
  };
}

app.post('/login', asyncHandler(async (req: Request, res: Response) => {
  const body = (req.body ?? {}) as { alias?: string };
  const { alias } = body;
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

app.post('/matches', authenticate, asyncHandler(async (req: AuthenticatedRequest, res: Response) => {
  const user = req.user as AuthenticatedUser;
  const matchRef = await firestore.collection('matches').add({
    status: 'PENDING',
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    createdBy: user.uid,
  });

  const snapshot = await matchRef.get();
  return res.status(201).json({ id: matchRef.id, ...snapshot.data() });
}));

app.post('/matches/:id/ready', authenticate, asyncHandler(async (req: AuthenticatedRequest, res: Response) => {
  const user = req.user as AuthenticatedUser;
  const { id } = req.params;
  const { playerNumber } = (req.body ?? {}) as { playerNumber?: number };
  if (playerNumber === undefined) {
    return res.status(400).json({ error: 'playerNumber requerido' });
  }

  const matchRef = firestore.collection('matches').doc(id);
  const readyRef = matchRef.collection('ready').doc(String(playerNumber));

  await readyRef.set({
    playerNumber,
    userId: user.uid,
    readyAt: admin.firestore.FieldValue.serverTimestamp(),
  });

  return res.status(204).send();
}));

app.post('/matches/:id/roll', authenticate, asyncHandler(async (req: AuthenticatedRequest, res: Response) => {
  const user = req.user as AuthenticatedUser;
  const { id } = req.params;
  const { playerNumber, roll } = (req.body ?? {}) as { playerNumber?: number; roll?: number };
  if (playerNumber === undefined || roll === undefined) {
    return res.status(400).json({ error: 'playerNumber y roll requeridos' });
  }

  const matchRef = firestore.collection('matches').doc(id);
  const rollRef = matchRef.collection('rolls').doc(String(playerNumber));

  await firestore.runTransaction(async (transaction: FirebaseFirestore.Transaction) => {
    const matchSnapshot = await transaction.get(matchRef);
    if (!matchSnapshot.exists) {
      throw new Error('Match no encontrado');
    }

    transaction.set(rollRef, {
      playerNumber,
      roll,
      userId: user.uid,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    transaction.set(
      matchRef,
      { [`scores.${playerNumber}`]: roll },
      { merge: true },
    );
  });

  return res.status(204).send();
}));

app.get('/matches/:id', authenticate, asyncHandler(async (req: AuthenticatedRequest, res: Response) => {
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

const ready = readySnapshot.docs.map(
    (doc: FirebaseFirestore.QueryDocumentSnapshot<FirebaseFirestore.DocumentData>) => ({
      id: doc.id,
      ...doc.data(),
    }),
  );
  const rolls = rollsSnapshot.docs.map(
    (doc: FirebaseFirestore.QueryDocumentSnapshot<FirebaseFirestore.DocumentData>) => ({
      id: doc.id,
      ...doc.data(),
    }),
  );

  return res.json({ id: matchSnapshot.id, ...matchSnapshot.data(), ready, rolls });
}));

async function getLeaderboard(limit: number) {
  const effectiveLimit = Number.isFinite(limit) ? Math.min(Math.max(limit, 1), 100) : 10;
  const snapshot = await firestore
    .collection('scores')
    .orderBy('score', 'desc')
    .orderBy('createdAt', 'asc')
    .limit(effectiveLimit)
    .get();

  return snapshot.docs.map((doc, index) => toScoreDto(doc, index + 1));
}

app.get('/leaderboard/top10', authenticate, asyncHandler(async (_req: AuthenticatedRequest, res: Response) => {
  const leaderboard = await getLeaderboard(10);
  return res.json(leaderboard);
}));

app.get('/leaderboard', authenticate, asyncHandler(async (req: AuthenticatedRequest, res: Response) => {
  const limitParam = req.query.limit;
  const limit = typeof limitParam === 'string'
    ? parseInt(limitParam, 10)
    : Array.isArray(limitParam) && typeof limitParam[0] === 'string'
      ? parseInt(limitParam[0], 10)
      : 10;

  const leaderboard = await getLeaderboard(limit);
  return res.json(leaderboard);
}));

app.post('/scores', authenticate, asyncHandler(async (req: AuthenticatedRequest, res: Response) => {
  const user = req.user as AuthenticatedUser;
  const body = (req.body ?? {}) as {
    alias?: string;
    score?: number;
    prizeName?: string | null;
    prizeDescription?: string | null;
  };
  const { alias, score } = body;
  if (!alias || !Number.isFinite(score)) {
    return res.status(400).json({ error: 'alias y score requeridos' });
  }

  const entry = {
    alias,
    score: score as number,
    userId: user.uid,
    prizeName: body.prizeName || null,
    prizeDescription: body.prizeDescription || null,
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

app.get('/prize/common', authenticate, asyncHandler(async (_req: AuthenticatedRequest, res: Response) => {
  const { snapshot } = await fetchPrize();
  return res.json(toPrizeDto(snapshot.data()));
}));

app.get('/commonPrize', authenticate, asyncHandler(async (_req: AuthenticatedRequest, res: Response) => {
  const { snapshot } = await fetchPrize();
  return res.json(toPrizeDto(snapshot.data()));
}));

app.put('/prize/common', authenticate, asyncHandler(async (req: AuthenticatedRequest, res: Response) => {
  const { descripcion, valor } = (req.body ?? {}) as { descripcion?: string; valor?: number };
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

const errorHandler: ErrorRequestHandler = (error, _req, res, _next) => {
  // eslint-disable-next-line no-console
  console.error('Unhandled error', error);
  const message = (error as { message?: string })?.message || 'Error interno';
  return res.status(500).json({ error: message });
};

app.use(errorHandler);

functionsFramework.http('api', app);