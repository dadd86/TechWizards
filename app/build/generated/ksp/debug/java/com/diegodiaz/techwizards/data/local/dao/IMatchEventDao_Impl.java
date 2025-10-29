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
import com.diegodiaz.techwizards.data.local.entity.MatchEventEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import java.lang.Class;
import java.lang.Exception;
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
public final class IMatchEventDao_Impl implements IMatchEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MatchEventEntity> __insertionAdapterOfMatchEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMatch;

  public IMatchEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMatchEventEntity = new EntityInsertionAdapter<MatchEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `match_event` (`id`,`matchId`,`tipo`,`timestamp`,`actorParticipantId`,`payload`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchEventEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMatchId());
        statement.bindString(3, entity.getTipo());
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getActorParticipantId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getActorParticipantId());
        }
        if (entity.getPayload() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPayload());
        }
      }
    };
    this.__preparedStmtOfDeleteByMatch = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM match_event WHERE matchId = ?";
        return _query;
      }
    };
  }

  @Override
  public Completable upsert(final MatchEventEntity event) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchEventEntity.insert(event);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable upsertAll(final List<MatchEventEntity> events) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchEventEntity.insert(events);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
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
  public Maybe<MatchEventEntity> getById(final long id) {
    final String _sql = "SELECT * FROM match_event WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return Maybe.fromCallable(new Callable<MatchEventEntity>() {
      @Override
      @Nullable
      public MatchEventEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfActorParticipantId = CursorUtil.getColumnIndexOrThrow(_cursor, "actorParticipantId");
          final int _cursorIndexOfPayload = CursorUtil.getColumnIndexOrThrow(_cursor, "payload");
          final MatchEventEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMatchId;
            _tmpMatchId = _cursor.getLong(_cursorIndexOfMatchId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final Long _tmpActorParticipantId;
            if (_cursor.isNull(_cursorIndexOfActorParticipantId)) {
              _tmpActorParticipantId = null;
            } else {
              _tmpActorParticipantId = _cursor.getLong(_cursorIndexOfActorParticipantId);
            }
            final String _tmpPayload;
            if (_cursor.isNull(_cursorIndexOfPayload)) {
              _tmpPayload = null;
            } else {
              _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            }
            _result = new MatchEventEntity(_tmpId,_tmpMatchId,_tmpTipo,_tmpTimestamp,_tmpActorParticipantId,_tmpPayload);
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
  public Flowable<List<MatchEventEntity>> listByMatch(final long matchId) {
    final String _sql = "SELECT * FROM match_event WHERE matchId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, matchId);
    return RxRoom.createFlowable(__db, false, new String[] {"match_event"}, new Callable<List<MatchEventEntity>>() {
      @Override
      @NonNull
      public List<MatchEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfTipo = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfActorParticipantId = CursorUtil.getColumnIndexOrThrow(_cursor, "actorParticipantId");
          final int _cursorIndexOfPayload = CursorUtil.getColumnIndexOrThrow(_cursor, "payload");
          final List<MatchEventEntity> _result = new ArrayList<MatchEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchEventEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMatchId;
            _tmpMatchId = _cursor.getLong(_cursorIndexOfMatchId);
            final String _tmpTipo;
            _tmpTipo = _cursor.getString(_cursorIndexOfTipo);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final Long _tmpActorParticipantId;
            if (_cursor.isNull(_cursorIndexOfActorParticipantId)) {
              _tmpActorParticipantId = null;
            } else {
              _tmpActorParticipantId = _cursor.getLong(_cursorIndexOfActorParticipantId);
            }
            final String _tmpPayload;
            if (_cursor.isNull(_cursorIndexOfPayload)) {
              _tmpPayload = null;
            } else {
              _tmpPayload = _cursor.getString(_cursorIndexOfPayload);
            }
            _item = new MatchEventEntity(_tmpId,_tmpMatchId,_tmpTipo,_tmpTimestamp,_tmpActorParticipantId,_tmpPayload);
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
