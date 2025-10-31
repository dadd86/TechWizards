package com.diegodiaz.techwizards.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Void;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IUsuarioDao_Impl implements IUsuarioDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UsuarioEntity> __insertionAdapterOfUsuarioEntity;

  private final SharedSQLiteStatement __preparedStmtOfActualizarSaldo;

  private final SharedSQLiteStatement __preparedStmtOfActualizarUltimoResultado;

  private final SharedSQLiteStatement __preparedStmtOfBorrarTodo;

  public IUsuarioDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUsuarioEntity = new EntityInsertionAdapter<UsuarioEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `Usuario` (`numero`,`usuario`,`fechaAlta`,`monedas`,`gano`,`firebaseUid`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UsuarioEntity entity) {
        statement.bindLong(1, entity.getNumero());
        statement.bindString(2, entity.getAlias());
        statement.bindLong(3, entity.getFechaAltaMs());
        statement.bindLong(4, entity.getMonedas());
        final int _tmp = entity.getGanoUltimaPartida() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getFirebaseUid() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getFirebaseUid());
        }
      }
    };
    this.__preparedStmtOfActualizarSaldo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE Usuario SET monedas = ? WHERE numero = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActualizarUltimoResultado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE Usuario SET gano = ? WHERE numero = ?";
        return _query;
      }
    };
    this.__preparedStmtOfBorrarTodo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM usuario";
        return _query;
      }
    };
  }

  @Override
  public Completable upsert(final UsuarioEntity usuario) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUsuarioEntity.insert(usuario);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Object upsertSuspend(final UsuarioEntity usuario,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUsuarioEntity.insert(usuario);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizarSaldo(final long numero, final int nuevoSaldo,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarSaldo.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nuevoSaldo);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, numero);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarSaldo.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object actualizarUltimoResultado(final long numero, final boolean gano,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarUltimoResultado.acquire();
        int _argIndex = 1;
        final int _tmp = gano ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, numero);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarUltimoResultado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public void borrarTodo() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfBorrarTodo.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfBorrarTodo.release(_stmt);
    }
  }

  @Override
  public Object obtenerUsuarioPrincipal(final Continuation<? super UsuarioEntity> $completion) {
    final String _sql = "SELECT `Usuario`.`numero` AS `numero`, `Usuario`.`usuario` AS `usuario`, `Usuario`.`fechaAlta` AS `fechaAlta`, `Usuario`.`monedas` AS `monedas`, `Usuario`.`gano` AS `gano`, `Usuario`.`firebaseUid` AS `firebaseUid` FROM Usuario LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UsuarioEntity>() {
      @Override
      @Nullable
      public UsuarioEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNumero = 0;
          final int _cursorIndexOfAlias = 1;
          final int _cursorIndexOfFechaAltaMs = 2;
          final int _cursorIndexOfMonedas = 3;
          final int _cursorIndexOfGanoUltimaPartida = 4;
          final int _cursorIndexOfFirebaseUid = 5;
          final UsuarioEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpNumero;
            _tmpNumero = _cursor.getLong(_cursorIndexOfNumero);
            final String _tmpAlias;
            _tmpAlias = _cursor.getString(_cursorIndexOfAlias);
            final long _tmpFechaAltaMs;
            _tmpFechaAltaMs = _cursor.getLong(_cursorIndexOfFechaAltaMs);
            final int _tmpMonedas;
            _tmpMonedas = _cursor.getInt(_cursorIndexOfMonedas);
            final boolean _tmpGanoUltimaPartida;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfGanoUltimaPartida);
            _tmpGanoUltimaPartida = _tmp != 0;
            final String _tmpFirebaseUid;
            if (_cursor.isNull(_cursorIndexOfFirebaseUid)) {
              _tmpFirebaseUid = null;
            } else {
              _tmpFirebaseUid = _cursor.getString(_cursorIndexOfFirebaseUid);
            }
            _result = new UsuarioEntity(_tmpNumero,_tmpAlias,_tmpFechaAltaMs,_tmpMonedas,_tmpGanoUltimaPartida,_tmpFirebaseUid);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Maybe<UsuarioEntity> getByNumeroRx(final long numero) {
    final String _sql = "SELECT * FROM Usuario WHERE numero = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, numero);
    return Maybe.fromCallable(new Callable<UsuarioEntity>() {
      @Override
      @Nullable
      public UsuarioEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "numero");
          final int _cursorIndexOfAlias = CursorUtil.getColumnIndexOrThrow(_cursor, "usuario");
          final int _cursorIndexOfFechaAltaMs = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaAlta");
          final int _cursorIndexOfMonedas = CursorUtil.getColumnIndexOrThrow(_cursor, "monedas");
          final int _cursorIndexOfGanoUltimaPartida = CursorUtil.getColumnIndexOrThrow(_cursor, "gano");
          final int _cursorIndexOfFirebaseUid = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseUid");
          final UsuarioEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpNumero;
            _tmpNumero = _cursor.getLong(_cursorIndexOfNumero);
            final String _tmpAlias;
            _tmpAlias = _cursor.getString(_cursorIndexOfAlias);
            final long _tmpFechaAltaMs;
            _tmpFechaAltaMs = _cursor.getLong(_cursorIndexOfFechaAltaMs);
            final int _tmpMonedas;
            _tmpMonedas = _cursor.getInt(_cursorIndexOfMonedas);
            final boolean _tmpGanoUltimaPartida;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfGanoUltimaPartida);
            _tmpGanoUltimaPartida = _tmp != 0;
            final String _tmpFirebaseUid;
            if (_cursor.isNull(_cursorIndexOfFirebaseUid)) {
              _tmpFirebaseUid = null;
            } else {
              _tmpFirebaseUid = _cursor.getString(_cursorIndexOfFirebaseUid);
            }
            _result = new UsuarioEntity(_tmpNumero,_tmpAlias,_tmpFechaAltaMs,_tmpMonedas,_tmpGanoUltimaPartida,_tmpFirebaseUid);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getByNumero(final long numero,
      final Continuation<? super UsuarioEntity> $completion) {
    final String _sql = "SELECT * FROM Usuario WHERE numero = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, numero);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UsuarioEntity>() {
      @Override
      @Nullable
      public UsuarioEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "numero");
          final int _cursorIndexOfAlias = CursorUtil.getColumnIndexOrThrow(_cursor, "usuario");
          final int _cursorIndexOfFechaAltaMs = CursorUtil.getColumnIndexOrThrow(_cursor, "fechaAlta");
          final int _cursorIndexOfMonedas = CursorUtil.getColumnIndexOrThrow(_cursor, "monedas");
          final int _cursorIndexOfGanoUltimaPartida = CursorUtil.getColumnIndexOrThrow(_cursor, "gano");
          final int _cursorIndexOfFirebaseUid = CursorUtil.getColumnIndexOrThrow(_cursor, "firebaseUid");
          final UsuarioEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpNumero;
            _tmpNumero = _cursor.getLong(_cursorIndexOfNumero);
            final String _tmpAlias;
            _tmpAlias = _cursor.getString(_cursorIndexOfAlias);
            final long _tmpFechaAltaMs;
            _tmpFechaAltaMs = _cursor.getLong(_cursorIndexOfFechaAltaMs);
            final int _tmpMonedas;
            _tmpMonedas = _cursor.getInt(_cursorIndexOfMonedas);
            final boolean _tmpGanoUltimaPartida;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfGanoUltimaPartida);
            _tmpGanoUltimaPartida = _tmp != 0;
            final String _tmpFirebaseUid;
            if (_cursor.isNull(_cursorIndexOfFirebaseUid)) {
              _tmpFirebaseUid = null;
            } else {
              _tmpFirebaseUid = _cursor.getString(_cursorIndexOfFirebaseUid);
            }
            _result = new UsuarioEntity(_tmpNumero,_tmpAlias,_tmpFechaAltaMs,_tmpMonedas,_tmpGanoUltimaPartida,_tmpFirebaseUid);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
