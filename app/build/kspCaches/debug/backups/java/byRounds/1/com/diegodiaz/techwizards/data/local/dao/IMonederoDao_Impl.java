package com.diegodiaz.techwizards.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Void;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IMonederoDao_Impl implements IMonederoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MonederoEntity> __insertionAdapterOfMonederoEntity;

  private final SharedSQLiteStatement __preparedStmtOfActualizarSaldo;

  public IMonederoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMonederoEntity = new EntityInsertionAdapter<MonederoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `monedero` (`id`,`usuarioId`,`saldo`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonederoEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUsuarioId());
        statement.bindLong(3, entity.getSaldo());
      }
    };
    this.__preparedStmtOfActualizarSaldo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE monedero SET saldo = ? WHERE usuarioId = ?";
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
  public Completable actualizarSaldo(final String usuarioId, final int nuevo) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarSaldo.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, nuevo);
        _argIndex = 2;
        _stmt.bindString(_argIndex, usuarioId);
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
  public Flowable<MonederoEntity> observeSaldo(final String usuarioId) {
    final String _sql = "SELECT * FROM monedero WHERE usuarioId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, usuarioId);
    return RxRoom.createFlowable(__db, false, new String[] {"monedero"}, new Callable<MonederoEntity>() {
      @Override
      @NonNull
      public MonederoEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioId = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioId");
          final int _cursorIndexOfSaldo = CursorUtil.getColumnIndexOrThrow(_cursor, "saldo");
          final MonederoEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUsuarioId;
            _tmpUsuarioId = _cursor.getString(_cursorIndexOfUsuarioId);
            final int _tmpSaldo;
            _tmpSaldo = _cursor.getInt(_cursorIndexOfSaldo);
            _result = new MonederoEntity(_tmpId,_tmpUsuarioId,_tmpSaldo);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
