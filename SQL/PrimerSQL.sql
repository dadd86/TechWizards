/* =====================================================================
   📦 TechWizards – Esquema SQLite (V3 Hardened)
   Objetivo:
     - Persistencia local segura para juego de azar con dado.
     - Integración con Room + RxJava3 (y DataStore para settings).
   Seguridad:
     - FKs estrictas, CHECKs de dominio, json_valid() donde aplica.
     - PRAGMAs para durabilidad/recuperación y borrado seguro.
   Rendimiento:
     - WAL + índices en todas las FKs hijas y consultas frecuentes.
   Migraciones:
     - Usa Room Migration para ALTER/RENAME si cambian nombres.
   ===================================================================== */

-- ===========================
-- 🔧 PRAGMAS – Durabilidad & Seguridad
-- ===========================
/*
  foreign_keys     : obliga integridad referencial a nivel SQLite (Room también).
  journal_mode=WAL : concurrencia lecturas/escrituras y recuperación rápida.
  synchronous=FULL : máxima durabilidad (puedes bajar a NORMAL si necesitas más rendimiento).
  secure_delete    : sobre-escribe páginas borradas (reduce recuperación de datos borrados).
  temp_store=MEMORY: objetos temporales en RAM (menos E/S).
  mmap_size        : activa mapeo de memoria (optimiza lecturas grandes).
*/
PRAGMA foreign_keys = ON;
PRAGMA journal_mode = WAL;
PRAGMA synchronous = FULL;
PRAGMA secure_delete = ON;
PRAGMA temp_store = MEMORY;
PRAGMA mmap_size = 134217728;

BEGIN TRANSACTION;

-- =====================================================================
-- 👤 USUARIO
--   Modelo de identidad local del jugador.
--   Room: mapea booleanos a INTEGER (0/1) vía TypeConverter.
--   Notas:
--     - 'usuario' es el "alias" visible (3..50).
--     - 'fechaAlta' es epoch millis (>0).
-- =====================================================================
CREATE TABLE IF NOT EXISTS Usuario (
    numero       INTEGER PRIMARY KEY,
    usuario      TEXT    NOT NULL CHECK (length(trim(usuario)) BETWEEN 3 AND 50),
    fechaAlta    INTEGER NOT NULL CHECK (fechaAlta > 0),
    monedas      INTEGER NOT NULL DEFAULT 0 CHECK (monedas >= 0),
    gano         INTEGER NOT NULL DEFAULT 0 CHECK (gano IN (0,1)),
    firebaseUid  TEXT UNIQUE
);
/* Room Sugerido:
   @ColumnInfo(name="usuario") val alias: String
   @ColumnInfo(name="fechaAlta") val fechaAltaMs: Long
   @ColumnInfo(name="gano") val ganoUltimaPartida: Boolean
*/

-- =====================================================================
-- 🏷️ IDMAP – Correlación Local↔Remoto
--   Uso: desambiguar ids de Firestore/REST frente a ids locales.
--   Unicidad bidireccional: (localTable, localId) y (remoteCollection, remoteId)
-- =====================================================================
CREATE TABLE IF NOT EXISTS IdMap (
    localTable        TEXT NOT NULL CHECK (length(localTable) > 0),
    localId           TEXT NOT NULL,
    remoteCollection  TEXT NOT NULL,
    remoteId          TEXT NOT NULL,
    PRIMARY KEY (localTable, localId),
    UNIQUE (remoteCollection, remoteId)
);

-- =====================================================================
-- 🏠 LOBBY – Sala previa a partida
--   Dominio: estado ∈ {PENDING, FULL, CLOSED}
--   FKs: creadorNum → Usuario.numero (CASCADE)
--   Índices: (estado, createdAtMs DESC) + idx FK (abajo)
-- =====================================================================
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
/* Room Sugerido:
   @ColumnInfo(name="creadorNum") val creadorNumero: Long
   TypeConverter<LobbyEstado↔String>
*/

-- =====================================================================
-- 🎮 MATCH – Instancia de partida
--   Dominio: estado ∈ {PENDING, ACTIVE, FINISHED, CANCELLED}
--   FKs: createdByNum → Usuario.numero (CASCADE)
--        lobbyId → Lobby.id (SET NULL) – match puede no venir de lobby
-- =====================================================================
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
/* Room Sugerido:
   @ColumnInfo(name="createdByNum") val createdByNumero: Long
   TypeConverter<MatchEstado↔String>
*/

-- =====================================================================
-- 👥 MATCH PARTICIPANT – Jugadores en un match
--   PK compuesta: (matchId, usuarioNum) → ya indexa ambas FKs.
--   'rol' con CHECK para evitar valores arbitrarios.
-- =====================================================================
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
/* Room Sugerido:
   @ColumnInfo(name="usuarioNum") val usuarioNumero: Long
*/

-- =====================================================================
-- 📜 MATCH EVENT – Bitácora inmutable
--   Unicidad: (matchId, seq) asegura orden total.
--   payloadJson validado con json_valid() para evitar basura.
-- =====================================================================
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
/* Room Sugerido:
   @ColumnInfo(name="actorNum") val actorNumero: Long
*/

-- =====================================================================
-- 💬 MESSAGE – Chat del match
--   CHECK de longitud (1..500) y trim().
--   Índice (matchId, createdAtMs) para listados cronológicos.
-- =====================================================================
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
/* Room Sugerido:
   @ColumnInfo(name="senderNum") val senderNumero: Long
*/

-- =====================================================================
-- 💰 MONEDERO – Saldo por usuario
--   CHECK saldo ≥ 0 para evitar negativos accidentales.
-- =====================================================================
CREATE TABLE IF NOT EXISTS Monedero (
    id            TEXT PRIMARY KEY,
    usuarioNumero INTEGER NOT NULL,
    saldo         INTEGER NOT NULL DEFAULT 0 CHECK (saldo >= 0),
    FOREIGN KEY (usuarioNumero) REFERENCES Usuario(numero) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS idx_monedero_usuario ON Monedero(usuarioNumero);

-- =====================================================================
-- 🧮 MATCH SCORE – Puntaje final por jugador/match
--   PK compuesta (matchId, usuarioNum) evita duplicados.
-- =====================================================================
CREATE TABLE IF NOT EXISTS MatchScore (
    matchId     TEXT NOT NULL,
    usuarioNum  INTEGER NOT NULL,
    score       INTEGER NOT NULL CHECK (score >= 0),
    PRIMARY KEY (matchId, usuarioNum),
    FOREIGN KEY (matchId) REFERENCES Match(id) ON DELETE CASCADE,
    FOREIGN KEY (usuarioNum) REFERENCES Usuario(numero) ON DELETE CASCADE
);
/* Room Sugerido:
   @ColumnInfo(name="usuarioNum") val usuarioNumero: Long
*/

-- =====================================================================
-- 🗓️ EVENTO – Misiones/retos/torneos
--   Booleans como INTEGER (0/1) – usa TypeConverter en Room.
-- =====================================================================
CREATE TABLE IF NOT EXISTS evento (
    id           TEXT PRIMARY KEY,
    nombre       TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
    descripcion  TEXT NOT NULL CHECK (length(trim(descripcion)) > 0),
    fechaInicio  INTEGER NOT NULL,
    fechaFin     INTEGER NOT NULL,
    completado   INTEGER NOT NULL DEFAULT 0 CHECK (completado IN (0,1))
);
CREATE INDEX IF NOT EXISTS idx_evento_inicio ON evento(fechaInicio);

-- =====================================================================
-- 📤 OUTBOX – Operaciones idempotentes para sync
--   payloadJson validado; attempt ≥ 0; op restringido.
--   Índice por (entityType, entityId) para búsquedas rápidas.
-- =====================================================================
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

-- =====================================================================
-- 🪦 TOMBSTONE – Borrado lógico para replicación
--   PK compuesta: (tableName, entityId).
-- =====================================================================
CREATE TABLE IF NOT EXISTS Tombstone (
    tableName    TEXT NOT NULL,
    entityId     TEXT NOT NULL,
    deletedAtMs  INTEGER NOT NULL,
    PRIMARY KEY (tableName, entityId)
);

-- =====================================================================
-- 🧩 PARTIDA – Historial de tiradas del dado (singleplayer)
--   Dominio: resultado ∈ {'GANADO','PERDIDO'} (si agregas EMPATE, adapta CHECK)
--   'nombreJugador' auxiliar para UI; ignorable en dominio.
-- =====================================================================
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
/* Room Sugerido:
   @ColumnInfo(name="cambioMonedas") val deltaMonedas: Int
   TypeConverter<Resultado↔String> (‘GANADO’/‘PERDIDO’)
*/
-- =====================================================================
-- 📍 VICTORY LOCATION – Ubicaciones al ganar
--   Persistencia local para capturar la ubicación tras una victoria.
--   Restricciones: latitud [-90,90], longitud [-180,180], exactitud ≥ 0.
--   Integrado con Room (VictoryLocationEntity) y DAO de solo inserción/consulta.
-- =====================================================================
CREATE TABLE IF NOT EXISTS victory_location (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    latitude        REAL    NOT NULL CHECK (latitude BETWEEN -90 AND 90),
    longitude       REAL    NOT NULL CHECK (longitude BETWEEN -180 AND 180),
    accuracyMetres  REAL    CHECK (accuracyMetres IS NULL OR accuracyMetres >= 0),
    capturedAtMs    INTEGER NOT NULL CHECK (capturedAtMs > 0)
);
CREATE INDEX IF NOT EXISTS idx_victory_location_capturedAt ON victory_location(capturedAtMs DESC);

-- =====================================================================
-- 📚 Índices extra para columnas FK (evitan table scans en updates)
--   (algunas ya cubiertas por PK compuestas o índices previos)
-- =====================================================================
CREATE INDEX IF NOT EXISTS idx_lobby_creadorNum            ON Lobby(creadorNum);
CREATE INDEX IF NOT EXISTS idx_match_createdByNum          ON Match(createdByNum);
CREATE INDEX IF NOT EXISTS idx_match_lobbyId               ON Match(lobbyId);
CREATE INDEX IF NOT EXISTS idx_message_senderNum           ON Message(senderNum);
CREATE INDEX IF NOT EXISTS idx_monedero_usuarioNumero      ON Monedero(usuarioNumero);
/* No se crean para MatchScore/Participant porque sus PK compuestas
   ya indexan las columnas. */

COMMIT;

/* ========================== ✅ Notas finales ==========================
- Booleans en Room: usa @TypeConverter(Int↔Boolean).
- Enums (LobbyEstado, MatchEstado, Resultado): @TypeConverter(String↔Enum).
- Logs: no registres payloadJson completo; redacta PII si existiera.
- Backups: considera cifrado a nivel OS (EncryptedFile) o SQLCipher si es requisito.
- DataStore: usa DataStore Preferences para GameSettings (no requiere tabla).
====================================================================== */
