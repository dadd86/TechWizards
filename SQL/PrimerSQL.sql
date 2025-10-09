-- =========================================================
-- SQLite: robustez / durabilidad
-- =========================================================
PRAGMA foreign_keys = ON;   -- Habilita y hace cumplir claves foráneas
PRAGMA journal_mode = WAL;  -- Write-Ahead Logging: mejor concurrencia y recuperación
PRAGMA synchronous = FULL;  -- Durabilidad máxima; evalúa 'NORMAL' si priorizas rendimiento

BEGIN TRANSACTION;

-- =========================================================
-- USUARIO: identidad local del jugador + vínculo con Firebase Auth
-- NOTA: la tabla Usuario ya existe (numero PK, usuario UNIQUE, fechaAlta, monedas, gano).
--       Aquí añadimos firebaseUid para vincular identidad remota (Auth).
-- =========================================================
-- firebaseUid: UID de Firebase Auth (único por usuario remoto). Puede ser null si aún no vinculó cuenta.
ALTER TABLE Usuario ADD COLUMN firebaseUid TEXT UNIQUE;
-- (opcional) Validación de tamaño de UID (Firebase usa ~28 chars en Base64URL; dejamos rango amplio):
-- ALTER TABLE Usuario ADD COLUMN firebaseUid TEXT UNIQUE CHECK (length(firebaseUid) BETWEEN 20 AND 50);


-- =========================================================
-- LOBBY: sala previa a la partida para invitar/unir jugadores por código
-- =========================================================
/*
  id          : Identificador global (UUID/ULID) generado localmente (offline-first).
  codigo      : Código corto para compartir (único).
  modo        : Modo de juego ("1v1", "duos", etc.).
  estado      : Estado del lobby: PENDING (abierto), FULL (capacidad completa), CLOSED (cerrado).
  creadorNum  : Usuario creador (FK a Usuario.numero).
  createdAtMs : Epoch ms de creación (cliente o servidor).
*/
CREATE TABLE IF NOT EXISTS Lobby (
    id           TEXT PRIMARY KEY,                        -- UUID/ULID
    codigo       TEXT UNIQUE,                             -- Código de invitación (p. ej., 6-10 chars)
    modo         TEXT NOT NULL,                           -- Modo de juego ("1v1", "duos", ...)
    estado       TEXT NOT NULL CHECK (estado IN ('PENDING','FULL','CLOSED')),
    creadorNum   INTEGER NOT NULL,                        -- FK -> Usuario.numero
    createdAtMs  INTEGER NOT NULL,                        -- Epoch ms
    FOREIGN KEY (creadorNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_lobby__estado ON Lobby(estado);

-- =========================================================
-- MATCH: partida multijugador (instancia de juego)
-- =========================================================
/*
  id            : Identificador global (UUID/ULID).
  lobbyId       : Relación con Lobby (si se originó en uno).
  modo          : Modo de juego (consistente con Lobby.modo).
  estado        : PENDING (creada), ACTIVE (en juego), FINISHED (finalizada), CANCELLED (cancelada).
  createdByNum  : Usuario host (FK).
  createdAtMs   : Epoch ms de creación.
  startedAtMs   : Epoch ms de inicio (nullable).
  finishedAtMs  : Epoch ms de fin (nullable).
*/
CREATE TABLE IF NOT EXISTS Match (
    id           TEXT PRIMARY KEY,                        -- UUID/ULID
    lobbyId      TEXT,                                    -- FK opcional -> Lobby.id
    modo         TEXT NOT NULL,
    estado       TEXT NOT NULL CHECK (estado IN ('PENDING','ACTIVE','FINISHED','CANCELLED')),
    createdByNum INTEGER NOT NULL,                        -- FK -> Usuario.numero (host)
    createdAtMs  INTEGER NOT NULL,
    startedAtMs  INTEGER,
    finishedAtMs INTEGER,
    FOREIGN KEY (createdByNum) REFERENCES Usuario(numero) ON DELETE CASCADE,
    FOREIGN KEY (lobbyId) REFERENCES Lobby(id) ON DELETE SET NULL
);
CREATE INDEX IF NOT EXISTS idx_match__estado_createdAt ON Match(estado, createdAtMs DESC);

-- =========================================================
-- MATCH PARTICIPANT: jugadores asociados a un match
-- =========================================================
/*
  matchId     : Partida a la que pertenece.
  usuarioNum  : Usuario participante (FK).
  rol         : "host" o "player" (expandible si agregas roles).
  teamId      : Equipo (etiqueta libre; normalízalo en tabla Team si crece).
  joinedAtMs  : Epoch ms de ingreso al match.
  leftAtMs    : Epoch ms de salida (nullable).
  score       : Puntuación del jugador dentro del match (acumulada o final).
*/
CREATE TABLE IF NOT EXISTS MatchParticipant (
    matchId      TEXT    NOT NULL,                        -- FK -> Match.id
    usuarioNum   INTEGER NOT NULL,                        -- FK -> Usuario.numero
    rol          TEXT,                                    -- "host","player"
    teamId       TEXT,                                    -- etiqueta opcional de equipo
    joinedAtMs   INTEGER NOT NULL,
    leftAtMs     INTEGER,
    score        INTEGER NOT NULL DEFAULT 0 CHECK (score >= 0),
    PRIMARY KEY (matchId, usuarioNum),
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (usuarioNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_participant__match ON MatchParticipant(matchId);

-- =========================================================
-- MATCH EVENT: log inmutable (append-only) de acciones del match
-- =========================================================
/*
  id          : Identificador global (UUID/ULID) del evento.
  matchId     : Partida a la que pertenece.
  seq         : Secuencia única por match (asegura orden total).
  type        : Tipo de evento (p. ej., "MOVE","BET","ROLL", ...).
  actorNum    : Usuario que ejecuta la acción (FK).
  payloadJson : Datos del evento en JSON (parámetros, resultado, etc.).
  createdAtMs : Epoch ms de creación (cliente o servidor).
*/
CREATE TABLE IF NOT EXISTS MatchEvent (
    id           TEXT PRIMARY KEY,                        -- UUID/ULID
    matchId      TEXT    NOT NULL,                        -- FK -> Match.id
    seq          INTEGER NOT NULL,                        -- Secuencia por match
    type         TEXT    NOT NULL,                        -- Nombre del evento
    actorNum     INTEGER NOT NULL,                        -- FK -> Usuario.numero
    payloadJson  TEXT,                                    -- JSON con parámetros/resultados
    createdAtMs  INTEGER NOT NULL,                        -- Epoch ms
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (actorNum) REFERENCES Usuario(numero) ON DELETE CASCADE,
    UNIQUE (matchId, seq)                                 -- Garantiza orden único por match
);
CREATE INDEX IF NOT EXISTS idx_event__match_seq ON MatchEvent(matchId, seq);

-- =========================================================
-- MESSAGE: chat dentro del match (para UX y moderación)
-- =========================================================
/*
  id          : Identificador global (UUID/ULID) del mensaje.
  matchId     : Partida a la que pertenece el mensaje.
  senderNum   : Usuario emisor (FK).
  text        : Contenido del mensaje (sanitizado por app; no PII sensible).
  createdAtMs : Epoch ms de envío.
*/
CREATE TABLE IF NOT EXISTS Message (
    id           TEXT PRIMARY KEY,                        -- UUID/ULID
    matchId      TEXT NOT NULL,                           -- FK -> Match.id
    senderNum    INTEGER NOT NULL,                        -- FK -> Usuario.numero
    text         TEXT NOT NULL CHECK (length(trim(text)) > 0),
    createdAtMs  INTEGER NOT NULL,
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (senderNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_message__match_time ON Message(matchId, createdAtMs);

-- =========================================================
-- MATCH SCORE: marcador final por partida (consulta rápida post-match)
-- =========================================================
/*
  matchId    : Partida a la que pertenece el registro.
  usuarioNum : Usuario (FK).
  score      : Marcador final (acumulado) del jugador en ese match.
*/
CREATE TABLE IF NOT EXISTS MatchScore (
    matchId     TEXT    NOT NULL,                        -- FK -> Match.id
    usuarioNum  INTEGER NOT NULL,                        -- FK -> Usuario.numero
    score       INTEGER NOT NULL CHECK (score >= 0),
    PRIMARY KEY (matchId, usuarioNum),
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (usuarioNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);

-- =========================================================
-- OUTBOX: operaciones pendientes de sincronizar con Firebase (idempotentes)
-- =========================================================
/*
  operationId  : Identificador del intento (UUID) — asegura idempotencia.
  entityType   : Tipo de entidad ("Match","MatchEvent","Message", ...).
  entityId     : ID local (UUID/PK) de la entidad a sincronizar.
  op           : Operación a ejecutar en remoto: CREATE|UPDATE|DELETE.
  payloadJson  : Snapshot/Delta para la operación (contrato con capa remota).
  attempt      : Cantidad de reintentos realizados.
  lastError    : Último error textual (debug/telemetría).
  createdAtMs  : Epoch ms de encolado.
  updatedAtMs  : Epoch ms del último intento/actualización.
*/
CREATE TABLE IF NOT EXISTS Outbox (
    operationId  TEXT PRIMARY KEY,                        -- UUID del intento
    entityType   TEXT NOT NULL,
    entityId     TEXT NOT NULL,
    op           TEXT NOT NULL CHECK (op IN ('CREATE','UPDATE','DELETE')),
    payloadJson  TEXT NOT NULL,
    attempt      INTEGER NOT NULL DEFAULT 0,
    lastError    TEXT,
    createdAtMs  INTEGER NOT NULL,
    updatedAtMs  INTEGER NOT NULL
);

-- =========================================================
-- IDMAP: correspondencia ID local ↔ ID remoto (Firestore)
-- =========================================================
/*
  localTable       : Nombre lógico de la tabla local (p. ej., 'Match','MatchEvent').
  localId          : ID local (UUID/PK).
  remoteCollection : Colección remota (Firestore).
  remoteId         : Document ID remoto (Firestore).
*/
CREATE TABLE IF NOT EXISTS IdMap (
    localTable        TEXT NOT NULL,
    localId           TEXT NOT NULL,
    remoteCollection  TEXT NOT NULL,
    remoteId          TEXT NOT NULL,
    PRIMARY KEY (localTable, localId),
    UNIQUE (remoteCollection, remoteId)
);


-- =========================================================
-- TOMBSTONE: borrados lógicos para replicación confiable
-- =========================================================
/*
  tableName  : Tabla local a la que pertenece el registro eliminado.
  entityId   : ID de la entidad eliminada (PK/UUID local).
  deletedAtMs: Epoch ms del borrado lógico (para propagación a remoto y GC).
*/
CREATE TABLE IF NOT EXISTS Tombstone (
    tableName    TEXT NOT NULL,
    entityId     TEXT NOT NULL,
    deletedAtMs  INTEGER NOT NULL,
    PRIMARY KEY (tableName, entityId)
);

COMMIT;
