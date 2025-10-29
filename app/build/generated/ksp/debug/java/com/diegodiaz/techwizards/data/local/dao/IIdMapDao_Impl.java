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
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.IdMapEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
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
public final class IIdMapDao_Impl implements IIdMapDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IdMapEntity> __insertionAdapterOfIdMapEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByLocal;

  public IIdMapDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIdMapEntity = new EntityInsertionAdapter<IdMapEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `IdMap` (`localTable`,`localId`,`remoteCollection`,`remoteId`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IdMapEntity entity) {
        statement.bindString(1, entity.getLocalTable());
        statement.bindString(2, entity.getLocalId());
        statement.bindString(3, entity.getRemoteCollection());
        statement.bindString(4, entity.getRemoteId());
      }
    };
    this.__preparedStmtOfDeleteByLocal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM id_map WHERE type = ? AND localId = ?";
        return _query;
      }
    };
  }

  @Override
  public Completable upsert(final IdMapEntity entity) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIdMapEntity.insert(entity);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable upsertAll(final List<IdMapEntity> list) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIdMapEntity.insert(list);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable deleteByLocal(final String type, final String localId) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByLocal.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, type);
        _argIndex = 2;
        _stmt.bindString(_argIndex, localId);
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
          __preparedStmtOfDeleteByLocal.release(_stmt);
        }
      }
    });
  }

  @Override
  public Maybe<IdMapEntity> findByLocal(final String type, final String localId) {
    final String _sql = "SELECT * FROM id_map WHERE type = ? AND localId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    _argIndex = 2;
    _statement.bindString(_argIndex, localId);
    return Maybe.fromCallable(new Callable<IdMapEntity>() {
      @Override
      @Nullable
      public IdMapEntity call() throws Exception {
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
  public Maybe<IdMapEntity> findByRemote(final String type, final String remoteId) {
    final String _sql = "SELECT * FROM id_map WHERE type = ? AND remoteId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    _argIndex = 2;
    _statement.bindString(_argIndex, remoteId);
    return Maybe.fromCallable(new Callable<IdMapEntity>() {
      @Override
      @Nullable
      public IdMapEntity call() throws Exception {
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
  public Single<List<IdMapEntity>> listByType(final String type) {
    final String _sql = "SELECT * FROM id_map WHERE type = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, type);
    return RxRoom.createSingle(new Callable<List<IdMapEntity>>() {
      @Override
      @Nullable
      public List<IdMapEntity> call() throws Exception {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
