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
import com.diegodiaz.techwizards.data.local.dao.IMonederoDao;
import com.diegodiaz.techwizards.data.local.dao.IMonederoDao_Impl;
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao;
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao_Impl;
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
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4151371c4fdd3a3c3886d1e2a82762f0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `usuario`");
        db.execSQL("DROP TABLE IF EXISTS `monedero`");
        db.execSQL("DROP TABLE IF EXISTS `partida`");
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
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4151371c4fdd3a3c3886d1e2a82762f0", "96423ab7cd2d71addfb74b3c31c1f6be");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "usuario","monedero","partida");
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
}
