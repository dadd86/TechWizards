-- ================================================================
-- 📦 BASE DE DATOS: TechWizards (Juego de Azar con Dado)
-- 🧠 OBJETIVO: Persistencia segura (Room + RxJava3 + JSON + FKs)
-- 🔒 VERSIÓN: V3 (Hardening)
-- ================================================================

PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = FULL;
PRAGMA secure_delete = ON;
PRAGMA temp_store = MEMORY;
PRAGMA mmap_size = 134217728;

BEGIN TRANSACTION;

-- ======================= 👤 USUARIO ===============================
CREATE TABLE IF NOT EXISTS Usuario (
    numero       INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario      TEXT    NOT NULL CHECK (length(trim(usuario)) BETWEEN 3 AND 50),
    fechaAlta    INTEGER NOT NULL CHECK (fechaAlta > 0),
    monedas      INTEGER NOT NULL DEFAULT 0 CHECK (monedas >= 0),
    gano         INTEGER NOT NULL DEFAULT 0 CHECK (gano IN (0,1)),
    firebaseUid  TEXT UNIQUE
);

-- ======================= 🏷️ IDMAP ================================
CREATE TABLE IF NOT EXISTS IdMap (
    localTable        TEXT NOT NULL CHECK (length(localTable) > 0),
    localId           TEXT NOT NULL,
    remoteCollection  TEXT NOT NULL,
    remoteId          TEXT NOT NULL,
    PRIMARY KEY (localTable, localId),
    UNIQUE (remoteCollection, remoteId)
);

-- ======================= 🏠 LOBBY ================================
CREATE TABLE IF NOT EXISTS Lobby (
    id           TEXT PRIMARY KEY,
    nombre       TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
    codigo       TEXT UNIQUE,
    modo         TEXT NOT NULL CHECK (modo IN ('1v1','duos','solo')),
    estado       TEXT NOT NULL CHECK (estado IN ('PENDING','FULL','CLOSED')),
    creadorNum   INTEGER NOT NULL,
    createdAtMs  INTEGER NOT NULL,
    FOREIGN KEY (creadorNum) REFERENCES Usuario(numero)
        ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_lobby_estado_createdAt ON Lobby(estado, createdAtMs DESC);

-- ======================= 🎮 MATCH ================================
CREATE TABLE IF NOT EXISTS Match (
    id           TEXT PRIMARY KEY,
    lobbyId      TEXT,
    modo         TEXT NOT NULL,
    estado       TEXT NOT NULL CHECK (estado IN ('PENDING','ACTIVE','FINISHED','CANCELLED')),
    createdByNum INTEGER NOT NULL,
    createdAtMs  INTEGER NOT NULL,
    startedAtMs  INTEGER,
    finishedAtMs INTEGER,
    FOREIGN KEY (createdByNum) REFERENCES Usuario(numero)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (lobbyId) REFERENCES Lobby(id)
        ON DELETE SET NULL ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_match_estado_createdAt ON Match(estado, createdAtMs DESC);

-- ======================= 👥 PARTICIPANTE ==========================
CREATE TABLE IF NOT EXISTS MatchParticipant (
    matchId     TEXT NOT NULL,
    usuarioNum  INTEGER NOT NULL,
    rol         TEXT CHECK (rol IN ('host','player','guest')),
    teamId      TEXT,
    joinedAtMs  INTEGER NOT NULL,
    leftAtMs    INTEGER,
    score       INTEGER NOT NULL DEFAULT 0 CHECK (score >= 0),
    PRIMARY KEY (matchId, usuarioNum),
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (usuarioNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_participant_match ON MatchParticipant(matchId);

-- ======================= 📜 EVENTOS ==============================
CREATE TABLE IF NOT EXISTS MatchEvent (
    id           TEXT PRIMARY KEY,
    matchId      TEXT NOT NULL,
    seq          INTEGER NOT NULL,
    type         TEXT NOT NULL,
    actorNum     INTEGER NOT NULL,
    payloadJson  TEXT CHECK (json_valid(payloadJson)),
    createdAtMs  INTEGER NOT NULL,
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (actorNum) REFERENCES Usuario(numero) ON DELETE CASCADE,
    UNIQUE (matchId, seq)
);
CREATE INDEX IF NOT EXISTS idx_event_match_seq ON MatchEvent(matchId, seq);

-- ======================= 💬 MENSAJES =============================
CREATE TABLE IF NOT EXISTS Message (
    id           TEXT PRIMARY KEY,
    matchId      TEXT NOT NULL,
    senderNum    INTEGER NOT NULL,
    text         TEXT NOT NULL CHECK (length(trim(text)) BETWEEN 1 AND 500),
    createdAtMs  INTEGER NOT NULL,
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (senderNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_message_match_time ON Message(matchId, createdAtMs);

-- ======================= 💰 MONEDERO =============================
CREATE TABLE IF NOT EXISTS Monedero (
    id            TEXT PRIMARY KEY,
    usuarioNumero INTEGER NOT NULL,
    saldo         INTEGER NOT NULL DEFAULT 0 CHECK (saldo >= 0),
    FOREIGN KEY (usuarioNumero) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_monedero_usuario ON Monedero(usuarioNumero);

-- ======================= 🧮 SCORE ================================
CREATE TABLE IF NOT EXISTS MatchScore (
    matchId     TEXT NOT NULL,
    usuarioNum  INTEGER NOT NULL,
    score       INTEGER NOT NULL CHECK (score >= 0),
    PRIMARY KEY (matchId, usuarioNum),
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (usuarioNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);

-- ======================= 🗓️ EVENTO ==============================
CREATE TABLE IF NOT EXISTS evento (
    id           TEXT PRIMARY KEY,
    nombre       TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
    descripcion  TEXT NOT NULL CHECK (length(trim(descripcion)) > 0),
    fechaInicio  INTEGER NOT NULL,
    fechaFin     INTEGER NOT NULL,
    completado   INTEGER NOT NULL DEFAULT 0 CHECK (completado IN (0,1))
);
CREATE INDEX IF NOT EXISTS idx_evento_inicio ON evento(fechaInicio);

-- ======================= 📤 OUTBOX ===============================
CREATE TABLE IF NOT EXISTS Outbox (
    operationId  TEXT PRIMARY KEY,
    entityType   TEXT NOT NULL,
    entityId     TEXT NOT NULL,
    op           TEXT NOT NULL CHECK (op IN ('CREATE','UPDATE','DELETE')),
    payloadJson  TEXT NOT NULL CHECK (json_valid(payloadJson)),
    attempt      INTEGER NOT NULL DEFAULT 0 CHECK (attempt >= 0),
    lastError    TEXT,
    createdAtMs  INTEGER NOT NULL,
    updatedAtMs  INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_outbox_entity ON Outbox(entityType, entityId);

-- ======================= 🪦 TOMBSTONE ============================
CREATE TABLE IF NOT EXISTS Tombstone (
    tableName    TEXT NOT NULL,
    entityId     TEXT NOT NULL,
    deletedAtMs  INTEGER NOT NULL,
    PRIMARY KEY (tableName, entityId)
);

-- ======================= 🧩 PARTIDA ==============================
CREATE TABLE IF NOT EXISTS Partida (
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    usuarioNumero  INTEGER NOT NULL REFERENCES Usuario(numero)
        ON DELETE CASCADE ON UPDATE CASCADE,
    fecha          INTEGER NOT NULL,
    resultado      TEXT    NOT NULL CHECK (resultado IN ('GANADO','PERDIDO')),
    cambioMonedas  INTEGER NOT NULL,
    nombreJugador  TEXT    NOT NULL DEFAULT '' CHECK (length(nombreJugador) <= 60)
);
CREATE INDEX IF NOT EXISTS idx_partida_usuario_fecha ON Partida(usuarioNumero, fecha DESC);

-- FKs hijos (recomendado)
CREATE INDEX IF NOT EXISTS idx_lobby_creadorNum            ON Lobby(creadorNum);
CREATE INDEX IF NOT EXISTS idx_match_createdByNum          ON Match(createdByNum);
CREATE INDEX IF NOT EXISTS idx_match_lobbyId               ON Match(lobbyId);
-- MatchParticipant tiene PK (matchId, usuarioNum): ya indexa ambas.
CREATE INDEX IF NOT EXISTS idx_message_senderNum           ON Message(senderNum);
-- Message(matchId) ya indexado por (matchId, createdAtMs) -> OK
CREATE INDEX IF NOT EXISTS idx_monedero_usuarioNumero      ON Monedero(usuarioNumero);
-- MatchScore: PK (matchId, usuarioNum) -> ya indexa ambas
-- Partida ya tiene (usuarioNumero, fecha DESC) -> OK

COMMIT;
