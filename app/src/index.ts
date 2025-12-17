import express from "express";
import cors from "cors";
import { onRequest } from "firebase-functions/v2/https";
import admin from "firebase-admin";

admin.initializeApp();
const db = admin.firestore();

const app = express();
app.use(cors({ origin: true }));
app.use(express.json());

// --- Auth middleware: exige Bearer token y lo verifica ---
async function requireAuth(req: any, res: any, next: any) {
  try {
    const header = req.header("Authorization") || "";
    const match = header.match(/^Bearer (.+)$/);
    if (!match) return res.status(401).json({ error: "missing_bearer_token" });

    const idToken = match[1];
    const decoded = await admin.auth().verifyIdToken(idToken); // :contentReference[oaicite:2]{index=2}
    req.user = decoded; // uid, email, etc.
    return next();
  } catch (e: any) {
    return res.status(401).json({ error: "invalid_token", detail: String(e?.message ?? e) });
  }
}

// --- GET /leaderboard/top10 ---
app.get("/leaderboard/top10", async (_req, res) => {
  const snap = await db.collection("scores")
    .orderBy("score", "desc")
    .limit(10)
    .get();

  const items = snap.docs.map((d, idx) => {
    const data = d.data();
    return {
      id: d.id,
      alias: data.alias,
      score: data.score,
      position: idx + 1,
      prizeName: data.prizeName ?? null,
      prizeDescription: data.prizeDescription ?? null,
    };
  });

  res.json(items);
});

// --- POST /scores (requiere auth) ---
app.post("/scores", requireAuth, async (req: any, res) => {
  const { alias, score } = req.body ?? {};
  if (typeof alias !== "string" || !alias.trim()) return res.status(400).json({ error: "invalid_alias" });
  if (typeof score !== "number" || !Number.isFinite(score)) return res.status(400).json({ error: "invalid_score" });

  const uid = req.user.uid;
  await db.collection("scores").add({
    uid,
    alias: alias.trim(),
    score,
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
  });

  res.status(204).send();
});

// --- GET /prize/common ---
app.get("/prize/common", async (_req, res) => {
  const ref = db.doc("prize/common");
  const doc = await ref.get();
  if (!doc.exists) {
    // default para no romper
    return res.json({ descripcion: "Premio común", valor: 0, updatedAt: Date.now() });
  }
  const data: any = doc.data();
  res.json({
    descripcion: data.descripcion ?? "Premio común",
    valor: data.valor ?? 0,
    updatedAt: data.updatedAt ?? null,
  });
});

// --- PUT /prize/common (requiere auth) ---
app.put("/prize/common", requireAuth, async (req: any, res) => {
  const { descripcion, valor } = req.body ?? {};
  if (typeof descripcion !== "string" || !descripcion.trim()) return res.status(400).json({ error: "invalid_descripcion" });
  if (typeof valor !== "number" || !Number.isFinite(valor)) return res.status(400).json({ error: "invalid_valor" });

  const ref = db.doc("prize/common");
  const payload = {
    descripcion: descripcion.trim(),
    valor,
    updatedAt: Date.now(),
    updatedByUid: req.user.uid,
  };
  await ref.set(payload, { merge: true });

  res.json({ descripcion: payload.descripcion, valor: payload.valor, updatedAt: payload.updatedAt });
});

// --- POST /login (opcional) ---
app.post("/login", requireAuth, async (req: any, res) => {
  const alias = String(req.body?.alias ?? "").trim();
  if (!alias) return res.status(400).json({ error: "invalid_alias" });

  // Aquí “registras” el alias del usuario (opcional)
  await db.doc(`users/${req.user.uid}`).set(
    { alias, updatedAt: Date.now() },
    { merge: true }
  );

  // Para tu app: token = el mismo ID token (sirve como sesión)
  res.json({ token: req.header("Authorization")!.replace(/^Bearer /, ""), alias });
});

// Export HTTP function (2nd gen)
export const api = onRequest({ cors: true }, app);
