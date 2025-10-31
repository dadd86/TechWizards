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
import com.diegodiaz.techwizards.data.local.EnumConverters;
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity;
import com.diegodiaz.techwizards.domain.model.Resultado;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
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
public final class IPartidaDao_Impl implements IPartidaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PartidaEntity> __insertionAdapterOfPartidaEntity;

  private final EnumConverters __enumConverters = new EnumConverters();

  private final SharedSQLiteStatement __preparedStmtOfBorrarTodo;

  public IPartidaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPartidaEntity = new EntityInsertionAdapter<PartidaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `Partida` (`id`,`usuarioNumero`,`fecha`,`resultado`,`cambioMonedas`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PartidaEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUsuarioNumero());
        statement.bindLong(3, entity.getFecha());
        final String _tmp = __enumConverters.fromResultado(entity.getResultado());
        if (_tmp == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp);
        }
        statement.bindLong(5, entity.getCambioMonedas());
      }
    };
    this.__preparedStmtOfBorrarTodo = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM Partida";
        return _query;
      }
    };
  }

  @Override
  public Completable insertar(final PartidaEntity entity) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPartidaEntity.insert(entity);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable borrarTodo() {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfBorrarTodo.acquire();
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
          __preparedStmtOfBorrarTodo.release(_stmt);
        }
      }
    });
  }

  @Override
  public Flowable<List<PartidaEntity>> historial(final long usuarioNumero) {
    final String _sql = "SELECT * FROM Partida WHERE usuarioNumero = ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, usuarioNumero);
    return RxRoom.createFlowable(__db, false, new String[] {"Partida"}, new Callable<List<PartidaEntity>>() {
      @Override
      @NonNull
      public List<PartidaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioNumero = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioNumero");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfResultado = CursorUtil.getColumnIndexOrThrow(_cursor, "resultado");
          final int _cursorIndexOfCambioMonedas = CursorUtil.getColumnIndexOrThrow(_cursor, "cambioMonedas");
          final List<PartidaEntity> _result = new ArrayList<PartidaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PartidaEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUsuarioNumero;
            _tmpUsuarioNumero = _cursor.getLong(_cursorIndexOfUsuarioNumero);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final Resultado _tmpResultado;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfResultado)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfResultado);
            }
            final Resultado _tmp_1 = __enumConverters.toResultado(_tmp);
            if (_tmp_1 == null) {
              throw new IllegalStateException("Expected NON-NULL 'com.diegodiaz.techwizards.domain.model.Resultado', but it was NULL.");
            } else {
              _tmpResultado = _tmp_1;
            }
            final int _tmpCambioMonedas;
            _tmpCambioMonedas = _cursor.getInt(_cursorIndexOfCambioMonedas);
            _item = new PartidaEntity(_tmpId,_tmpUsuarioNumero,_tmpFecha,_tmpResultado,_tmpCambioMonedas);
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
