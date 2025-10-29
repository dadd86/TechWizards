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
import com.diegodiaz.techwizards.data.local.entity.MatchEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
        return "INSERT OR REPLACE INTO `Match` (`id`,`lobbyId`,`modo`,`estado`,`createdByNum`,`createdAtMs`,`startedAtMs`,`finishedAtMs`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchEntity entity) {
        statement.bindString(1, entity.getId());
        if (entity.getLobbyId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getLobbyId());
        }
        statement.bindString(3, entity.getModo());
        statement.bindString(4, entity.getEstado());
        statement.bindLong(5, entity.getCreatedByNumero());
        statement.bindLong(6, entity.getCreatedAtMs());
        if (entity.getStartedAtMs() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getStartedAtMs());
        }
        if (entity.getFinishedAtMs() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getFinishedAtMs());
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
          final int _cursorIndexOfModo = CursorUtil.getColumnIndexOrThrow(_cursor, "modo");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfCreatedByNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "createdByNum");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfStartedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAtMs");
          final int _cursorIndexOfFinishedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAtMs");
          final MatchEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLobbyId;
            if (_cursor.isNull(_cursorIndexOfLobbyId)) {
              _tmpLobbyId = null;
            } else {
              _tmpLobbyId = _cursor.getString(_cursorIndexOfLobbyId);
            }
            final String _tmpModo;
            _tmpModo = _cursor.getString(_cursorIndexOfModo);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpCreatedByNumero;
            _tmpCreatedByNumero = _cursor.getLong(_cursorIndexOfCreatedByNumero);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final Long _tmpStartedAtMs;
            if (_cursor.isNull(_cursorIndexOfStartedAtMs)) {
              _tmpStartedAtMs = null;
            } else {
              _tmpStartedAtMs = _cursor.getLong(_cursorIndexOfStartedAtMs);
            }
            final Long _tmpFinishedAtMs;
            if (_cursor.isNull(_cursorIndexOfFinishedAtMs)) {
              _tmpFinishedAtMs = null;
            } else {
              _tmpFinishedAtMs = _cursor.getLong(_cursorIndexOfFinishedAtMs);
            }
            _result = new MatchEntity(_tmpId,_tmpLobbyId,_tmpModo,_tmpEstado,_tmpCreatedByNumero,_tmpCreatedAtMs,_tmpStartedAtMs,_tmpFinishedAtMs);
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
    final String _sql = "SELECT * FROM `match` WHERE finEn IS NULL ORDER BY inicioEn DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[] {"match"}, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
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
  public Single<Integer> countOngoing() {
    final String _sql = "SELECT COUNT(*) FROM `match` WHERE inicioEn IS NULL";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createSingle(new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
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
  public Object listarPorEstado(final String estado, final int limite,
      final Continuation<? super List<MatchEntity>> $completion) {
    final String _sql = "SELECT * FROM Match WHERE estado = ? ORDER BY finishedAtMs DESC, createdAtMs DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, estado);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limite);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MatchEntity>>() {
      @Override
      @NonNull
      public List<MatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLobbyId = CursorUtil.getColumnIndexOrThrow(_cursor, "lobbyId");
          final int _cursorIndexOfModo = CursorUtil.getColumnIndexOrThrow(_cursor, "modo");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfCreatedByNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "createdByNum");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfStartedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAtMs");
          final int _cursorIndexOfFinishedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAtMs");
          final List<MatchEntity> _result = new ArrayList<MatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLobbyId;
            if (_cursor.isNull(_cursorIndexOfLobbyId)) {
              _tmpLobbyId = null;
            } else {
              _tmpLobbyId = _cursor.getString(_cursorIndexOfLobbyId);
            }
            final String _tmpModo;
            _tmpModo = _cursor.getString(_cursorIndexOfModo);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpCreatedByNumero;
            _tmpCreatedByNumero = _cursor.getLong(_cursorIndexOfCreatedByNumero);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final Long _tmpStartedAtMs;
            if (_cursor.isNull(_cursorIndexOfStartedAtMs)) {
              _tmpStartedAtMs = null;
            } else {
              _tmpStartedAtMs = _cursor.getLong(_cursorIndexOfStartedAtMs);
            }
            final Long _tmpFinishedAtMs;
            if (_cursor.isNull(_cursorIndexOfFinishedAtMs)) {
              _tmpFinishedAtMs = null;
            } else {
              _tmpFinishedAtMs = _cursor.getLong(_cursorIndexOfFinishedAtMs);
            }
            _item = new MatchEntity(_tmpId,_tmpLobbyId,_tmpModo,_tmpEstado,_tmpCreatedByNumero,_tmpCreatedAtMs,_tmpStartedAtMs,_tmpFinishedAtMs);
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

  @Override
  public Object obtenerPorId(final String matchId,
      final Continuation<? super MatchEntity> $completion) {
    final String _sql = "SELECT * FROM Match WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, matchId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MatchEntity>() {
      @Override
      @Nullable
      public MatchEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfLobbyId = CursorUtil.getColumnIndexOrThrow(_cursor, "lobbyId");
          final int _cursorIndexOfModo = CursorUtil.getColumnIndexOrThrow(_cursor, "modo");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfCreatedByNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "createdByNum");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final int _cursorIndexOfStartedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAtMs");
          final int _cursorIndexOfFinishedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "finishedAtMs");
          final MatchEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpLobbyId;
            if (_cursor.isNull(_cursorIndexOfLobbyId)) {
              _tmpLobbyId = null;
            } else {
              _tmpLobbyId = _cursor.getString(_cursorIndexOfLobbyId);
            }
            final String _tmpModo;
            _tmpModo = _cursor.getString(_cursorIndexOfModo);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpCreatedByNumero;
            _tmpCreatedByNumero = _cursor.getLong(_cursorIndexOfCreatedByNumero);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            final Long _tmpStartedAtMs;
            if (_cursor.isNull(_cursorIndexOfStartedAtMs)) {
              _tmpStartedAtMs = null;
            } else {
              _tmpStartedAtMs = _cursor.getLong(_cursorIndexOfStartedAtMs);
            }
            final Long _tmpFinishedAtMs;
            if (_cursor.isNull(_cursorIndexOfFinishedAtMs)) {
              _tmpFinishedAtMs = null;
            } else {
              _tmpFinishedAtMs = _cursor.getLong(_cursorIndexOfFinishedAtMs);
            }
            _result = new MatchEntity(_tmpId,_tmpLobbyId,_tmpModo,_tmpEstado,_tmpCreatedByNumero,_tmpCreatedAtMs,_tmpStartedAtMs,_tmpFinishedAtMs);
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
