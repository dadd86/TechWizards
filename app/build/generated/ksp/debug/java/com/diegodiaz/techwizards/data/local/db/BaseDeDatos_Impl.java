package com.diegodiaz.techwizards.data.local.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.diegodiaz.techwizards.data.local.dao.IEventoDao;
import com.diegodiaz.techwizards.data.local.dao.IEventoDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IIdMapDao;
import com.diegodiaz.techwizards.data.local.dao.IIdMapDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.ILobbyDao;
import com.diegodiaz.techwizards.data.local.dao.ILobbyDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IMatchDao;
import com.diegodiaz.techwizards.data.local.dao.IMatchDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IMatchEventDao;
import com.diegodiaz.techwizards.data.local.dao.IMatchEventDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IMatchParticipantDao;
import com.diegodiaz.techwizards.data.local.dao.IMatchParticipantDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IMatchScoreDao;
import com.diegodiaz.techwizards.data.local.dao.IMatchScoreDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IMessageDao;
import com.diegodiaz.techwizards.data.local.dao.IMessageDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IMonederoDao;
import com.diegodiaz.techwizards.data.local.dao.IMonederoDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IOutboxDao;
import com.diegodiaz.techwizards.data.local.dao.IOutboxDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao;
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.ITombstoneDao;
import com.diegodiaz.techwizards.data.local.dao.ITombstoneDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IUsuarioDao;
import com.diegodiaz.techwizards.data.local.dao.IUsuarioDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BaseDeDatos_Impl extends BaseDeDatos {
  private volatile IUsuarioDao _iUsuarioDao;

  private volatile IMonederoDao _iMonederoDao;

  private volatile IPartidaDao _iPartidaDao;

  private volatile IEventoDao _iEventoDao;

  private volatile ILobbyDao _iLobbyDao;

  private volatile IMatchDao _iMatchDao;

  private volatile IMatchEventDao _iMatchEventDao;

  private volatile IMatchParticipantDao _iMatchParticipantDao;

  private volatile IMatchScoreDao _iMatchScoreDao;

  private volatile IMessageDao _iMessageDao;

  private volatile IOutboxDao _iOutboxDao;

  private volatile IIdMapDao _iIdMapDao;

  private volatile ITombstoneDao _iTombstoneDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `usuario` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `monedas` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `monedero` (`id` TEXT NOT NULL, `usuarioId` TEXT NOT NULL, `saldo` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_monedero_usuarioId` ON `monedero` (`usuarioId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `partida` (`id` TEXT NOT NULL, `usuarioId` TEXT NOT NULL, `fecha` INTEGER NOT NULL, `resultado` TEXT NOT NULL, `cambioMonedas` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`usuarioId`) REFERENCES `usuario`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_partida_usuarioId` ON `partida` (`usuarioId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_partida_fecha` ON `partida` (`fecha`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `evento` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `fechaInicio` INTEGER NOT NULL, `fechaFin` INTEGER NOT NULL, `completado` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `lobby` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `capacidad` INTEGER NOT NULL, `abierta` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_lobby_nombre` ON `lobby` (`nombre`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `match` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `lobbyId` INTEGER, `status` TEXT NOT NULL, `inicioEn` INTEGER NOT NULL, `finEn` INTEGER, FOREIGN KEY(`lobbyId`) REFERENCES `lobby`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_match_lobbyId` ON `match` (`lobbyId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `match_event` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `matchId` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `actorParticipantId` INTEGER, `payload` TEXT, FOREIGN KEY(`matchId`) REFERENCES `match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`actorParticipantId`) REFERENCES `match_participant`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_match_event_matchId` ON `match_event` (`matchId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_match_event_actorParticipantId` ON `match_event` (`actorParticipantId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `match_participant` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `matchId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `apodo` TEXT, `joinedAt` INTEGER NOT NULL, `esGanador` INTEGER NOT NULL, FOREIGN KEY(`matchId`) REFERENCES `match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`userId`) REFERENCES `usuario`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_match_participant_matchId` ON `match_participant` (`matchId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_match_participant_userId` ON `match_participant` (`userId`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_match_participant_matchId_userId` ON `match_participant` (`matchId`, `userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `match_score` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `matchId` INTEGER NOT NULL, `participantId` INTEGER NOT NULL, `puntos` INTEGER NOT NULL, FOREIGN KEY(`matchId`) REFERENCES `match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`participantId`) REFERENCES `match_participant`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_match_score_matchId` ON `match_score` (`matchId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_match_score_participantId` ON `match_score` (`participantId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `message` (`id` TEXT NOT NULL, `matchId` TEXT NOT NULL, `remitenteId` TEXT NOT NULL, `contenido` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`matchId`) REFERENCES `match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`remitenteId`) REFERENCES `usuario`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_message_matchId` ON `message` (`matchId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_message_remitenteId` ON `message` (`remitenteId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `outbox` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `tipo` TEXT NOT NULL, `payload` TEXT NOT NULL, `creadoEn` INTEGER NOT NULL, `entregado` INTEGER NOT NULL, `reintentos` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_tipo` ON `outbox` (`tipo`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_entregado` ON `outbox` (`entregado`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_outbox_creadoEn` ON `outbox` (`creadoEn`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `id_map` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `localId` TEXT NOT NULL, `remoteId` TEXT, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tombstone` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `type` TEXT NOT NULL, `deletedId` TEXT NOT NULL, `deletedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tombstone_type_deletedId` ON `tombstone` (`type`, `deletedId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tombstone_deletedAt` ON `tombstone` (`deletedAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e15831b177d96b3760af55850a299224')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `usuario`");
        db.execSQL("DROP TABLE IF EXISTS `monedero`");
        db.execSQL("DROP TABLE IF EXISTS `partida`");
        db.execSQL("DROP TABLE IF EXISTS `evento`");
        db.execSQL("DROP TABLE IF EXISTS `lobby`");
        db.execSQL("DROP TABLE IF EXISTS `match`");
        db.execSQL("DROP TABLE IF EXISTS `match_event`");
        db.execSQL("DROP TABLE IF EXISTS `match_participant`");
        db.execSQL("DROP TABLE IF EXISTS `match_score`");
        db.execSQL("DROP TABLE IF EXISTS `message`");
        db.execSQL("DROP TABLE IF EXISTS `outbox`");
        db.execSQL("DROP TABLE IF EXISTS `id_map`");
        db.execSQL("DROP TABLE IF EXISTS `tombstone`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsUsuario = new HashMap<String, TableInfo.Column>(3);
        _columnsUsuario.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuario.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuario.put("monedas", new TableInfo.Column("monedas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsuario = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsuario = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsuario = new TableInfo("usuario", _columnsUsuario, _foreignKeysUsuario, _indicesUsuario);
        final TableInfo _existingUsuario = TableInfo.read(db, "usuario");
        if (!_infoUsuario.equals(_existingUsuario)) {
          return new RoomOpenHelper.ValidationResult(false, "usuario(com.diegodiaz.techwizards.data.local.entity.UsuarioEntity).\n"
                  + " Expected:\n" + _infoUsuario + "\n"
                  + " Found:\n" + _existingUsuario);
        }
        final HashMap<String, TableInfo.Column> _columnsMonedero = new HashMap<String, TableInfo.Column>(3);
        _columnsMonedero.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonedero.put("usuarioId", new TableInfo.Column("usuarioId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonedero.put("saldo", new TableInfo.Column("saldo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMonedero = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMonedero = new HashSet<TableInfo.Index>(1);
        _indicesMonedero.add(new TableInfo.Index("index_monedero_usuarioId", true, Arrays.asList("usuarioId"), Arrays.asList("ASC")));
        final TableInfo _infoMonedero = new TableInfo("monedero", _columnsMonedero, _foreignKeysMonedero, _indicesMonedero);
        final TableInfo _existingMonedero = TableInfo.read(db, "monedero");
        if (!_infoMonedero.equals(_existingMonedero)) {
          return new RoomOpenHelper.ValidationResult(false, "monedero(com.diegodiaz.techwizards.data.local.entity.MonederoEntity).\n"
                  + " Expected:\n" + _infoMonedero + "\n"
                  + " Found:\n" + _existingMonedero);
        }
        final HashMap<String, TableInfo.Column> _columnsPartida = new HashMap<String, TableInfo.Column>(5);
        _columnsPartida.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("usuarioId", new TableInfo.Column("usuarioId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("fecha", new TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("resultado", new TableInfo.Column("resultado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("cambioMonedas", new TableInfo.Column("cambioMonedas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPartida = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPartida.add(new TableInfo.ForeignKey("usuario", "CASCADE", "NO ACTION", Arrays.asList("usuarioId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPartida = new HashSet<TableInfo.Index>(2);
        _indicesPartida.add(new TableInfo.Index("index_partida_usuarioId", false, Arrays.asList("usuarioId"), Arrays.asList("ASC")));
        _indicesPartida.add(new TableInfo.Index("index_partida_fecha", false, Arrays.asList("fecha"), Arrays.asList("ASC")));
        final TableInfo _infoPartida = new TableInfo("partida", _columnsPartida, _foreignKeysPartida, _indicesPartida);
        final TableInfo _existingPartida = TableInfo.read(db, "partida");
        if (!_infoPartida.equals(_existingPartida)) {
          return new RoomOpenHelper.ValidationResult(false, "partida(com.diegodiaz.techwizards.data.local.entity.PartidaEntity).\n"
                  + " Expected:\n" + _infoPartida + "\n"
                  + " Found:\n" + _existingPartida);
        }
        final HashMap<String, TableInfo.Column> _columnsEvento = new HashMap<String, TableInfo.Column>(6);
        _columnsEvento.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("descripcion", new TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("fechaInicio", new TableInfo.Column("fechaInicio", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("fechaFin", new TableInfo.Column("fechaFin", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEvento.put("completado", new TableInfo.Column("completado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEvento = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEvento = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEvento = new TableInfo("evento", _columnsEvento, _foreignKeysEvento, _indicesEvento);
        final TableInfo _existingEvento = TableInfo.read(db, "evento");
        if (!_infoEvento.equals(_existingEvento)) {
          return new RoomOpenHelper.ValidationResult(false, "evento(com.diegodiaz.techwizards.data.local.entity.EventoEntity).\n"
                  + " Expected:\n" + _infoEvento + "\n"
                  + " Found:\n" + _existingEvento);
        }
        final HashMap<String, TableInfo.Column> _columnsLobby = new HashMap<String, TableInfo.Column>(4);
        _columnsLobby.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("capacidad", new TableInfo.Column("capacidad", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("abierta", new TableInfo.Column("abierta", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLobby = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLobby = new HashSet<TableInfo.Index>(1);
        _indicesLobby.add(new TableInfo.Index("index_lobby_nombre", true, Arrays.asList("nombre"), Arrays.asList("ASC")));
        final TableInfo _infoLobby = new TableInfo("lobby", _columnsLobby, _foreignKeysLobby, _indicesLobby);
        final TableInfo _existingLobby = TableInfo.read(db, "lobby");
        if (!_infoLobby.equals(_existingLobby)) {
          return new RoomOpenHelper.ValidationResult(false, "lobby(com.diegodiaz.techwizards.data.local.entity.LobbyEntity).\n"
                  + " Expected:\n" + _infoLobby + "\n"
                  + " Found:\n" + _existingLobby);
        }
        final HashMap<String, TableInfo.Column> _columnsMatch = new HashMap<String, TableInfo.Column>(5);
        _columnsMatch.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("lobbyId", new TableInfo.Column("lobbyId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("inicioEn", new TableInfo.Column("inicioEn", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("finEn", new TableInfo.Column("finEn", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatch = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMatch.add(new TableInfo.ForeignKey("lobby", "SET NULL", "NO ACTION", Arrays.asList("lobbyId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMatch = new HashSet<TableInfo.Index>(1);
        _indicesMatch.add(new TableInfo.Index("index_match_lobbyId", false, Arrays.asList("lobbyId"), Arrays.asList("ASC")));
        final TableInfo _infoMatch = new TableInfo("match", _columnsMatch, _foreignKeysMatch, _indicesMatch);
        final TableInfo _existingMatch = TableInfo.read(db, "match");
        if (!_infoMatch.equals(_existingMatch)) {
          return new RoomOpenHelper.ValidationResult(false, "match(com.diegodiaz.techwizards.data.local.entity.MatchEntity).\n"
                  + " Expected:\n" + _infoMatch + "\n"
                  + " Found:\n" + _existingMatch);
        }
        final HashMap<String, TableInfo.Column> _columnsMatchEvent = new HashMap<String, TableInfo.Column>(6);
        _columnsMatchEvent.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("matchId", new TableInfo.Column("matchId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("tipo", new TableInfo.Column("tipo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("actorParticipantId", new TableInfo.Column("actorParticipantId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("payload", new TableInfo.Column("payload", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatchEvent = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMatchEvent.add(new TableInfo.ForeignKey("match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMatchEvent.add(new TableInfo.ForeignKey("match_participant", "SET NULL", "NO ACTION", Arrays.asList("actorParticipantId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMatchEvent = new HashSet<TableInfo.Index>(2);
        _indicesMatchEvent.add(new TableInfo.Index("index_match_event_matchId", false, Arrays.asList("matchId"), Arrays.asList("ASC")));
        _indicesMatchEvent.add(new TableInfo.Index("index_match_event_actorParticipantId", false, Arrays.asList("actorParticipantId"), Arrays.asList("ASC")));
        final TableInfo _infoMatchEvent = new TableInfo("match_event", _columnsMatchEvent, _foreignKeysMatchEvent, _indicesMatchEvent);
        final TableInfo _existingMatchEvent = TableInfo.read(db, "match_event");
        if (!_infoMatchEvent.equals(_existingMatchEvent)) {
          return new RoomOpenHelper.ValidationResult(false, "match_event(com.diegodiaz.techwizards.data.local.entity.MatchEventEntity).\n"
                  + " Expected:\n" + _infoMatchEvent + "\n"
                  + " Found:\n" + _existingMatchEvent);
        }
        final HashMap<String, TableInfo.Column> _columnsMatchParticipant = new HashMap<String, TableInfo.Column>(6);
        _columnsMatchParticipant.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("matchId", new TableInfo.Column("matchId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("apodo", new TableInfo.Column("apodo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("joinedAt", new TableInfo.Column("joinedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("esGanador", new TableInfo.Column("esGanador", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatchParticipant = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMatchParticipant.add(new TableInfo.ForeignKey("match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMatchParticipant.add(new TableInfo.ForeignKey("usuario", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMatchParticipant = new HashSet<TableInfo.Index>(3);
        _indicesMatchParticipant.add(new TableInfo.Index("index_match_participant_matchId", false, Arrays.asList("matchId"), Arrays.asList("ASC")));
        _indicesMatchParticipant.add(new TableInfo.Index("index_match_participant_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        _indicesMatchParticipant.add(new TableInfo.Index("index_match_participant_matchId_userId", true, Arrays.asList("matchId", "userId"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoMatchParticipant = new TableInfo("match_participant", _columnsMatchParticipant, _foreignKeysMatchParticipant, _indicesMatchParticipant);
        final TableInfo _existingMatchParticipant = TableInfo.read(db, "match_participant");
        if (!_infoMatchParticipant.equals(_existingMatchParticipant)) {
          return new RoomOpenHelper.ValidationResult(false, "match_participant(com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity).\n"
                  + " Expected:\n" + _infoMatchParticipant + "\n"
                  + " Found:\n" + _existingMatchParticipant);
        }
        final HashMap<String, TableInfo.Column> _columnsMatchScore = new HashMap<String, TableInfo.Column>(4);
        _columnsMatchScore.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchScore.put("matchId", new TableInfo.Column("matchId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchScore.put("participantId", new TableInfo.Column("participantId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchScore.put("puntos", new TableInfo.Column("puntos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatchScore = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMatchScore.add(new TableInfo.ForeignKey("match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMatchScore.add(new TableInfo.ForeignKey("match_participant", "CASCADE", "NO ACTION", Arrays.asList("participantId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMatchScore = new HashSet<TableInfo.Index>(2);
        _indicesMatchScore.add(new TableInfo.Index("index_match_score_matchId", false, Arrays.asList("matchId"), Arrays.asList("ASC")));
        _indicesMatchScore.add(new TableInfo.Index("index_match_score_participantId", false, Arrays.asList("participantId"), Arrays.asList("ASC")));
        final TableInfo _infoMatchScore = new TableInfo("match_score", _columnsMatchScore, _foreignKeysMatchScore, _indicesMatchScore);
        final TableInfo _existingMatchScore = TableInfo.read(db, "match_score");
        if (!_infoMatchScore.equals(_existingMatchScore)) {
          return new RoomOpenHelper.ValidationResult(false, "match_score(com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity).\n"
                  + " Expected:\n" + _infoMatchScore + "\n"
                  + " Found:\n" + _existingMatchScore);
        }
        final HashMap<String, TableInfo.Column> _columnsMessage = new HashMap<String, TableInfo.Column>(5);
        _columnsMessage.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("matchId", new TableInfo.Column("matchId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("remitenteId", new TableInfo.Column("remitenteId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("contenido", new TableInfo.Column("contenido", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessage = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMessage.add(new TableInfo.ForeignKey("match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMessage.add(new TableInfo.ForeignKey("usuario", "CASCADE", "NO ACTION", Arrays.asList("remitenteId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMessage = new HashSet<TableInfo.Index>(2);
        _indicesMessage.add(new TableInfo.Index("index_message_matchId", false, Arrays.asList("matchId"), Arrays.asList("ASC")));
        _indicesMessage.add(new TableInfo.Index("index_message_remitenteId", false, Arrays.asList("remitenteId"), Arrays.asList("ASC")));
        final TableInfo _infoMessage = new TableInfo("message", _columnsMessage, _foreignKeysMessage, _indicesMessage);
        final TableInfo _existingMessage = TableInfo.read(db, "message");
        if (!_infoMessage.equals(_existingMessage)) {
          return new RoomOpenHelper.ValidationResult(false, "message(com.diegodiaz.techwizards.data.local.entity.MessageEntity).\n"
                  + " Expected:\n" + _infoMessage + "\n"
                  + " Found:\n" + _existingMessage);
        }
        final HashMap<String, TableInfo.Column> _columnsOutbox = new HashMap<String, TableInfo.Column>(6);
        _columnsOutbox.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("tipo", new TableInfo.Column("tipo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("payload", new TableInfo.Column("payload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("creadoEn", new TableInfo.Column("creadoEn", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("entregado", new TableInfo.Column("entregado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("reintentos", new TableInfo.Column("reintentos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOutbox = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOutbox = new HashSet<TableInfo.Index>(3);
        _indicesOutbox.add(new TableInfo.Index("index_outbox_tipo", false, Arrays.asList("tipo"), Arrays.asList("ASC")));
        _indicesOutbox.add(new TableInfo.Index("index_outbox_entregado", false, Arrays.asList("entregado"), Arrays.asList("ASC")));
        _indicesOutbox.add(new TableInfo.Index("index_outbox_creadoEn", false, Arrays.asList("creadoEn"), Arrays.asList("ASC")));
        final TableInfo _infoOutbox = new TableInfo("outbox", _columnsOutbox, _foreignKeysOutbox, _indicesOutbox);
        final TableInfo _existingOutbox = TableInfo.read(db, "outbox");
        if (!_infoOutbox.equals(_existingOutbox)) {
          return new RoomOpenHelper.ValidationResult(false, "outbox(com.diegodiaz.techwizards.data.local.entity.OutboxEntity).\n"
                  + " Expected:\n" + _infoOutbox + "\n"
                  + " Found:\n" + _existingOutbox);
        }
        final HashMap<String, TableInfo.Column> _columnsIdMap = new HashMap<String, TableInfo.Column>(5);
        _columnsIdMap.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdMap.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdMap.put("localId", new TableInfo.Column("localId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdMap.put("remoteId", new TableInfo.Column("remoteId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdMap.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIdMap = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIdMap = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIdMap = new TableInfo("id_map", _columnsIdMap, _foreignKeysIdMap, _indicesIdMap);
        final TableInfo _existingIdMap = TableInfo.read(db, "id_map");
        if (!_infoIdMap.equals(_existingIdMap)) {
          return new RoomOpenHelper.ValidationResult(false, "id_map(com.diegodiaz.techwizards.data.local.entity.IdMapEntity).\n"
                  + " Expected:\n" + _infoIdMap + "\n"
                  + " Found:\n" + _existingIdMap);
        }
        final HashMap<String, TableInfo.Column> _columnsTombstone = new HashMap<String, TableInfo.Column>(4);
        _columnsTombstone.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTombstone.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTombstone.put("deletedId", new TableInfo.Column("deletedId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTombstone.put("deletedAt", new TableInfo.Column("deletedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTombstone = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTombstone = new HashSet<TableInfo.Index>(2);
        _indicesTombstone.add(new TableInfo.Index("index_tombstone_type_deletedId", true, Arrays.asList("type", "deletedId"), Arrays.asList("ASC", "ASC")));
        _indicesTombstone.add(new TableInfo.Index("index_tombstone_deletedAt", false, Arrays.asList("deletedAt"), Arrays.asList("ASC")));
        final TableInfo _infoTombstone = new TableInfo("tombstone", _columnsTombstone, _foreignKeysTombstone, _indicesTombstone);
        final TableInfo _existingTombstone = TableInfo.read(db, "tombstone");
        if (!_infoTombstone.equals(_existingTombstone)) {
          return new RoomOpenHelper.ValidationResult(false, "tombstone(com.diegodiaz.techwizards.data.local.entity.TombstoneEntity).\n"
                  + " Expected:\n" + _infoTombstone + "\n"
                  + " Found:\n" + _existingTombstone);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "e15831b177d96b3760af55850a299224", "4b3449d8ffc6ed229cfead816926adfc");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "usuario","monedero","partida","evento","lobby","match","match_event","match_participant","match_score","message","outbox","id_map","tombstone");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `usuario`");
      _db.execSQL("DELETE FROM `monedero`");
      _db.execSQL("DELETE FROM `partida`");
      _db.execSQL("DELETE FROM `evento`");
      _db.execSQL("DELETE FROM `lobby`");
      _db.execSQL("DELETE FROM `match`");
      _db.execSQL("DELETE FROM `match_event`");
      _db.execSQL("DELETE FROM `match_participant`");
      _db.execSQL("DELETE FROM `match_score`");
      _db.execSQL("DELETE FROM `message`");
      _db.execSQL("DELETE FROM `outbox`");
      _db.execSQL("DELETE FROM `id_map`");
      _db.execSQL("DELETE FROM `tombstone`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(IUsuarioDao.class, IUsuarioDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IMonederoDao.class, IMonederoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IPartidaDao.class, IPartidaDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IEventoDao.class, IEventoDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ILobbyDao.class, ILobbyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IMatchDao.class, IMatchDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IMatchEventDao.class, IMatchEventDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IMatchParticipantDao.class, IMatchParticipantDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IMatchScoreDao.class, IMatchScoreDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IMessageDao.class, IMessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IOutboxDao.class, IOutboxDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IIdMapDao.class, IIdMapDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ITombstoneDao.class, ITombstoneDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public IUsuarioDao usuarioDao() {
    if (_iUsuarioDao != null) {
      return _iUsuarioDao;
    } else {
      synchronized(this) {
        if(_iUsuarioDao == null) {
          _iUsuarioDao = new IUsuarioDao_Impl(this);
        }
        return _iUsuarioDao;
      }
    }
  }

  @Override
  public IMonederoDao monederoDao() {
    if (_iMonederoDao != null) {
      return _iMonederoDao;
    } else {
      synchronized(this) {
        if(_iMonederoDao == null) {
          _iMonederoDao = new IMonederoDao_Impl(this);
        }
        return _iMonederoDao;
      }
    }
  }

  @Override
  public IPartidaDao partidaDao() {
    if (_iPartidaDao != null) {
      return _iPartidaDao;
    } else {
      synchronized(this) {
        if(_iPartidaDao == null) {
          _iPartidaDao = new IPartidaDao_Impl(this);
        }
        return _iPartidaDao;
      }
    }
  }

  @Override
  public IEventoDao eventoDao() {
    if (_iEventoDao != null) {
      return _iEventoDao;
    } else {
      synchronized(this) {
        if(_iEventoDao == null) {
          _iEventoDao = new IEventoDao_Impl(this);
        }
        return _iEventoDao;
      }
    }
  }

  @Override
  public ILobbyDao lobbyDao() {
    if (_iLobbyDao != null) {
      return _iLobbyDao;
    } else {
      synchronized(this) {
        if(_iLobbyDao == null) {
          _iLobbyDao = new ILobbyDao_Impl(this);
        }
        return _iLobbyDao;
      }
    }
  }

  @Override
  public IMatchDao matchDao() {
    if (_iMatchDao != null) {
      return _iMatchDao;
    } else {
      synchronized(this) {
        if(_iMatchDao == null) {
          _iMatchDao = new IMatchDao_Impl(this);
        }
        return _iMatchDao;
      }
    }
  }

  @Override
  public IMatchEventDao matchEventDao() {
    if (_iMatchEventDao != null) {
      return _iMatchEventDao;
    } else {
      synchronized(this) {
        if(_iMatchEventDao == null) {
          _iMatchEventDao = new IMatchEventDao_Impl(this);
        }
        return _iMatchEventDao;
      }
    }
  }

  @Override
  public IMatchParticipantDao matchParticipantDao() {
    if (_iMatchParticipantDao != null) {
      return _iMatchParticipantDao;
    } else {
      synchronized(this) {
        if(_iMatchParticipantDao == null) {
          _iMatchParticipantDao = new IMatchParticipantDao_Impl(this);
        }
        return _iMatchParticipantDao;
      }
    }
  }

  @Override
  public IMatchScoreDao matchScoreDao() {
    if (_iMatchScoreDao != null) {
      return _iMatchScoreDao;
    } else {
      synchronized(this) {
        if(_iMatchScoreDao == null) {
          _iMatchScoreDao = new IMatchScoreDao_Impl(this);
        }
        return _iMatchScoreDao;
      }
    }
  }

  @Override
  public IMessageDao messageDao() {
    if (_iMessageDao != null) {
      return _iMessageDao;
    } else {
      synchronized(this) {
        if(_iMessageDao == null) {
          _iMessageDao = new IMessageDao_Impl(this);
        }
        return _iMessageDao;
      }
    }
  }

  @Override
  public IOutboxDao outboxDao() {
    if (_iOutboxDao != null) {
      return _iOutboxDao;
    } else {
      synchronized(this) {
        if(_iOutboxDao == null) {
          _iOutboxDao = new IOutboxDao_Impl(this);
        }
        return _iOutboxDao;
      }
    }
  }

  @Override
  public IIdMapDao idMapDao() {
    if (_iIdMapDao != null) {
      return _iIdMapDao;
    } else {
      synchronized(this) {
        if(_iIdMapDao == null) {
          _iIdMapDao = new IIdMapDao_Impl(this);
        }
        return _iIdMapDao;
      }
    }
  }

  @Override
  public ITombstoneDao tombstoneDao() {
    if (_iTombstoneDao != null) {
      return _iTombstoneDao;
    } else {
      synchronized(this) {
        if(_iTombstoneDao == null) {
          _iTombstoneDao = new ITombstoneDao_Impl(this);
        }
        return _iTombstoneDao;
      }
    }
  }
}
