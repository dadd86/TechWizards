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
import com.diegodiaz.techwizards.data.local.entity.OutboxEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Void;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IOutboxDao_Impl implements IOutboxDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OutboxEntity> __insertionAdapterOfOutboxEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkDelivered;

  private final SharedSQLiteStatement __preparedStmtOfIncrementRetry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfPurgeDelivered;

  public IOutboxDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOutboxEntity = new EntityInsertionAdapter<OutboxEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `outbox` (`id`,`tipo`,`payload`,`creadoEn`,`entregado`,`reintentos`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OutboxEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTipo());
        statement.bindString(3, entity.getPayload());
        statement.bindLong(4, entity.getCreadoEn());
        final int _tmp = entity.getEntregado() ? 1 : 0;
        statement.bindLong(5, _tmp);
        statement.bindLong(6, entity.getReintentos());
      }
    };
    this.__preparedStmtOfMarkDelivered = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE outbox SET entregado = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementRetry = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE outbox SET reintentos = reintentos + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM outbox WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPurgeDelivered = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM outbox WHERE entregado = 1";
        return _query;
      }
    };
  }

  @Override
  public Completable upsert(final OutboxEntity entity) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOutboxEntity.insert(entity);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable upsertAll(final List<OutboxEntity> list) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOutboxEntity.insert(list);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable markDelivered(final long id) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkDelivered.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfMarkDelivered.release(_stmt);
        }
      }
    });
  }

  @Override
  public Completable incrementRetry(final long id) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementRetry.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfIncrementRetry.release(_stmt);
        }
      }
    });
  }

  @Override
  public Completable deleteById(final long id) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    });
  }

  @Override
  public Completable purgeDelivered() {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPurgeDelivered.acquire();
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
          __preparedStmtOfPurgeDelivered.release(_stmt);
        }
      }
    });
  }

  @Override
  public Maybe<OutboxEntity> getById(final long id) {
    final String _sql = "SELECT * FROM outbox WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return Maybe.fromCallable(new Callable<OutboxEntity>() {
      @Override
      @Nullable
      public OutboxEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfPayload = CursorUtil.getColumnIndexOrThrow(_cursor, "payload");
          final int _cursorIndexOfCreadoEn = CursorUtil.getColumnIndexOrThrow(_cursor, "creadoEn");
          final int _cursorIndexOfEntregado = CursorUtil.getColumnIndexOrThrow(_cursor, "entregado");
          final int _cursorIndexOfReintentos = CursorUtil.getColumnIndexOrThrow(_cursor, "reintentos");
          final OutboxEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            final long _tmpCreadoEn;
            _tmpCreadoEn = _cursor.getLong(_cursorIndexOfCreadoEn);
            final boolean _tmpEntregado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEntregado);
            _tmpEntregado = _tmp != 0;
            final int _tmpReintentos;
            _tmpReintentos = _cursor.getInt(_cursorIndexOfReintentos);
            _result = new OutboxEntity(_tmpId,_tmpTipo,_tmpPayload,_tmpCreadoEn,_tmpEntregado,_tmpReintentos);
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
  public Flowable<List<OutboxEntity>> listPending() {
    final String _sql = "SELECT `outbox`.`id` AS `id`, `outbox`.`tipo` AS `tipo`, `outbox`.`payload` AS `payload`, `outbox`.`creadoEn` AS `creadoEn`, `outbox`.`entregado` AS `entregado`, `outbox`.`reintentos` AS `reintentos` FROM outbox WHERE entregado = 0 ORDER BY creadoEn ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[] {"outbox"}, new Callable<List<OutboxEntity>>() {
      @Override
      @NonNull
      public List<OutboxEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTipo = 1;
          final int _cursorIndexOfPayload = 2;
          final int _cursorIndexOfCreadoEn = 3;
          final int _cursorIndexOfEntregado = 4;
          final int _cursorIndexOfReintentos = 5;
          final List<OutboxEntity> _result = new ArrayList<OutboxEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OutboxEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            final long _tmpCreadoEn;
            _tmpCreadoEn = _cursor.getLong(_cursorIndexOfCreadoEn);
            final boolean _tmpEntregado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEntregado);
            _tmpEntregado = _tmp != 0;
            final int _tmpReintentos;
            _tmpReintentos = _cursor.getInt(_cursorIndexOfReintentos);
            _item = new OutboxEntity(_tmpId,_tmpTipo,_tmpPayload,_tmpCreadoEn,_tmpEntregado,_tmpReintentos);
            _result.add(_item);
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
  public Maybe<OutboxEntity> nextPending() {
    final String _sql = "SELECT `outbox`.`id` AS `id`, `outbox`.`tipo` AS `tipo`, `outbox`.`payload` AS `payload`, `outbox`.`creadoEn` AS `creadoEn`, `outbox`.`entregado` AS `entregado`, `outbox`.`reintentos` AS `reintentos` FROM outbox WHERE entregado = 0 ORDER BY creadoEn ASC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return Maybe.fromCallable(new Callable<OutboxEntity>() {
      @Override
      @Nullable
      public OutboxEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTipo = 1;
          final int _cursorIndexOfPayload = 2;
          final int _cursorIndexOfCreadoEn = 3;
          final int _cursorIndexOfEntregado = 4;
          final int _cursorIndexOfReintentos = 5;
          final OutboxEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            final long _tmpCreadoEn;
            _tmpCreadoEn = _cursor.getLong(_cursorIndexOfCreadoEn);
            final boolean _tmpEntregado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEntregado);
            _tmpEntregado = _tmp != 0;
            final int _tmpReintentos;
            _tmpReintentos = _cursor.getInt(_cursorIndexOfReintentos);
            _result = new OutboxEntity(_tmpId,_tmpTipo,_tmpPayload,_tmpCreadoEn,_tmpEntregado,_tmpReintentos);
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
  public Flowable<List<OutboxEntity>> listPendingByType(final String tipo) {
    final String _sql = "SELECT * FROM outbox WHERE tipo = ? AND entregado = 0 ORDER BY creadoEn ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tipo);
    return RxRoom.createFlowable(__db, false, new String[] {"outbox"}, new Callable<List<OutboxEntity>>() {
      @Override
      @NonNull
      public List<OutboxEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfPayload = CursorUtil.getColumnIndexOrThrow(_cursor, "payload");
          final int _cursorIndexOfCreadoEn = CursorUtil.getColumnIndexOrThrow(_cursor, "creadoEn");
          final int _cursorIndexOfEntregado = CursorUtil.getColumnIndexOrThrow(_cursor, "entregado");
          final int _cursorIndexOfReintentos = CursorUtil.getColumnIndexOrThrow(_cursor, "reintentos");
          final List<OutboxEntity> _result = new ArrayList<OutboxEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OutboxEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final String _tmpPayload;
            _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            final long _tmpCreadoEn;
            _tmpCreadoEn = _cursor.getLong(_cursorIndexOfCreadoEn);
            final boolean _tmpEntregado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfEntregado);
            _tmpEntregado = _tmp != 0;
            final int _tmpReintentos;
            _tmpReintentos = _cursor.getInt(_cursorIndexOfReintentos);
            _item = new OutboxEntity(_tmpId,_tmpTipo,_tmpPayload,_tmpCreadoEn,_tmpEntregado,_tmpReintentos);
            _result.add(_item);
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
