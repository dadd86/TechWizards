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

type AuthedRequest = express.Request & {
  user?: admin.auth.DecodedIdToken;
};

/* =========================================================
 * Helpers de log
 * ========================================================= */
function maskToken(headerValue?: string | null) {
  const h = headerValue ?? "";
  const m = h.match(/^Bearer (.+)$/);
  if (!m) return { hasBearer: false };

  const token = m[1];
  return {
    hasBearer: true,
    tokenLen: token.length,
    preview:
      token.length <= 12
        ? token
        : `${token.slice(0, 6)}...${token.slice(-6)}`,
  };
}

function reqInfo(req: express.Request) {
  return {
    method: req.method,
    path: req.path,
    ip: req.ip,
  };
}

/* =========================================================
 * Auth middleware
 * ========================================================= */
async function requireAuth(
  req: AuthedRequest,
  res: express.Response,
  next: express.NextFunction
) {
  const authHeader = req.header("Authorization");

  console.log("[AUTH] IN", {
    ...reqInfo(req),
    auth: maskToken(authHeader),
  });

  try {
    const match = authHeader?.match(/^Bearer (.+)$/);
    if (!match) {
      console.warn("[AUTH] missing_bearer");
      return res.status(401).json({ error: "missing_bearer_token" });
    }

    const decoded = await admin.auth().verifyIdToken(match[1]);
    req.user = decoded;

    console.log("[AUTH] OK", {
      uid: decoded.uid,
      email: decoded.email ?? null,
    });

    return next();
  } catch (e: any) {
    console.error("[AUTH] invalid_token", e?.message ?? e);
    return res.status(401).json({ error: "invalid_token" });
  }
}

function requireAdmin(req: AuthedRequest, res: express.Response, next: any) {
  const u: any = req.user;
  if (u?.admin === true || u?.role === "admin" || u?.claims?.admin === true) {
    return next();
  }
  return res.status(403).json({ error: "admin_only" });
}

/* =========================================================
 * GET /leaderboard/top10
 * ========================================================= */
app.get("/leaderboard/top10", async (_req, res) => {
  console.log("[TOP10] IN");

  const snap = await db
    .collection("players")
    .orderBy("wins", "desc")
    .limit(10)
    .get();

  const items = snap.docs.map((d, idx) => {
    const data = d.data() as any;
    const wins = Number(data.wins ?? 0);
    return {
      id: data.usuarioNumero?.toString() ?? d.id,
      alias:
        data.alias?.trim() ||
        data.aliasJugador?.trim() ||
        "Jugador",
      score: Number(data.coins ?? 0),
      wins: Number.isFinite(wins) ? wins : 0,
      position: idx + 1,
      prizeName: null,
      prizeDescription: null,
    };
  });

  res.json(items);
});
/* =========================================================
 * GET /scores/top10 (compat con OpenAPI actual)
 * ========================================================= */
app.get("/scores/top10", async (_req, res) => {
  console.log("[SCORES_TOP10] IN");

  const snap = await db
    .collection("players")
    .orderBy("wins", "desc")
    .limit(10)
    .get();

  const items = snap.docs.map((d) => {
    const data = d.data() as any;
    const updatedAt = data.updatedAt ?? null;
    const updatedAtIso =
      typeof updatedAt?.toDate === "function"
        ? updatedAt.toDate().toISOString()
        : typeof updatedAt === "number"
          ? new Date(updatedAt).toISOString()
          : null;
    const wins = Number(data.wins ?? 0);

    return {
      userId: data.usuarioNumero?.toString() ?? d.id,
      userName:
        data.alias?.trim() ||
        data.aliasJugador?.trim() ||
        "Jugador",
      points: Number(data.coins ?? 0),
      wins: Number.isFinite(wins) ? wins : 0,
      timestamp: updatedAtIso,
    };
  });

  res.json({ items });
});
/* =========================================================
 * POST /login
 * ========================================================= */
app.post("/login", requireAuth, async (req: AuthedRequest, res) => {
  console.log("[LOGIN] IN", {
    uid: req.user?.uid ?? null,
    body: req.body,
  });

  const alias = String(req.body?.alias ?? "").trim();
  if (!alias) return res.status(400).json({ error: "invalid_alias" });
  if (!req.user?.uid) return res.status(401).json({ error: "missing_uid" });

  const uid = req.user.uid;
  const now = Date.now();

  try {
    await db.doc(`users/${uid}`).set(
      { alias, updatedAt: now },
      { merge: true }
    );

    await db.doc(`players/${uid}`).set(
      {
        uid,
        alias,
        coins: 0,
        wins: 0,
        losses: 0,
        updatedAt: now,
      },
      { merge: true }
    );

    const token = (req.header("Authorization") || "").replace(/^Bearer /, "");
    const isAdmin =
      (req.user as any)?.admin === true ||
      (req.user as any)?.role === "admin" ||
      (req.user as any)?.claims?.admin === true;

    console.log("[LOGIN] OK", { uid, alias, isAdmin });

    res.json({ token, alias, isAdmin });
  } catch (e: any) {
    console.error("[LOGIN] FAIL", e?.message ?? e);
    res.status(500).json({ error: "internal" });
  }
});

/* =========================================================
 * POST /scores
 * ========================================================= */
app.post("/scores", requireAuth, async (req: AuthedRequest, res) => {
  console.log("[SCORE] IN", {
    uid: req.user?.uid ?? null,
    body: req.body,
  });

  try {
    const { alias, deltaMonedas } = req.body ?? {};
    const uid = req.user?.uid;

    if (!uid) return res.status(401).json({ error: "missing_uid" });
    if (typeof alias !== "string" || !alias.trim())
      return res.status(400).json({ error: "invalid_alias" });

    if (
      !Number.isInteger(deltaMonedas) ||
      deltaMonedas < -MAX_SCORE ||
      deltaMonedas > MAX_SCORE
    ) {
      return res.status(400).json({ error: "invalid_delta" });
    }

    const sanitizedAlias = alias.trim();
    const playerRef = db.doc(`players/${uid}`);
    const historyRef = playerRef.collection("history").doc();

    await db.runTransaction(async (tx) => {
      const snap = await tx.get(playerRef);
      const currentCoins = Number(snap.data()?.coins ?? 0);
      const newCoins = Math.max(0, currentCoins + deltaMonedas);

      console.log("[SCORE] TX", {
        uid,
        deltaMonedas,
        currentCoins,
        newCoins,
      });

      tx.set(
        playerRef,
        {
          uid,
          alias: sanitizedAlias,
          coins: newCoins,
          updatedAt: Date.now(),
        },
        { merge: true }
      );

      tx.set(historyRef, {
        uid,
        alias: sanitizedAlias,
        deltaMonedas,
        coinsAfter: newCoins,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    });

    console.log("[SCORE] OK", { uid, deltaMonedas });
    res.status(204).send();
  } catch (e: any) {
    console.error("[SCORE] FAIL", e?.message ?? e);
    res.status(500).json({ error: "internal" });
  }
});

/* =========================================================
 * GET /prize/common
 * ========================================================= */
app.get("/prize/common", async (_req, res) => {
  const ref = db.doc("prize/common");
  const doc = await ref.get();

  if (!doc.exists) {
    return res.json({
      descripcion: "Premio común",
      valor: 0,
      updatedAt: Date.now(),
    });
  }

  const data = doc.data() as any;
  res.json({
    descripcion: data.descripcion ?? "Premio común",
    valor: data.valor ?? 0,
    updatedAt: data.updatedAt ?? null,
  });
});

/* =========================================================
 * PUT /prize/common (admin)
 * ========================================================= */
app.put(
  "/prize/common",
  requireAuth,
  requireAdmin,
  async (req: AuthedRequest, res) => {
    const { descripcion, valor } = req.body ?? {};

    if (!descripcion?.trim())
      return res.status(400).json({ error: "invalid_descripcion" });
    if (!Number.isInteger(valor) || valor < 0 || valor > MAX_PRIZE_VALUE)
      return res.status(400).json({ error: "invalid_valor" });

    await db.doc("prize/common").set(
      {
        descripcion: descripcion.trim(),
        valor,
        updatedAt: Date.now(),
        updatedByUid: req.user?.uid,
      },
      { merge: true }
    );

    res.json({ descripcion, valor });
  }
);

/* =========================================================
 * POST /prize/common/increment
 *  - Suma delta al premio común
 * ========================================================= */
app.post("/prize/common/increment", requireAuth, async (req: AuthedRequest, res) => {
  const uid = req.user?.uid ?? null;
  const delta = Number(req.body?.delta);

  console.log("[PRIZE_INC] IN", { uid, delta });

  if (!Number.isInteger(delta) || delta <= 0 || delta > MAX_PRIZE_VALUE) {
    return res.status(400).json({ error: "invalid_delta" });
  }

  try {
    const ref = db.doc("prize/common");

    const result = await db.runTransaction(async (tx) => {
      const snap = await tx.get(ref);
      const current = Number(snap.data()?.valor ?? 0);
      const next = current + delta;

      tx.set(ref, {
        descripcion: snap.data()?.descripcion ?? "Premio común",
        valor: next,
        updatedAt: Date.now(),
        updatedByUid: uid,
      }, { merge: true });

      return { descripcion: snap.data()?.descripcion ?? "Premio común", valor: next };
    });

    console.log("[PRIZE_INC] OK", { uid, valor: result.valor });
    return res.json(result);
   } catch (e: any) {
    console.error("[PRIZE_INC] FAIL FULL", e);
    return res.status(500).json({ error: e?.message ?? String(e) });
  }
});

/* =========================================================
 * POST /prize/common/claim
 *  - Cobra el premio común y lo resetea a 0 (desaparece)
 *  - Idempotente con claimId
 * ========================================================= */
app.post("/prize/common/claim", requireAuth, async (req: AuthedRequest, res) => {
  const uid = req.user?.uid;
  const claimId = String(req.body?.claimId ?? "").trim();

  console.log("[PRIZE_CLAIM] IN", { uid: uid ?? null, claimId });

  if (!uid) return res.status(401).json({ error: "missing_uid" });
  if (!claimId) return res.status(400).json({ error: "invalid_claimId" });

  try {
    const prizeRef = db.doc("prize/common");
    const playerRef = db.doc(`players/${uid}`);

    const result = await db.runTransaction(async (tx) => {
      const prizeSnap = await tx.get(prizeRef);
      const data = (prizeSnap.data() ?? {}) as any;

      const lastClaimId = String(data.lastClaimId ?? "");
      const lastClaimAmount = Number(data.lastClaimAmount ?? 0);

      // Idempotencia: si reintenta el mismo claimId, devolvemos lo mismo sin pagar de nuevo
      if (lastClaimId && lastClaimId === claimId) {
        return {
          descripcion: data.descripcion ?? "Premio común",
          claimed: lastClaimAmount,
          alreadyClaimed: true,
        };
      }

      const currentPrize = Number(data.valor ?? 0);
      const claimed = Math.max(0, currentPrize);

      // Resetea el bote y registra el claim
      tx.set(prizeRef, {
        descripcion: data.descripcion ?? "Premio común",
        valor: 0,
        lastClaimId: claimId,
        lastClaimAmount: claimed,
        lastClaimedByUid: uid,
        lastClaimedAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: Date.now(),
        updatedByUid: uid,
      }, { merge: true });

            // Paga al jugador sumando coins (robusto: si coins no es number, lo repara)
      if (claimed > 0) {
        const playerSnap = await tx.get(playerRef);
        const currentCoinsRaw = playerSnap.data()?.coins;

        const currentCoins =
          typeof currentCoinsRaw === "number" && Number.isFinite(currentCoinsRaw)
            ? currentCoinsRaw
            : 0;

        tx.set(
          playerRef,
          {
            coins: currentCoins + claimed,
            updatedAt: Date.now(),
          },
          { merge: true }
        );
      }


      return {
        descripcion: data.descripcion ?? "Premio común",
        claimed,
        alreadyClaimed: false,
      };
    });

    console.log("[PRIZE_CLAIM] OK", { uid, claimed: result.claimed, alreadyClaimed: result.alreadyClaimed });
    return res.json(result);
  } catch (e: any) {
    console.error("[PRIZE_CLAIM] FAIL FULL", e);
    return res.status(500).json({
      error: e?.message ?? String(e),
      stack: e?.stack ?? null,
    });
  }
});


/* =========================================================
 * Export
 * ========================================================= */
export const api = onRequest({ cors: true }, app);

