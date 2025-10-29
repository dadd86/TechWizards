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
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ILobbyDao_Impl implements ILobbyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LobbyEntity> __insertionAdapterOfLobbyEntity;

  private final SharedSQLiteStatement __preparedStmtOfCerrarLobby;

  private final SharedSQLiteStatement __preparedStmtOfActualizarEstado;

  public ILobbyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLobbyEntity = new EntityInsertionAdapter<LobbyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `Lobby` (`nombre`,`id`,`codigo`,`modo`,`estado`,`creadorNum`,`createdAtMs`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LobbyEntity entity) {
        statement.bindString(1, entity.getNombre());
        statement.bindString(2, entity.getId());
        if (entity.getCodigo() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCodigo());
        }
        statement.bindString(4, entity.getModo());
        statement.bindString(5, entity.getEstado());
        statement.bindLong(6, entity.getCreadorNumero());
        statement.bindLong(7, entity.getCreatedAtMs());
      }
    };
    this.__preparedStmtOfCerrarLobby = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE lobby SET abierta = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActualizarEstado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE Lobby SET estado = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Completable insert(final LobbyEntity lobby) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLobbyEntity.insert(lobby);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Object upsert(final LobbyEntity lobby, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLobbyEntity.insert(lobby);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Completable cerrarLobby(final String lobbyId) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfCerrarLobby.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, lobbyId);
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
          __preparedStmtOfCerrarLobby.release(_stmt);
        }
      }
    });
  }

  @Override
  public Object actualizarEstado(final String lobbyId, final String estado,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActualizarEstado.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, estado);
        _argIndex = 2;
        _stmt.bindString(_argIndex, lobbyId);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActualizarEstado.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flowable<List<LobbyEntity>> getAll() {
    final String _sql = "SELECT `lobby`.`nombre` AS `nombre`, `lobby`.`id` AS `id`, `lobby`.`codigo` AS `codigo`, `lobby`.`modo` AS `modo`, `lobby`.`estado` AS `estado`, `lobby`.`creadorNum` AS `creadorNum`, `lobby`.`createdAtMs` AS `createdAtMs` FROM lobby";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[] {"lobby"}, new Callable<List<LobbyEntity>>() {
      @Override
      @NonNull
      public List<LobbyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNombre = 0;
          final int _cursorIndexOfId = 1;
          final int _cursorIndexOfCodigo = 2;
          final int _cursorIndexOfModo = 3;
          final int _cursorIndexOfEstado = 4;
          final int _cursorIndexOfCreadorNumero = 5;
          final int _cursorIndexOfCreatedAtMs = 6;
          final List<LobbyEntity> _result = new ArrayList<LobbyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LobbyEntity _item;
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpCodigo;
            if (_cursor.isNull(_cursorIndexOfCodigo)) {
              _tmpCodigo = null;
            } else {
              _tmpCodigo = _cursor.getString(_cursorIndexOfCodigo);
            }
            final String _tmpModo;
            _tmpModo = _cursor.getString(_cursorIndexOfModo);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpCreadorNumero;
            _tmpCreadorNumero = _cursor.getLong(_cursorIndexOfCreadorNumero);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _item = new LobbyEntity(_tmpNombre,_tmpId,_tmpCodigo,_tmpModo,_tmpEstado,_tmpCreadorNumero,_tmpCreatedAtMs);
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
  public Object obtenerPorId(final String lobbyId,
      final Continuation<? super LobbyEntity> $completion) {
    final String _sql = "SELECT * FROM Lobby WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, lobbyId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LobbyEntity>() {
      @Override
      @Nullable
      public LobbyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCodigo = CursorUtil.getColumnIndexOrThrow(_cursor, "codigo");
          final int _cursorIndexOfModo = CursorUtil.getColumnIndexOrThrow(_cursor, "modo");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfCreadorNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "creadorNum");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final LobbyEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpCodigo;
            if (_cursor.isNull(_cursorIndexOfCodigo)) {
              _tmpCodigo = null;
            } else {
              _tmpCodigo = _cursor.getString(_cursorIndexOfCodigo);
            }
            final String _tmpModo;
            _tmpModo = _cursor.getString(_cursorIndexOfModo);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpCreadorNumero;
            _tmpCreadorNumero = _cursor.getLong(_cursorIndexOfCreadorNumero);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _result = new LobbyEntity(_tmpNombre,_tmpId,_tmpCodigo,_tmpModo,_tmpEstado,_tmpCreadorNumero,_tmpCreatedAtMs);
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

  @Override
  public Object listarPorEstado(final String estado, final int limite,
      final Continuation<? super List<LobbyEntity>> $completion) {
    final String _sql = "SELECT * FROM Lobby WHERE estado = ? ORDER BY createdAtMs DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, estado);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limite);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LobbyEntity>>() {
      @Override
      @NonNull
      public List<LobbyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfNombre = CursorUtil.getColumnIndexOrThrow(_cursor, "nombre");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCodigo = CursorUtil.getColumnIndexOrThrow(_cursor, "codigo");
          final int _cursorIndexOfModo = CursorUtil.getColumnIndexOrThrow(_cursor, "modo");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final int _cursorIndexOfCreadorNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "creadorNum");
          final int _cursorIndexOfCreatedAtMs = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMs");
          final List<LobbyEntity> _result = new ArrayList<LobbyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LobbyEntity _item;
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpCodigo;
            if (_cursor.isNull(_cursorIndexOfCodigo)) {
              _tmpCodigo = null;
            } else {
              _tmpCodigo = _cursor.getString(_cursorIndexOfCodigo);
            }
            final String _tmpModo;
            _tmpModo = _cursor.getString(_cursorIndexOfModo);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            final long _tmpCreadorNumero;
            _tmpCreadorNumero = _cursor.getLong(_cursorIndexOfCreadorNumero);
            final long _tmpCreatedAtMs;
            _tmpCreatedAtMs = _cursor.getLong(_cursorIndexOfCreatedAtMs);
            _item = new LobbyEntity(_tmpNombre,_tmpId,_tmpCodigo,_tmpModo,_tmpEstado,_tmpCreadorNumero,_tmpCreatedAtMs);
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
