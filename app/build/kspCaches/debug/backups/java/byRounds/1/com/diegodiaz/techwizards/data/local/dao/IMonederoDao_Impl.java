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
import androidx.room.rxjava3.RxRoom;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IMonederoDao_Impl implements IMonederoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MonederoEntity> __insertionAdapterOfMonederoEntity;

  private final SharedSQLiteStatement __preparedStmtOfActualizarSaldo;

  private final SharedSQLiteStatement __preparedStmtOfBorrarTodo;

  public IMonederoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMonederoEntity = new EntityInsertionAdapter<MonederoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `Monedero` (`id`,`usuarioNumero`,`saldo`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonederoEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindLong(2, entity.getUsuarioNumero());
        statement.bindLong(3, entity.getSaldo());
      }
    };
    this.__preparedStmtOfActualizarSaldo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE monedero SET saldo = ? WHERE usuarioNumero = ?";
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
  public Completable upsert(final MonederoEntity entity) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMonederoEntity.insert(entity);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Object upsertSuspend(final MonederoEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMonederoEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Completable actualizarSaldo(final long usuarioNumero, final int nuevo) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarSaldo.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nuevo);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, usuarioNumero);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return null;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarSaldo.release(_stmt);
        }
      }
    });
  }

  @Override
  public Object actualizarSaldoSuspend(final long usuarioNumero, final int nuevo,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarSaldo.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nuevo);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, usuarioNumero);
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
  public Flowable<MonederoEntity> observeSaldo(final long usuarioNumero) {
    final String _sql = "SELECT * FROM Monedero WHERE usuarioNumero = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, usuarioNumero);
    return RxRoom.createFlowable(__db, false, new String[] {"Monedero"}, new Callable<MonederoEntity>() {
      @Override
      @NonNull
      public MonederoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioNumero");
          final int _cursorIndexOfSaldo = CursorUtil.getColumnIndexOrThrow(_cursor, "saldo");
          final MonederoEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final long _tmpUsuarioNumero;
            _tmpUsuarioNumero = _cursor.getLong(_cursorIndexOfUsuarioNumero);
            final int _tmpSaldo;
            _tmpSaldo = _cursor.getInt(_cursorIndexOfSaldo);
            _result = new MonederoEntity(_tmpId,_tmpUsuarioNumero,_tmpSaldo);
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
  public Flow<MonederoEntity> observeSaldoFlow(final long usuarioNumero) {
    final String _sql = "SELECT * FROM Monedero WHERE usuarioNumero = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, usuarioNumero);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"Monedero"}, new Callable<MonederoEntity>() {
      @Override
      @Nullable
      public MonederoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioNumero");
          final int _cursorIndexOfSaldo = CursorUtil.getColumnIndexOrThrow(_cursor, "saldo");
          final MonederoEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final long _tmpUsuarioNumero;
            _tmpUsuarioNumero = _cursor.getLong(_cursorIndexOfUsuarioNumero);
            final int _tmpSaldo;
            _tmpSaldo = _cursor.getInt(_cursorIndexOfSaldo);
            _result = new MonederoEntity(_tmpId,_tmpUsuarioNumero,_tmpSaldo);
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
  public Object obtenerSaldo(final long usuarioNumero,
      final Continuation<? super MonederoEntity> $completion) {
    final String _sql = "SELECT * FROM Monedero WHERE usuarioNumero = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, usuarioNumero);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MonederoEntity>() {
      @Override
      @Nullable
      public MonederoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioNumero");
          final int _cursorIndexOfSaldo = CursorUtil.getColumnIndexOrThrow(_cursor, "saldo");
          final MonederoEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final long _tmpUsuarioNumero;
            _tmpUsuarioNumero = _cursor.getLong(_cursorIndexOfUsuarioNumero);
            final int _tmpSaldo;
            _tmpSaldo = _cursor.getInt(_cursorIndexOfSaldo);
            _result = new MonederoEntity(_tmpId,_tmpUsuarioNumero,_tmpSaldo);
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
