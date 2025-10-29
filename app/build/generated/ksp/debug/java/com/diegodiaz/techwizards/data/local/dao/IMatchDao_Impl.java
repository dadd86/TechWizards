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
import com.diegodiaz.techwizards.data.local.entity.MatchEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class IMatchDao_Impl implements IMatchDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MatchEntity> __insertionAdapterOfMatchEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkFinished;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public IMatchDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMatchEntity = new EntityInsertionAdapter<MatchEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `match` (`id`,`lobbyId`,`status`,`inicioEn`,`finEn`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getLobbyId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindLong(2, entity.getLobbyId());
        }
        statement.bindString(3, entity.getStatus());
        statement.bindLong(4, entity.getInicioEn());
        if (entity.getFinEn() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getFinEn());
        }
      }
    };
    this.__preparedStmtOfMarkFinished = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE `match` SET finEn = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM `match` WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Completable upsert(final MatchEntity entity) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchEntity.insert(entity);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable upsertAll(final List<MatchEntity> list) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchEntity.insert(list);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable markFinished(final String id, final long finishedAt) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkFinished.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, finishedAt);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfMarkFinished.release(_stmt);
        }
      }
    });
  }

  @Override
  public Completable deleteById(final String id) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
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
  public Maybe<MatchEntity> getById(final String id) {
    final String _sql = "SELECT * FROM `match` WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    return Maybe.fromCallable(new Callable<MatchEntity>() {
      @Override
      @Nullable
      public MatchEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLobbyId = CursorUtil.getColumnIndexOrThrow(_cursor, "lobbyId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInicioEn = CursorUtil.getColumnIndexOrThrow(_cursor, "inicioEn");
          final int _cursorIndexOfFinEn = CursorUtil.getColumnIndexOrThrow(_cursor, "finEn");
          final MatchEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpLobbyId;
            if (_cursor.isNull(_cursorIndexOfLobbyId)) {
              _tmpLobbyId = null;
            } else {
              _tmpLobbyId = _cursor.getLong(_cursorIndexOfLobbyId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInicioEn;
            _tmpInicioEn = _cursor.getLong(_cursorIndexOfInicioEn);
            final Long _tmpFinEn;
            if (_cursor.isNull(_cursorIndexOfFinEn)) {
              _tmpFinEn = null;
            } else {
              _tmpFinEn = _cursor.getLong(_cursorIndexOfFinEn);
            }
            _result = new MatchEntity(_tmpId,_tmpLobbyId,_tmpStatus,_tmpInicioEn,_tmpFinEn);
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
  public Flowable<List<MatchEntity>> listByLobby(final String lobbyId) {
    final String _sql = "SELECT * FROM `match` WHERE lobbyId = ? ORDER BY inicioEn DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lobbyId);
    return RxRoom.createFlowable(__db, false, new String[] {"match"}, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLobbyId = CursorUtil.getColumnIndexOrThrow(_cursor, "lobbyId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfInicioEn = CursorUtil.getColumnIndexOrThrow(_cursor, "inicioEn");
          final int _cursorIndexOfFinEn = CursorUtil.getColumnIndexOrThrow(_cursor, "finEn");
          final List<MatchEntity> _result = new ArrayList<MatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpLobbyId;
            if (_cursor.isNull(_cursorIndexOfLobbyId)) {
              _tmpLobbyId = null;
            } else {
              _tmpLobbyId = _cursor.getLong(_cursorIndexOfLobbyId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInicioEn;
            _tmpInicioEn = _cursor.getLong(_cursorIndexOfInicioEn);
            final Long _tmpFinEn;
            if (_cursor.isNull(_cursorIndexOfFinEn)) {
              _tmpFinEn = null;
            } else {
              _tmpFinEn = _cursor.getLong(_cursorIndexOfFinEn);
            }
            _item = new MatchEntity(_tmpId,_tmpLobbyId,_tmpStatus,_tmpInicioEn,_tmpFinEn);
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
  public Flowable<List<MatchEntity>> listOngoing() {
    final String _sql = "SELECT `match`.`id` AS `id`, `match`.`lobbyId` AS `lobbyId`, `match`.`status` AS `status`, `match`.`inicioEn` AS `inicioEn`, `match`.`finEn` AS `finEn` FROM `match` WHERE finEn IS NULL ORDER BY inicioEn DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[] {"match"}, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfLobbyId = 1;
          final int _cursorIndexOfStatus = 2;
          final int _cursorIndexOfInicioEn = 3;
          final int _cursorIndexOfFinEn = 4;
          final List<MatchEntity> _result = new ArrayList<MatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final Long _tmpLobbyId;
            if (_cursor.isNull(_cursorIndexOfLobbyId)) {
              _tmpLobbyId = null;
            } else {
              _tmpLobbyId = _cursor.getLong(_cursorIndexOfLobbyId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpInicioEn;
            _tmpInicioEn = _cursor.getLong(_cursorIndexOfInicioEn);
            final Long _tmpFinEn;
            if (_cursor.isNull(_cursorIndexOfFinEn)) {
              _tmpFinEn = null;
            } else {
              _tmpFinEn = _cursor.getLong(_cursorIndexOfFinEn);
            }
            _item = new MatchEntity(_tmpId,_tmpLobbyId,_tmpStatus,_tmpInicioEn,_tmpFinEn);
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
  public Single<Integer> countOngoing() {
    final String _sql = "SELECT COUNT(*) FROM `match` WHERE inicioEn IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createSingle(new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
