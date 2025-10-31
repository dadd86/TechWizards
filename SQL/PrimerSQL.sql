-- ================================================================
-- 📦 BASE DE DATOS: TechWizards (Juego de Azar con Dado)
-- 🧠 OBJETIVO: Persistencia de datos (Room + RxJava3)
-- 📄 AUTOR: Diego Armando Diaz Devia
-- 🗓️ FECHA DE ACTUALIZACIÓN: 2025-10-29 (V2)
-- ================================================================

-- =========================================================
-- 🔧 PRAGMAS (durabilidad y concurrencia en SQLite)
-- =========================================================
PRAGMA foreign_keys = ON;    -- Enforce FKs (Room también lo activa; lo dejamos explícito)
PRAGMA journal_mode = WAL;   -- Mejor concurrencia y recuperación
PRAGMA synchronous = FULL;   -- Máxima durabilidad (cámbialo a NORMAL si priorizas rendimiento)

BEGIN TRANSACTION;

-- =========================================================
-- 👤 USUARIO
-- Tabla identidad local del jugador.
-- Columnas (coinciden con tu UsuarioEntity):
--   numero (PK autoincr), usuario (alias), fechaAlta, monedas, gano, firebaseUid
-- =========================================================
CREATE TABLE IF NOT EXISTS Usuario (
    numero      INTEGER PRIMARY KEY AUTOINCREMENT,   -- PK (Room: @PrimaryKey(autoGenerate=true))
    usuario     TEXT    NOT NULL,                    -- alias visible
    fechaAlta   INTEGER NOT NULL,                    -- epoch millis (en entity lo nombras fechaAltaMs → columna "fechaAlta")
    monedas     INTEGER NOT NULL DEFAULT 0,
    gano        INTEGER NOT NULL DEFAULT 0,          -- Boolean en Room (0/1)
    firebaseUid TEXT UNIQUE                          -- vínculo opcional con Firebase Auth
);

-- 🔁 MIGRACIÓN LEGADA (si ya existía Usuario sin firebaseUid):
-- ⚠️ SQLite no soporta IF NOT EXISTS en ADD COLUMN; agrégalo en una Migration de Room:
-- ALTER TABLE Usuario ADD COLUMN firebaseUid TEXT UNIQUE;

-- =========================================================
-- 🏷️ IDMAP (correspondencia ID local ↔ remoto)
-- Entity: IdMapEntity (PK compuesta localTable+localId; unique en remoteCollection+remoteId)
-- =========================================================
CREATE TABLE IF NOT EXISTS IdMap (
    localTable        TEXT NOT NULL,
    localId           TEXT NOT NULL,
    remoteCollection  TEXT NOT NULL,
    remoteId          TEXT NOT NULL,
    PRIMARY KEY (localTable, localId),
    UNIQUE (remoteCollection, remoteId)
);

-- =========================================================
-- 🏠 LOBBY (sala previa a la partida)
-- Entity: LobbyEntity
-- Campos clave: estado en {'PENDING','FULL','CLOSED'}, FK creadorNum -> Usuario.numero
-- =========================================================
CREATE TABLE IF NOT EXISTS Lobby (
    id           TEXT PRIMARY KEY,                                         -- UUID/ULID
    nombre       TEXT NOT NULL,                                            -- visible
    codigo       TEXT UNIQUE,                                              -- código invitación
    modo         TEXT NOT NULL,                                            -- "1v1","duos",...
    estado       TEXT NOT NULL CHECK (estado IN ('PENDING','FULL','CLOSED')),
    creadorNum   INTEGER NOT NULL,                                         -- FK -> Usuario.numero
    createdAtMs  INTEGER NOT NULL,
    FOREIGN KEY (creadorNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_lobby__estado      ON Lobby(estado);
CREATE INDEX IF NOT EXISTS idx_lobby__created_at  ON Lobby(createdAtMs);

-- =========================================================
-- 🎮 MATCH (instancia de partida multijugador)
-- Entity: MatchEntity (alineado con tu dominio)
-- estado ∈ {'PENDING','ACTIVE','FINISHED','CANCELLED'}
-- lobbyId es FK opcional (si proviene de un lobby)
-- =========================================================
CREATE TABLE IF NOT EXISTS Match (
    id           TEXT PRIMARY KEY,                                         -- UUID/ULID
    lobbyId      TEXT,                                                     -- FK opcional -> Lobby.id
    modo         TEXT NOT NULL,
    estado       TEXT NOT NULL CHECK (estado IN ('PENDING','ACTIVE','FINISHED','CANCELLED')),
    createdByNum INTEGER NOT NULL,                                         -- FK -> Usuario.numero (host)
    createdAtMs  INTEGER NOT NULL,
    startedAtMs  INTEGER,
    finishedAtMs INTEGER,
    FOREIGN KEY (createdByNum) REFERENCES Usuario(numero) ON DELETE CASCADE,
    FOREIGN KEY (lobbyId)     REFERENCES Lobby(id)        ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_match__estado_createdAt ON Match(estado, createdAtMs DESC);

-- =========================================================
-- 👥 MATCH PARTICIPANT (jugadores dentro de un match)
-- Entity: MatchParticipantEntity (PK compuesta matchId+usuarioNum)
-- =========================================================
CREATE TABLE IF NOT EXISTS MatchParticipant (
    matchId     TEXT    NOT NULL,                                         -- FK -> Match.id
    usuarioNum  INTEGER NOT NULL,                                         -- FK -> Usuario.numero
    rol         TEXT,                                                     -- "host","player", etc.
    teamId      TEXT,                                                     -- etiqueta opcional
    joinedAtMs  INTEGER NOT NULL,
    leftAtMs    INTEGER,
    score       INTEGER NOT NULL DEFAULT 0 CHECK (score >= 0),
    PRIMARY KEY (matchId, usuarioNum),
    FOREIGN KEY (matchId)    REFERENCES Match(id)    ON DELETE CASCADE,
    FOREIGN KEY (usuarioNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_participant__match ON MatchParticipant(matchId);

-- =========================================================
-- 📜 MATCH EVENT (bitácora inmutable de eventos por match)
-- Entity: MatchEventEntity
-- UNIQUE (matchId, seq) asegura orden total por partida
-- =========================================================
CREATE TABLE IF NOT EXISTS MatchEvent (
    id           TEXT PRIMARY KEY,                                         -- UUID/ULID
    matchId      TEXT    NOT NULL,                                         -- FK -> Match.id
    seq          INTEGER NOT NULL,                                         -- secuencia por match
    type         TEXT    NOT NULL,                                         -- "MOVE","BET","ROLL",...
    actorNum     INTEGER NOT NULL,                                         -- FK -> Usuario.numero
    payloadJson  TEXT,                                                     -- JSON (sanitizado)
    createdAtMs  INTEGER NOT NULL,
    FOREIGN KEY (matchId) REFERENCES Match(id)         ON DELETE CASCADE,
    FOREIGN KEY (actorNum) REFERENCES Usuario(numero)  ON DELETE CASCADE,
    UNIQUE (matchId, seq)
);
CREATE INDEX IF NOT EXISTS idx_event__match_seq ON MatchEvent(matchId, seq);

-- =========================================================
-- 💬 MESSAGE (chat de match)
-- Entity: MessageEntity (coincide con tus DAOs; columnas exactas)
-- =========================================================
CREATE TABLE IF NOT EXISTS Message (
    id           TEXT PRIMARY KEY,                                         -- UUID/ULID
    matchId      TEXT    NOT NULL,                                         -- FK -> Match.id
    senderNum    INTEGER NOT NULL,                                         -- FK -> Usuario.numero
    text         TEXT    NOT NULL CHECK (length(trim(text)) > 0),
    createdAtMs  INTEGER NOT NULL,
    FOREIGN KEY (matchId)   REFERENCES Match(id)          ON DELETE CASCADE,
    FOREIGN KEY (senderNum) REFERENCES Usuario(numero)    ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_message__match_time ON Message(matchId, createdAtMs);

-- =========================================================
-- 🧮 MATCH SCORE (marcador final por match/usuario)
-- Entity: MatchScoreEntity (PK compuesta matchId+usuarioNum)
-- =========================================================
CREATE TABLE IF NOT EXISTS MatchScore (
    matchId     TEXT    NOT NULL,                                         -- FK -> Match.id
    usuarioNum  INTEGER NOT NULL,                                         -- FK -> Usuario.numero
    score       INTEGER NOT NULL CHECK (score >= 0),
    PRIMARY KEY (matchId, usuarioNum),
    FOREIGN KEY (matchId)    REFERENCES Match(id)       ON DELETE CASCADE,
    FOREIGN KEY (usuarioNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);

-- =========================================================
-- 💰 MONEDERO (saldo por usuario)
-- Entity: MonederoEntity (alineado a tu dominio: id, usuarioNumero, saldo)
-- =========================================================
CREATE TABLE IF NOT EXISTS Monedero (
    id            TEXT PRIMARY KEY,                                       -- p.ej. "wallet_<usuarioNumero>"
    usuarioNumero INTEGER NOT NULL,                                       -- FK -> Usuario.numero
    saldo         INTEGER NOT NULL DEFAULT 0 CHECK (saldo >= 0),
    FOREIGN KEY (usuarioNumero) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_monedero__usuario ON Monedero(usuarioNumero);

-- =========================================================
-- 🗓️ EVENTO (misiones/retos/torneos)
-- Tabla en minúscula: "evento" (coincide con tu IEventoDao)
-- Entity: EventoEntity
-- =========================================================
CREATE TABLE IF NOT EXISTS evento (
    id           TEXT    PRIMARY KEY,
    nombre       TEXT    NOT NULL,
    descripcion  TEXT    NOT NULL,
    fechaInicio  INTEGER NOT NULL,                                        -- epoch millis
    fechaFin     INTEGER NOT NULL,                                        -- epoch millis
    completado   INTEGER NOT NULL DEFAULT 0                               -- Boolean (0/1)
);
CREATE INDEX IF NOT EXISTS idx_evento__inicio ON evento(fechaInicio);

-- =========================================================
-- 📤 OUTBOX (operaciones idempotentes pendientes de sincronizar)
-- Entity: OutboxEntity
-- =========================================================
CREATE TABLE IF NOT EXISTS Outbox (
    operationId  TEXT PRIMARY KEY,                                        -- UUID del intento
    entityType   TEXT    NOT NULL,
    entityId     TEXT    NOT NULL,
    op           TEXT    NOT NULL CHECK (op IN ('CREATE','UPDATE','DELETE')),
    payloadJson  TEXT    NOT NULL,
    attempt      INTEGER NOT NULL DEFAULT 0,
    lastError    TEXT,
    createdAtMs  INTEGER NOT NULL,
    updatedAtMs  INTEGER NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_outbox__entity ON Outbox(entityType, entityId);

-- =========================================================
-- 🪦 TOMBSTONE (borrados lógicos para replicación)
-- Entity: TombstoneEntity (PK compuesta tableName+entityId)
-- =========================================================
CREATE TABLE IF NOT EXISTS Tombstone (
    tableName    TEXT    NOT NULL,
    entityId     TEXT    NOT NULL,
    deletedAtMs  INTEGER NOT NULL,
    PRIMARY KEY (tableName, entityId)
);

COMMIT;
