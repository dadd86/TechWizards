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
import androidx.room.rxjava3.EmptyResultSetException;
import androidx.room.rxjava3.RxRoom;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.TombstoneEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.lang.Class;
import java.lang.Exception;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ITombstoneDao_Impl implements ITombstoneDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TombstoneEntity> __insertionAdapterOfTombstoneEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOne;

  private final SharedSQLiteStatement __preparedStmtOfClearAll;

  public ITombstoneDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTombstoneEntity = new EntityInsertionAdapter<TombstoneEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `Tombstone` (`tableName`,`entityId`,`deletedAtMs`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TombstoneEntity entity) {
        statement.bindString(1, entity.getTableName());
        statement.bindString(2, entity.getEntityId());
        statement.bindLong(3, entity.getDeletedAtMs());
      }
    };
    this.__preparedStmtOfDeleteOne = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tombstone WHERE type = ? AND deletedId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tombstone";
        return _query;
      }
    };
  }

  @Override
  public Completable insert(final TombstoneEntity entity) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTombstoneEntity.insert(entity);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable insertAll(final List<TombstoneEntity> list) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTombstoneEntity.insert(list);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Object upsert(final TombstoneEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTombstoneEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Completable deleteOne(final String type, final String deletedId) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOne.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, type);
        _argIndex = 2;
        _stmt.bindString(_argIndex, deletedId);
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
          __preparedStmtOfDeleteOne.release(_stmt);
        }
      }
    });
  }

  @Override
  public Completable clearAll() {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAll.acquire();
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
          __preparedStmtOfClearAll.release(_stmt);
        }
      }
    });
  }

  @Override
  public Maybe<TombstoneEntity> getById(final long id) {
    final String _sql = "SELECT * FROM tombstone WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return Maybe.fromCallable(new Callable<TombstoneEntity>() {
      @Override
      @Nullable
      public TombstoneEntity call() throws Exception {
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
  public Flowable<List<TombstoneEntity>> streamByType(final String type) {
    final String _sql = "SELECT * FROM tombstone WHERE type = ? ORDER BY deletedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    return RxRoom.createFlowable(__db, false, new String[] {"tombstone"}, new Callable<List<TombstoneEntity>>() {
      @Override
      @NonNull
      public List<TombstoneEntity> call() throws Exception {
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
  public Single<List<TombstoneEntity>> listAll() {
    final String _sql = "SELECT * FROM tombstone ORDER BY deletedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createSingle(new Callable<List<TombstoneEntity>>() {
      @Override
      @Nullable
      public List<TombstoneEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          if (_result == null) {
            throw new EmptyResultSetException("Query returned empty result set: " + _statement.getSql());
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
  public Object listarPorTabla(final String tableName,
      final Continuation<? super List<TombstoneEntity>> $completion) {
    final String _sql = "SELECT * FROM Tombstone WHERE tableName = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, tableName);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TombstoneEntity>>() {
      @Override
      @NonNull
      public List<TombstoneEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTableName = CursorUtil.getColumnIndexOrThrow(_cursor, "tableName");
          final int _cursorIndexOfEntityId = CursorUtil.getColumnIndexOrThrow(_cursor, "entityId");
          final int _cursorIndexOfDeletedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAtMs");
          final List<TombstoneEntity> _result = new ArrayList<TombstoneEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TombstoneEntity _item;
            final String _tmpTableName;
            _tmpTableName = _cursor.getString(_cursorIndexOfTableName);
            final String _tmpEntityId;
            _tmpEntityId = _cursor.getString(_cursorIndexOfEntityId);
            final long _tmpDeletedAtMs;
            _tmpDeletedAtMs = _cursor.getLong(_cursorIndexOfDeletedAtMs);
            _item = new TombstoneEntity(_tmpTableName,_tmpEntityId,_tmpDeletedAtMs);
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
