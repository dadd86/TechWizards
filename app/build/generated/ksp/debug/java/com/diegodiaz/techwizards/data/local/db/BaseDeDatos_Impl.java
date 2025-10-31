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
        db.execSQL("CREATE TABLE IF NOT EXISTS `Usuario` (`numero` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuario` TEXT NOT NULL, `fechaAlta` INTEGER NOT NULL, `monedas` INTEGER NOT NULL, `gano` INTEGER NOT NULL, `firebaseUid` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Monedero` (`id` TEXT NOT NULL, `usuarioNumero` INTEGER NOT NULL, `saldo` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Partida` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioNumero` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, `resultado` TEXT NOT NULL, `cambioMonedas` INTEGER NOT NULL, FOREIGN KEY(`usuarioNumero`) REFERENCES `Usuario`(`numero`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Partida_usuarioNumero` ON `Partida` (`usuarioNumero`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Partida_usuarioNumero_fecha` ON `Partida` (`usuarioNumero`, `fecha`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `evento` (`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `fechaInicio` INTEGER NOT NULL, `fechaFin` INTEGER NOT NULL, `completado` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Lobby` (`nombre` TEXT NOT NULL, `id` TEXT NOT NULL, `codigo` TEXT, `modo` TEXT NOT NULL, `estado` TEXT NOT NULL, `creadorNum` INTEGER NOT NULL, `createdAtMs` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`creadorNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Lobby_estado` ON `Lobby` (`estado`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Lobby_codigo` ON `Lobby` (`codigo`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Lobby_creadorNum` ON `Lobby` (`creadorNum`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Lobby_estado_createdAtMs` ON `Lobby` (`estado`, `createdAtMs`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Match` (`id` TEXT NOT NULL, `lobbyId` TEXT, `modo` TEXT NOT NULL, `estado` TEXT NOT NULL, `createdByNum` INTEGER NOT NULL, `createdAtMs` INTEGER NOT NULL, `startedAtMs` INTEGER, `finishedAtMs` INTEGER, PRIMARY KEY(`id`), FOREIGN KEY(`lobbyId`) REFERENCES `Lobby`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`createdByNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Match_estado_createdAtMs` ON `Match` (`estado`, `createdAtMs`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Match_lobbyId` ON `Match` (`lobbyId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `MatchEvent` (`id` TEXT NOT NULL, `matchId` TEXT NOT NULL, `seq` INTEGER NOT NULL, `type` TEXT NOT NULL, `actorNum` INTEGER NOT NULL, `payloadJson` TEXT, `createdAtMs` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`actorNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_MatchEvent_matchId_seq` ON `MatchEvent` (`matchId`, `seq`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `MatchParticipant` (`matchId` TEXT NOT NULL, `usuarioNum` INTEGER NOT NULL, `rol` TEXT, `teamId` TEXT, `joinedAtMs` INTEGER NOT NULL, `leftAtMs` INTEGER, `score` INTEGER NOT NULL, PRIMARY KEY(`matchId`, `usuarioNum`), FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`usuarioNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_MatchParticipant_matchId` ON `MatchParticipant` (`matchId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_MatchParticipant_usuarioNum` ON `MatchParticipant` (`usuarioNum`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `MatchScore` (`matchId` TEXT NOT NULL, `usuarioNum` INTEGER NOT NULL, `score` INTEGER NOT NULL, PRIMARY KEY(`matchId`, `usuarioNum`), FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`usuarioNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Message` (`id` TEXT NOT NULL, `matchId` TEXT NOT NULL, `senderNum` INTEGER NOT NULL, `text` TEXT NOT NULL, `createdAtMs` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`senderNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Message_matchId_createdAtMs` ON `Message` (`matchId`, `createdAtMs`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Message_senderNum` ON `Message` (`senderNum`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Outbox` (`operationId` TEXT NOT NULL, `entityType` TEXT NOT NULL, `entityId` TEXT NOT NULL, `op` TEXT NOT NULL, `payloadJson` TEXT NOT NULL, `attempt` INTEGER NOT NULL, `lastError` TEXT, `createdAtMs` INTEGER NOT NULL, `updatedAtMs` INTEGER NOT NULL, PRIMARY KEY(`operationId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `IdMap` (`localTable` TEXT NOT NULL, `localId` TEXT NOT NULL, `remoteCollection` TEXT NOT NULL, `remoteId` TEXT NOT NULL, PRIMARY KEY(`localTable`, `localId`))");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_IdMap_remoteCollection_remoteId` ON `IdMap` (`remoteCollection`, `remoteId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Tombstone` (`tableName` TEXT NOT NULL, `entityId` TEXT NOT NULL, `deletedAtMs` INTEGER NOT NULL, PRIMARY KEY(`tableName`, `entityId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fbabe35cde15682d82f76bf0836d6e16')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `Usuario`");
        db.execSQL("DROP TABLE IF EXISTS `Monedero`");
        db.execSQL("DROP TABLE IF EXISTS `Partida`");
        db.execSQL("DROP TABLE IF EXISTS `evento`");
        db.execSQL("DROP TABLE IF EXISTS `Lobby`");
        db.execSQL("DROP TABLE IF EXISTS `Match`");
        db.execSQL("DROP TABLE IF EXISTS `MatchEvent`");
        db.execSQL("DROP TABLE IF EXISTS `MatchParticipant`");
        db.execSQL("DROP TABLE IF EXISTS `MatchScore`");
        db.execSQL("DROP TABLE IF EXISTS `Message`");
        db.execSQL("DROP TABLE IF EXISTS `Outbox`");
        db.execSQL("DROP TABLE IF EXISTS `IdMap`");
        db.execSQL("DROP TABLE IF EXISTS `Tombstone`");
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
        final HashMap<String, TableInfo.Column> _columnsUsuario = new HashMap<String, TableInfo.Column>(6);
        _columnsUsuario.put("numero", new TableInfo.Column("numero", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuario.put("usuario", new TableInfo.Column("usuario", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuario.put("fechaAlta", new TableInfo.Column("fechaAlta", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuario.put("monedas", new TableInfo.Column("monedas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuario.put("gano", new TableInfo.Column("gano", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsuario.put("firebaseUid", new TableInfo.Column("firebaseUid", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsuario = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsuario = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsuario = new TableInfo("Usuario", _columnsUsuario, _foreignKeysUsuario, _indicesUsuario);
        final TableInfo _existingUsuario = TableInfo.read(db, "Usuario");
        if (!_infoUsuario.equals(_existingUsuario)) {
          return new RoomOpenHelper.ValidationResult(false, "Usuario(com.diegodiaz.techwizards.data.local.entity.UsuarioEntity).\n"
                  + " Expected:\n" + _infoUsuario + "\n"
                  + " Found:\n" + _existingUsuario);
        }
        final HashMap<String, TableInfo.Column> _columnsMonedero = new HashMap<String, TableInfo.Column>(3);
        _columnsMonedero.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonedero.put("usuarioNumero", new TableInfo.Column("usuarioNumero", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMonedero.put("saldo", new TableInfo.Column("saldo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMonedero = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMonedero = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMonedero = new TableInfo("Monedero", _columnsMonedero, _foreignKeysMonedero, _indicesMonedero);
        final TableInfo _existingMonedero = TableInfo.read(db, "Monedero");
        if (!_infoMonedero.equals(_existingMonedero)) {
          return new RoomOpenHelper.ValidationResult(false, "Monedero(com.diegodiaz.techwizards.data.local.entity.MonederoEntity).\n"
                  + " Expected:\n" + _infoMonedero + "\n"
                  + " Found:\n" + _existingMonedero);
        }
        final HashMap<String, TableInfo.Column> _columnsPartida = new HashMap<String, TableInfo.Column>(5);
        _columnsPartida.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("usuarioNumero", new TableInfo.Column("usuarioNumero", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("fecha", new TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("resultado", new TableInfo.Column("resultado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPartida.put("cambioMonedas", new TableInfo.Column("cambioMonedas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPartida = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPartida.add(new TableInfo.ForeignKey("Usuario", "CASCADE", "CASCADE", Arrays.asList("usuarioNumero"), Arrays.asList("numero")));
        final HashSet<TableInfo.Index> _indicesPartida = new HashSet<TableInfo.Index>(2);
        _indicesPartida.add(new TableInfo.Index("index_Partida_usuarioNumero", false, Arrays.asList("usuarioNumero"), Arrays.asList("ASC")));
        _indicesPartida.add(new TableInfo.Index("index_Partida_usuarioNumero_fecha", false, Arrays.asList("usuarioNumero", "fecha"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoPartida = new TableInfo("Partida", _columnsPartida, _foreignKeysPartida, _indicesPartida);
        final TableInfo _existingPartida = TableInfo.read(db, "Partida");
        if (!_infoPartida.equals(_existingPartida)) {
          return new RoomOpenHelper.ValidationResult(false, "Partida(com.diegodiaz.techwizards.data.local.entity.PartidaEntity).\n"
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
        final HashMap<String, TableInfo.Column> _columnsLobby = new HashMap<String, TableInfo.Column>(7);
        _columnsLobby.put("nombre", new TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("codigo", new TableInfo.Column("codigo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("modo", new TableInfo.Column("modo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("estado", new TableInfo.Column("estado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("creadorNum", new TableInfo.Column("creadorNum", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLobby.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLobby = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysLobby.add(new TableInfo.ForeignKey("Usuario", "CASCADE", "NO ACTION", Arrays.asList("creadorNum"), Arrays.asList("numero")));
        final HashSet<TableInfo.Index> _indicesLobby = new HashSet<TableInfo.Index>(4);
        _indicesLobby.add(new TableInfo.Index("index_Lobby_estado", false, Arrays.asList("estado"), Arrays.asList("ASC")));
        _indicesLobby.add(new TableInfo.Index("index_Lobby_codigo", true, Arrays.asList("codigo"), Arrays.asList("ASC")));
        _indicesLobby.add(new TableInfo.Index("index_Lobby_creadorNum", false, Arrays.asList("creadorNum"), Arrays.asList("ASC")));
        _indicesLobby.add(new TableInfo.Index("index_Lobby_estado_createdAtMs", false, Arrays.asList("estado", "createdAtMs"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoLobby = new TableInfo("Lobby", _columnsLobby, _foreignKeysLobby, _indicesLobby);
        final TableInfo _existingLobby = TableInfo.read(db, "Lobby");
        if (!_infoLobby.equals(_existingLobby)) {
          return new RoomOpenHelper.ValidationResult(false, "Lobby(com.diegodiaz.techwizards.data.local.entity.LobbyEntity).\n"
                  + " Expected:\n" + _infoLobby + "\n"
                  + " Found:\n" + _existingLobby);
        }
        final HashMap<String, TableInfo.Column> _columnsMatch = new HashMap<String, TableInfo.Column>(8);
        _columnsMatch.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("lobbyId", new TableInfo.Column("lobbyId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("modo", new TableInfo.Column("modo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("estado", new TableInfo.Column("estado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("createdByNum", new TableInfo.Column("createdByNum", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("startedAtMs", new TableInfo.Column("startedAtMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatch.put("finishedAtMs", new TableInfo.Column("finishedAtMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatch = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMatch.add(new TableInfo.ForeignKey("Lobby", "SET NULL", "NO ACTION", Arrays.asList("lobbyId"), Arrays.asList("id")));
        _foreignKeysMatch.add(new TableInfo.ForeignKey("Usuario", "CASCADE", "NO ACTION", Arrays.asList("createdByNum"), Arrays.asList("numero")));
        final HashSet<TableInfo.Index> _indicesMatch = new HashSet<TableInfo.Index>(2);
        _indicesMatch.add(new TableInfo.Index("index_Match_estado_createdAtMs", false, Arrays.asList("estado", "createdAtMs"), Arrays.asList("ASC", "ASC")));
        _indicesMatch.add(new TableInfo.Index("index_Match_lobbyId", false, Arrays.asList("lobbyId"), Arrays.asList("ASC")));
        final TableInfo _infoMatch = new TableInfo("Match", _columnsMatch, _foreignKeysMatch, _indicesMatch);
        final TableInfo _existingMatch = TableInfo.read(db, "Match");
        if (!_infoMatch.equals(_existingMatch)) {
          return new RoomOpenHelper.ValidationResult(false, "Match(com.diegodiaz.techwizards.data.local.entity.MatchEntity).\n"
                  + " Expected:\n" + _infoMatch + "\n"
                  + " Found:\n" + _existingMatch);
        }
        final HashMap<String, TableInfo.Column> _columnsMatchEvent = new HashMap<String, TableInfo.Column>(7);
        _columnsMatchEvent.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("matchId", new TableInfo.Column("matchId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("seq", new TableInfo.Column("seq", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("actorNum", new TableInfo.Column("actorNum", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("payloadJson", new TableInfo.Column("payloadJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchEvent.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatchEvent = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMatchEvent.add(new TableInfo.ForeignKey("Match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMatchEvent.add(new TableInfo.ForeignKey("Usuario", "CASCADE", "NO ACTION", Arrays.asList("actorNum"), Arrays.asList("numero")));
        final HashSet<TableInfo.Index> _indicesMatchEvent = new HashSet<TableInfo.Index>(1);
        _indicesMatchEvent.add(new TableInfo.Index("index_MatchEvent_matchId_seq", true, Arrays.asList("matchId", "seq"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoMatchEvent = new TableInfo("MatchEvent", _columnsMatchEvent, _foreignKeysMatchEvent, _indicesMatchEvent);
        final TableInfo _existingMatchEvent = TableInfo.read(db, "MatchEvent");
        if (!_infoMatchEvent.equals(_existingMatchEvent)) {
          return new RoomOpenHelper.ValidationResult(false, "MatchEvent(com.diegodiaz.techwizards.data.local.entity.MatchEventEntity).\n"
                  + " Expected:\n" + _infoMatchEvent + "\n"
                  + " Found:\n" + _existingMatchEvent);
        }
        final HashMap<String, TableInfo.Column> _columnsMatchParticipant = new HashMap<String, TableInfo.Column>(7);
        _columnsMatchParticipant.put("matchId", new TableInfo.Column("matchId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("usuarioNum", new TableInfo.Column("usuarioNum", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("rol", new TableInfo.Column("rol", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("teamId", new TableInfo.Column("teamId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("joinedAtMs", new TableInfo.Column("joinedAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("leftAtMs", new TableInfo.Column("leftAtMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchParticipant.put("score", new TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatchParticipant = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMatchParticipant.add(new TableInfo.ForeignKey("Match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMatchParticipant.add(new TableInfo.ForeignKey("Usuario", "CASCADE", "NO ACTION", Arrays.asList("usuarioNum"), Arrays.asList("numero")));
        final HashSet<TableInfo.Index> _indicesMatchParticipant = new HashSet<TableInfo.Index>(2);
        _indicesMatchParticipant.add(new TableInfo.Index("index_MatchParticipant_matchId", false, Arrays.asList("matchId"), Arrays.asList("ASC")));
        _indicesMatchParticipant.add(new TableInfo.Index("index_MatchParticipant_usuarioNum", false, Arrays.asList("usuarioNum"), Arrays.asList("ASC")));
        final TableInfo _infoMatchParticipant = new TableInfo("MatchParticipant", _columnsMatchParticipant, _foreignKeysMatchParticipant, _indicesMatchParticipant);
        final TableInfo _existingMatchParticipant = TableInfo.read(db, "MatchParticipant");
        if (!_infoMatchParticipant.equals(_existingMatchParticipant)) {
          return new RoomOpenHelper.ValidationResult(false, "MatchParticipant(com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity).\n"
                  + " Expected:\n" + _infoMatchParticipant + "\n"
                  + " Found:\n" + _existingMatchParticipant);
        }
        final HashMap<String, TableInfo.Column> _columnsMatchScore = new HashMap<String, TableInfo.Column>(3);
        _columnsMatchScore.put("matchId", new TableInfo.Column("matchId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchScore.put("usuarioNum", new TableInfo.Column("usuarioNum", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMatchScore.put("score", new TableInfo.Column("score", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMatchScore = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMatchScore.add(new TableInfo.ForeignKey("Match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMatchScore.add(new TableInfo.ForeignKey("Usuario", "CASCADE", "NO ACTION", Arrays.asList("usuarioNum"), Arrays.asList("numero")));
        final HashSet<TableInfo.Index> _indicesMatchScore = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMatchScore = new TableInfo("MatchScore", _columnsMatchScore, _foreignKeysMatchScore, _indicesMatchScore);
        final TableInfo _existingMatchScore = TableInfo.read(db, "MatchScore");
        if (!_infoMatchScore.equals(_existingMatchScore)) {
          return new RoomOpenHelper.ValidationResult(false, "MatchScore(com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity).\n"
                  + " Expected:\n" + _infoMatchScore + "\n"
                  + " Found:\n" + _existingMatchScore);
        }
        final HashMap<String, TableInfo.Column> _columnsMessage = new HashMap<String, TableInfo.Column>(5);
        _columnsMessage.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("matchId", new TableInfo.Column("matchId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("senderNum", new TableInfo.Column("senderNum", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("text", new TableInfo.Column("text", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessage.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessage = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysMessage.add(new TableInfo.ForeignKey("Match", "CASCADE", "NO ACTION", Arrays.asList("matchId"), Arrays.asList("id")));
        _foreignKeysMessage.add(new TableInfo.ForeignKey("Usuario", "CASCADE", "NO ACTION", Arrays.asList("senderNum"), Arrays.asList("numero")));
        final HashSet<TableInfo.Index> _indicesMessage = new HashSet<TableInfo.Index>(2);
        _indicesMessage.add(new TableInfo.Index("index_Message_matchId_createdAtMs", false, Arrays.asList("matchId", "createdAtMs"), Arrays.asList("ASC", "ASC")));
        _indicesMessage.add(new TableInfo.Index("index_Message_senderNum", false, Arrays.asList("senderNum"), Arrays.asList("ASC")));
        final TableInfo _infoMessage = new TableInfo("Message", _columnsMessage, _foreignKeysMessage, _indicesMessage);
        final TableInfo _existingMessage = TableInfo.read(db, "Message");
        if (!_infoMessage.equals(_existingMessage)) {
          return new RoomOpenHelper.ValidationResult(false, "Message(com.diegodiaz.techwizards.data.local.entity.MessageEntity).\n"
                  + " Expected:\n" + _infoMessage + "\n"
                  + " Found:\n" + _existingMessage);
        }
        final HashMap<String, TableInfo.Column> _columnsOutbox = new HashMap<String, TableInfo.Column>(9);
        _columnsOutbox.put("operationId", new TableInfo.Column("operationId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("entityType", new TableInfo.Column("entityType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("entityId", new TableInfo.Column("entityId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("op", new TableInfo.Column("op", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("payloadJson", new TableInfo.Column("payloadJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("attempt", new TableInfo.Column("attempt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("lastError", new TableInfo.Column("lastError", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("createdAtMs", new TableInfo.Column("createdAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsOutbox.put("updatedAtMs", new TableInfo.Column("updatedAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysOutbox = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesOutbox = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoOutbox = new TableInfo("Outbox", _columnsOutbox, _foreignKeysOutbox, _indicesOutbox);
        final TableInfo _existingOutbox = TableInfo.read(db, "Outbox");
        if (!_infoOutbox.equals(_existingOutbox)) {
          return new RoomOpenHelper.ValidationResult(false, "Outbox(com.diegodiaz.techwizards.data.local.entity.OutboxEntity).\n"
                  + " Expected:\n" + _infoOutbox + "\n"
                  + " Found:\n" + _existingOutbox);
        }
        final HashMap<String, TableInfo.Column> _columnsIdMap = new HashMap<String, TableInfo.Column>(4);
        _columnsIdMap.put("localTable", new TableInfo.Column("localTable", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdMap.put("localId", new TableInfo.Column("localId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdMap.put("remoteCollection", new TableInfo.Column("remoteCollection", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdMap.put("remoteId", new TableInfo.Column("remoteId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIdMap = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIdMap = new HashSet<TableInfo.Index>(1);
        _indicesIdMap.add(new TableInfo.Index("index_IdMap_remoteCollection_remoteId", true, Arrays.asList("remoteCollection", "remoteId"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoIdMap = new TableInfo("IdMap", _columnsIdMap, _foreignKeysIdMap, _indicesIdMap);
        final TableInfo _existingIdMap = TableInfo.read(db, "IdMap");
        if (!_infoIdMap.equals(_existingIdMap)) {
          return new RoomOpenHelper.ValidationResult(false, "IdMap(com.diegodiaz.techwizards.data.local.entity.IdMapEntity).\n"
                  + " Expected:\n" + _infoIdMap + "\n"
                  + " Found:\n" + _existingIdMap);
        }
        final HashMap<String, TableInfo.Column> _columnsTombstone = new HashMap<String, TableInfo.Column>(3);
        _columnsTombstone.put("tableName", new TableInfo.Column("tableName", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTombstone.put("entityId", new TableInfo.Column("entityId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTombstone.put("deletedAtMs", new TableInfo.Column("deletedAtMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTombstone = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTombstone = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTombstone = new TableInfo("Tombstone", _columnsTombstone, _foreignKeysTombstone, _indicesTombstone);
        final TableInfo _existingTombstone = TableInfo.read(db, "Tombstone");
        if (!_infoTombstone.equals(_existingTombstone)) {
          return new RoomOpenHelper.ValidationResult(false, "Tombstone(com.diegodiaz.techwizards.data.local.entity.TombstoneEntity).\n"
                  + " Expected:\n" + _infoTombstone + "\n"
                  + " Found:\n" + _existingTombstone);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "fbabe35cde15682d82f76bf0836d6e16", "9a99c2cb6ffeb4a74e5c3b35ffb778a7");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "Usuario","Monedero","Partida","evento","Lobby","Match","MatchEvent","MatchParticipant","MatchScore","Message","Outbox","IdMap","Tombstone");
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
      _db.execSQL("DELETE FROM `Usuario`");
      _db.execSQL("DELETE FROM `Monedero`");
      _db.execSQL("DELETE FROM `Partida`");
      _db.execSQL("DELETE FROM `evento`");
      _db.execSQL("DELETE FROM `Lobby`");
      _db.execSQL("DELETE FROM `Match`");
      _db.execSQL("DELETE FROM `MatchEvent`");
      _db.execSQL("DELETE FROM `MatchParticipant`");
      _db.execSQL("DELETE FROM `MatchScore`");
      _db.execSQL("DELETE FROM `Message`");
      _db.execSQL("DELETE FROM `Outbox`");
      _db.execSQL("DELETE FROM `IdMap`");
      _db.execSQL("DELETE FROM `Tombstone`");
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
