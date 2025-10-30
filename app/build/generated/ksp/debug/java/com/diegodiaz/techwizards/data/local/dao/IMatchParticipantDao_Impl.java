package com.diegodiaz.techwizards.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IMatchParticipantDao_Impl implements IMatchParticipantDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MatchParticipantEntity> __insertionAdapterOfMatchParticipantEntity;

  public IMatchParticipantDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMatchParticipantEntity = new EntityInsertionAdapter<MatchParticipantEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `MatchParticipant` (`matchId`,`usuarioNum`,`rol`,`teamId`,`joinedAtMs`,`leftAtMs`,`score`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MatchParticipantEntity entity) {
        statement.bindString(1, entity.getMatchId());
        statement.bindLong(2, entity.getUsuarioNumero());
        if (entity.getRol() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getRol());
        }
        if (entity.getTeamId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTeamId());
        }
        statement.bindLong(5, entity.getJoinedAtMs());
        if (entity.getLeftAtMs() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getLeftAtMs());
        }
        statement.bindLong(7, entity.getScore());
      }
    };
  }

  @Override
  public Object upsert(final MatchParticipantEntity participante,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchParticipantEntity.insert(participante);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object listarPorMatch(final String matchId,
      final Continuation<? super List<MatchParticipantEntity>> $completion) {
    final String _sql = "SELECT * FROM MatchParticipant WHERE matchId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, matchId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MatchParticipantEntity>>() {
      @Override
      @NonNull
      public List<MatchParticipantEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfUsuarioNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioNum");
          final int _cursorIndexOfRol = CursorUtil.getColumnIndexOrThrow(_cursor, "rol");
          final int _cursorIndexOfTeamId = CursorUtil.getColumnIndexOrThrow(_cursor, "teamId");
          final int _cursorIndexOfJoinedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAtMs");
          final int _cursorIndexOfLeftAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "leftAtMs");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final List<MatchParticipantEntity> _result = new ArrayList<MatchParticipantEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MatchParticipantEntity _item;
            final String _tmpMatchId;
            _tmpMatchId = _cursor.getString(_cursorIndexOfMatchId);
            final long _tmpUsuarioNumero;
            _tmpUsuarioNumero = _cursor.getLong(_cursorIndexOfUsuarioNumero);
            final String _tmpRol;
            if (_cursor.isNull(_cursorIndexOfRol)) {
              _tmpRol = null;
            } else {
              _tmpRol = _cursor.getString(_cursorIndexOfRol);
            }
            final String _tmpTeamId;
            if (_cursor.isNull(_cursorIndexOfTeamId)) {
              _tmpTeamId = null;
            } else {
              _tmpTeamId = _cursor.getString(_cursorIndexOfTeamId);
            }
            final long _tmpJoinedAtMs;
            _tmpJoinedAtMs = _cursor.getLong(_cursorIndexOfJoinedAtMs);
            final Long _tmpLeftAtMs;
            if (_cursor.isNull(_cursorIndexOfLeftAtMs)) {
              _tmpLeftAtMs = null;
            } else {
              _tmpLeftAtMs = _cursor.getLong(_cursorIndexOfLeftAtMs);
            }
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            _item = new MatchParticipantEntity(_tmpMatchId,_tmpUsuarioNumero,_tmpRol,_tmpTeamId,_tmpJoinedAtMs,_tmpLeftAtMs,_tmpScore);
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
