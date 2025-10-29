package com.diegodiaz.techwizards.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
        return "INSERT OR REPLACE INTO `tombstone` (`id`,`type`,`deletedId`,`deletedAt`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TombstoneEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getType());
        statement.bindString(3, entity.getDeletedId());
        statement.bindLong(4, entity.getDeletedAt());
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
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDeletedId = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final TombstoneEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpDeletedId;
            _tmpDeletedId = _cursor.getString(_cursorIndexOfDeletedId);
            final long _tmpDeletedAt;
            _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            _result = new TombstoneEntity(_tmpId,_tmpType,_tmpDeletedId,_tmpDeletedAt);
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
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDeletedId = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedId");
          final int _cursorIndexOfDeletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deletedAt");
          final List<TombstoneEntity> _result = new ArrayList<TombstoneEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TombstoneEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpDeletedId;
            _tmpDeletedId = _cursor.getString(_cursorIndexOfDeletedId);
            final long _tmpDeletedAt;
            _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            _item = new TombstoneEntity(_tmpId,_tmpType,_tmpDeletedId,_tmpDeletedAt);
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
  public Single<List<TombstoneEntity>> listAll() {
    final String _sql = "SELECT `tombstone`.`id` AS `id`, `tombstone`.`type` AS `type`, `tombstone`.`deletedId` AS `deletedId`, `tombstone`.`deletedAt` AS `deletedAt` FROM tombstone ORDER BY deletedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createSingle(new Callable<List<TombstoneEntity>>() {
      @Override
      @Nullable
      public List<TombstoneEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfType = 1;
          final int _cursorIndexOfDeletedId = 2;
          final int _cursorIndexOfDeletedAt = 3;
          final List<TombstoneEntity> _result = new ArrayList<TombstoneEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TombstoneEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpDeletedId;
            _tmpDeletedId = _cursor.getString(_cursorIndexOfDeletedId);
            final long _tmpDeletedAt;
            _tmpDeletedAt = _cursor.getLong(_cursorIndexOfDeletedAt);
            _item = new TombstoneEntity(_tmpId,_tmpType,_tmpDeletedId,_tmpDeletedAt);
            _result.add(_item);
          }
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
