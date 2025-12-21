import * as admin from "firebase-admin";
import express from "express";
import cors from "cors";
import { onRequest } from "firebase-functions/v2/https";
import { setGlobalOptions } from "firebase-functions/v2/options";

setGlobalOptions({ maxInstances: 10 });

admin.initializeApp();
const db = admin.firestore();
const MAX_SCORE = 100_000;
const MAX_PRIZE_VALUE = 100_000;

const app = express();
app.use(cors({ origin: true }));
app.use(express.json());

type AuthedRequest = express.Request & { user?: admin.auth.DecodedIdToken };

// --- Auth middleware: exige Bearer token y lo verifica ---
async function requireAuth(req: AuthedRequest, res: express.Response, next: express.NextFunction) {
  try {
    const header = req.header("Authorization") || "";
    const match = header.match(/^Bearer (.+)$/);
    if (!match) return res.status(401).json({ error: "missing_bearer_token" });

    const idToken = match[1];
    const decoded = await admin.auth().verifyIdToken(idToken);
    req.user = decoded;
    return next();
  } catch (e: any) {
    return res.status(401).json({ error: "invalid_token", detail: String(e?.message ?? e) });
  }
}

function requireAdmin(req: any, res: any, next: any) {
  const user = req.user || {};
  if (user.admin === true || user.role === "admin" || user.claims?.admin === true) {
    return next();
  }
  return res.status(403).json({ error: "forbidden", detail: "admin_only" });
}

// --- GET /leaderboard/top10 ---
app.get("/leaderboard/top10", async (_req, res) => {
  const snap = await db.collection("players")
    .orderBy("coins", "desc")
    .limit(10)
    .get();

  const items = snap.docs.map((d, idx) => {
    const data = d.data() as any;
    return {
      id: d.id,
      alias: data.alias ?? "Jugador",
      score: data.coins ?? 0,
      position: idx + 1,
      prizeName: null,
      prizeDescription: null,
    };
  });

  res.json(items);
});

// --- POST /scores (requiere auth) ---
app.post("/scores", requireAuth, async (req: AuthedRequest, res) => {
  try {
    const { alias, score } = (req.body ?? {}) as any;

    if (typeof alias !== "string" || !alias.trim()) return res.status(400).json({ error: "invalid_alias" });
    const sanitizedAlias = alias.trim();
    if (sanitizedAlias.length < 3 || sanitizedAlias.length > 30)
      return res.status(400).json({ error: "invalid_alias_length" });
    if (!Number.isInteger(score) || score < 0 || score > MAX_SCORE)
      return res.status(400).json({ error: "invalid_score_range" });
    if (!req.user?.uid) return res.status(401).json({ error: "missing_uid" });

    await db.collection("scores").add({
      uid: req.user.uid,
      alias: sanitizedAlias,
      score,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });
    const playerRef = db.doc(`players/${req.user.uid}`);
        await db.runTransaction(async (tx) => {
          const snap = await tx.get(playerRef);
          const currentCoins = (snap.exists ? (snap.data() as any).coins : 0) ?? 0;
          const nextCoins = Math.max(currentCoins, score);

          tx.set(playerRef, {
            alias: sanitizedAlias,
            coins: nextCoins,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
          }, { merge: true });
        });
    return res.status(204).send();
  } catch (e: any) {
    console.error("POST /scores failed", e);
    return res.status(500).json({ error: "internal", detail: String(e?.message ?? e) });
  }
});

// --- GET /prize/common ---
app.get("/prize/common", async (_req, res) => {
  const ref = db.doc("prize/common");
  const doc = await ref.get();

  if (!doc.exists) {
    return res.json({ descripcion: "Premio común", valor: 0, updatedAt: Date.now() });
  }

  const data = doc.data() as any;
  res.json({
    descripcion: data.descripcion ?? "Premio común",
    valor: data.valor ?? 0,
    updatedAt: data.updatedAt ?? null,
  });
});

// --- PUT /prize/common (requiere auth) ---
app.put("/prize/common", requireAuth, requireAdmin, async (req: any, res) => {
  const { descripcion, valor } = (req.body ?? {}) as any;

  if (typeof descripcion !== "string" || !descripcion.trim()) return res.status(400).json({ error: "invalid_descripcion" });
  if (!Number.isInteger(valor) || valor < 0 || valor > MAX_PRIZE_VALUE) return res.status(400).json({ error: "invalid_valor" });
  if (!req.user?.uid) return res.status(401).json({ error: "missing_uid" });

  const payload = {
    descripcion: descripcion.trim(),
    valor,
    updatedAt: Date.now(),
    updatedByUid: req.user.uid,
  };

  await db.doc("prize/common").set(payload, { merge: true });

  res.json({ descripcion: payload.descripcion, valor: payload.valor, updatedAt: payload.updatedAt });
});

// --- POST /login (requiere auth) ---
// Nota: esto NO "loguea" (ya estás logueado). Solo registra alias.
app.post("/login", requireAuth, async (req: AuthedRequest, res) => {
  const alias = String((req.body ?? {}).alias ?? "").trim();
  if (!alias) return res.status(400).json({ error: "invalid_alias" });
  if (!req.user?.uid) return res.status(401).json({ error: "missing_uid" });

  await db.doc(`users/${req.user.uid}`).set(
    { alias, updatedAt: Date.now() },
    { merge: true }
  );

  const token = (req.header("Authorization") || "").replace(/^Bearer /, "");
  const isAdmin = req.user.admin === true || req.user.role === "admin" || req.user.claims?.admin === true;
  res.json({ token, alias, isAdmin });
});

// Export HTTP function (2nd gen)
export const api = onRequest({ cors: true }, app);
