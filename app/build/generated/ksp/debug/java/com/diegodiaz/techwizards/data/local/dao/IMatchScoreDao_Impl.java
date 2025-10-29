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
import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
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
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IMatchScoreDao_Impl implements IMatchScoreDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MatchScoreEntity> __insertionAdapterOfMatchScoreEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateScore;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMatch;

  public IMatchScoreDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMatchScoreEntity = new EntityInsertionAdapter<MatchScoreEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `MatchScore` (`matchId`,`usuarioNum`,`score`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchScoreEntity entity) {
        statement.bindString(1, entity.getMatchId());
        statement.bindLong(2, entity.getUsuarioNumero());
        statement.bindLong(3, entity.getScore());
      }
    };
    this.__preparedStmtOfUpdateScore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE match_score SET puntos = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM match_score WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteByMatch = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM match_score WHERE matchId = ?";
        return _query;
      }
    };
  }

  @Override
  public Completable upsert(final MatchScoreEntity entity) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchScoreEntity.insert(entity);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable upsertAll(final List<MatchScoreEntity> list) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchScoreEntity.insert(list);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable updateScore(final long id, final int points) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateScore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, points);
        _argIndex = 2;
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
          __preparedStmtOfUpdateScore.release(_stmt);
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
  public Completable deleteByMatch(final long matchId) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMatch.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, matchId);
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
          __preparedStmtOfDeleteByMatch.release(_stmt);
        }
      }
    });
  }

  @Override
  public Maybe<MatchScoreEntity> getById(final long id) {
    final String _sql = "SELECT * FROM match_score WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return Maybe.fromCallable(new Callable<MatchScoreEntity>() {
      @Override
      @Nullable
      public MatchScoreEntity call() throws Exception {
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
  public Flowable<List<MatchScoreEntity>> listByMatch(final long matchId) {
    final String _sql = "SELECT * FROM match_score WHERE matchId = ? ORDER BY puntos DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, matchId);
    return RxRoom.createFlowable(__db, false, new String[] {"match_score"}, new Callable<List<MatchScoreEntity>>() {
      @Override
      @NonNull
      public List<MatchScoreEntity> call() throws Exception {
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
  public Object listarPorMatch(final String matchId,
      final Continuation<? super List<MatchScoreEntity>> $completion) {
    final String _sql = "SELECT * FROM MatchScore WHERE matchId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, matchId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MatchScoreEntity>>() {
      @Override
      @NonNull
      public List<MatchScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfUsuarioNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioNum");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final List<MatchScoreEntity> _result = new ArrayList<MatchScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchScoreEntity _item;
            final String _tmpMatchId;
            _tmpMatchId = _cursor.getString(_cursorIndexOfMatchId);
            final long _tmpUsuarioNumero;
            _tmpUsuarioNumero = _cursor.getLong(_cursorIndexOfUsuarioNumero);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            _item = new MatchScoreEntity(_tmpMatchId,_tmpUsuarioNumero,_tmpScore);
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
