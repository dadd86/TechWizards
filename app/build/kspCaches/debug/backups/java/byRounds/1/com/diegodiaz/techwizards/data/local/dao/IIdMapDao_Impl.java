package com.diegodiaz.techwizards.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.IdMapEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class IIdMapDao_Impl implements IIdMapDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IdMapEntity> __insertionAdapterOfIdMapEntity;

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
  }

  @Override
  public Object upsert(final IdMapEntity entity, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIdMapEntity.insert(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object obtener(final String localTable, final String localId,
      final Continuation<? super IdMapEntity> $completion) {
    final String _sql = "SELECT * FROM IdMap WHERE localTable = ? AND localId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, localTable);
    _argIndex = 2;
    _statement.bindString(_argIndex, localId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IdMapEntity>() {
      @Override
      @Nullable
      public IdMapEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalTable = CursorUtil.getColumnIndexOrThrow(_cursor, "localTable");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfRemoteCollection = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteCollection");
          final int _cursorIndexOfRemoteId = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteId");
          final IdMapEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpLocalTable;
            _tmpLocalTable = _cursor.getString(_cursorIndexOfLocalTable);
            final String _tmpLocalId;
            _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            final String _tmpRemoteCollection;
            _tmpRemoteCollection = _cursor.getString(_cursorIndexOfRemoteCollection);
            final String _tmpRemoteId;
            _tmpRemoteId = _cursor.getString(_cursorIndexOfRemoteId);
            _result = new IdMapEntity(_tmpLocalTable,_tmpLocalId,_tmpRemoteCollection,_tmpRemoteId);
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
