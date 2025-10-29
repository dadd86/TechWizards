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
import com.diegodiaz.techwizards.data.local.entity.OutboxEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.lang.Void;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IOutboxDao_Impl implements IOutboxDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OutboxEntity> __insertionAdapterOfOutboxEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkDelivered;

  private final SharedSQLiteStatement __preparedStmtOfIncrementRetry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfPurgeDelivered;

  private final SharedSQLiteStatement __preparedStmtOfActualizarIntento;

  public IOutboxDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOutboxEntity = new EntityInsertionAdapter<OutboxEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `Outbox` (`operationId`,`entityType`,`entityId`,`op`,`payloadJson`,`attempt`,`lastError`,`createdAtMs`,`updatedAtMs`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OutboxEntity entity) {
        statement.bindString(1, entity.getOperationId());
        statement.bindString(2, entity.getEntityType());
        statement.bindString(3, entity.getEntityId());
        statement.bindString(4, entity.getOp());
        statement.bindString(5, entity.getPayloadJson());
        statement.bindLong(6, entity.getAttempt());
        if (entity.getLastError() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getLastError());
        }
        statement.bindLong(8, entity.getCreatedAtMs());
        statement.bindLong(9, entity.getUpdatedAtMs());
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
    this.__preparedStmtOfActualizarIntento = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE Outbox SET attempt = ?, lastError = ?, updatedAtMs = ? WHERE operationId = ?";
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
  public Object actualizarIntento(final String operationId, final int attempt,
      final String lastError, final long updatedAtMs,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarIntento.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, attempt);
        _argIndex = 2;
        if (lastError == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, lastError);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, updatedAtMs);
        _argIndex = 4;
        _stmt.bindString(_argIndex, operationId);
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
          __preparedStmtOfActualizarIntento.release(_stmt);
        }
      }
    }, $completion);
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
    final String _sql = "SELECT * FROM outbox WHERE entregado = 0 ORDER BY creadoEn ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[] {"outbox"}, new Callable<List<OutboxEntity>>() {
      @Override
      @NonNull
      public List<OutboxEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
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
    final String _sql = "SELECT * FROM outbox WHERE entregado = 0 ORDER BY creadoEn ASC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return Maybe.fromCallable(new Callable<OutboxEntity>() {
      @Override
      @Nullable
      public OutboxEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
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
  public Object obtenerPendientes(final int limit,
      final Continuation<? super List<OutboxEntity>> $completion) {
    final String _sql = "SELECT * FROM Outbox ORDER BY createdAtMs ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OutboxEntity>>() {
      @Override
      @NonNull
      public List<OutboxEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOperationId = CursorUtil.getColumnIndexOrThrow(_cursor, "operationId");
          final int _cursorIndexOfEntityType = CursorUtil.getColumnIndexOrThrow(_cursor, "entityType");
          final int _cursorIndexOfEntityId = CursorUtil.getColumnIndexOrThrow(_cursor, "entityId");
          final int _cursorIndexOfOp = CursorUtil.getColumnIndexOrThrow(_cursor, "op");
          final int _cursorIndexOfPayloadJson = CursorUtil.getColumnIndexOrThrow(_cursor, "payloadJson");
          final int _cursorIndexOfAttempt = CursorUtil.getColumnIndexOrThrow(_cursor, "attempt");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfUpdatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtMs");
          final List<OutboxEntity> _result = new ArrayList<OutboxEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OutboxEntity _item;
            final String _tmpOperationId;
            _tmpOperationId = _cursor.getString(_cursorIndexOfOperationId);
            final String _tmpEntityType;
            _tmpEntityType = _cursor.getString(_cursorIndexOfEntityType);
            final String _tmpEntityId;
            _tmpEntityId = _cursor.getString(_cursorIndexOfEntityId);
            final String _tmpOp;
            _tmpOp = _cursor.getString(_cursorIndexOfOp);
            final String _tmpPayloadJson;
            _tmpPayloadJson = _cursor.getString(_cursorIndexOfPayloadJson);
            final int _tmpAttempt;
            _tmpAttempt = _cursor.getInt(_cursorIndexOfAttempt);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final long _tmpUpdatedAtMs;
            _tmpUpdatedAtMs = _cursor.getLong(_cursorIndexOfUpdatedAtMs);
            _item = new OutboxEntity(_tmpOperationId,_tmpEntityType,_tmpEntityId,_tmpOp,_tmpPayloadJson,_tmpAttempt,_tmpLastError,_tmpCreatedAtMs,_tmpUpdatedAtMs);
            _result.add(_item);
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
