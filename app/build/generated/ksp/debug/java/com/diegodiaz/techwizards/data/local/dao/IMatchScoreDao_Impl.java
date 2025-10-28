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
import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity;
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
        return "INSERT OR REPLACE INTO `match_score` (`id`,`matchId`,`participantId`,`puntos`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchScoreEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMatchId());
        statement.bindLong(3, entity.getParticipantId());
        statement.bindLong(4, entity.getPuntos());
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
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfParticipantId = CursorUtil.getColumnIndexOrThrow(_cursor, "participantId");
          final int _cursorIndexOfPuntos = CursorUtil.getColumnIndexOrThrow(_cursor, "puntos");
          final MatchScoreEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMatchId;
            _tmpMatchId = _cursor.getLong(_cursorIndexOfMatchId);
            final long _tmpParticipantId;
            _tmpParticipantId = _cursor.getLong(_cursorIndexOfParticipantId);
            final int _tmpPuntos;
            _tmpPuntos = _cursor.getInt(_cursorIndexOfPuntos);
            _result = new MatchScoreEntity(_tmpId,_tmpMatchId,_tmpParticipantId,_tmpPuntos);
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
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfParticipantId = CursorUtil.getColumnIndexOrThrow(_cursor, "participantId");
          final int _cursorIndexOfPuntos = CursorUtil.getColumnIndexOrThrow(_cursor, "puntos");
          final List<MatchScoreEntity> _result = new ArrayList<MatchScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchScoreEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMatchId;
            _tmpMatchId = _cursor.getLong(_cursorIndexOfMatchId);
            final long _tmpParticipantId;
            _tmpParticipantId = _cursor.getLong(_cursorIndexOfParticipantId);
            final int _tmpPuntos;
            _tmpPuntos = _cursor.getInt(_cursorIndexOfPuntos);
            _item = new MatchScoreEntity(_tmpId,_tmpMatchId,_tmpParticipantId,_tmpPuntos);
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
