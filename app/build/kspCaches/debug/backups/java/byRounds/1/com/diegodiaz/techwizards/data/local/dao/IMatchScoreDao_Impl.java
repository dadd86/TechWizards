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
import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class IMatchScoreDao_Impl implements IMatchScoreDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MatchScoreEntity> __insertionAdapterOfMatchScoreEntity;

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
  }

  @Override
  public Object upsert(final MatchScoreEntity score, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMatchScoreEntity.insert(score);
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
