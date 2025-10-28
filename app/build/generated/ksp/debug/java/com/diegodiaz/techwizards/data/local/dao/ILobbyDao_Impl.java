package com.diegodiaz.techwizards.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.rxjava3.RxRoom;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
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
public final class ILobbyDao_Impl implements ILobbyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LobbyEntity> __insertionAdapterOfLobbyEntity;

  private final SharedSQLiteStatement __preparedStmtOfCerrarLobby;

  public ILobbyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLobbyEntity = new EntityInsertionAdapter<LobbyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `lobby` (`id`,`nombre`,`capacidad`,`abierta`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LobbyEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindLong(3, entity.getCapacidad());
        final int _tmp = entity.getAbierta() ? 1 : 0;
        statement.bindLong(4, _tmp);
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
  public Flowable<List<LobbyEntity>> getAll() {
    final String _sql = "SELECT `lobby`.`id` AS `id`, `lobby`.`nombre` AS `nombre`, `lobby`.`capacidad` AS `capacidad`, `lobby`.`abierta` AS `abierta` FROM lobby";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[] {"lobby"}, new Callable<List<LobbyEntity>>() {
      @Override
      @NonNull
      public List<LobbyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfNombre = 1;
          final int _cursorIndexOfCapacidad = 2;
          final int _cursorIndexOfAbierta = 3;
          final List<LobbyEntity> _result = new ArrayList<LobbyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LobbyEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final int _tmpCapacidad;
            _tmpCapacidad = _cursor.getInt(_cursorIndexOfCapacidad);
            final boolean _tmpAbierta;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAbierta);
            _tmpAbierta = _tmp != 0;
            _item = new LobbyEntity(_tmpId,_tmpNombre,_tmpCapacidad,_tmpAbierta);
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
