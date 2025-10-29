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
import com.diegodiaz.techwizards.data.local.entity.EventoEntity;
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
public final class IEventoDao_Impl implements IEventoDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EventoEntity> __insertionAdapterOfEventoEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarcarCompletado;

  public IEventoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEventoEntity = new EntityInsertionAdapter<EventoEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `evento` (`id`,`nombre`,`descripcion`,`fechaInicio`,`fechaFin`,`completado`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EventoEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getNombre());
        statement.bindString(3, entity.getDescripcion());
        statement.bindLong(4, entity.getFechaInicio());
        statement.bindLong(5, entity.getFechaFin());
        final int _tmp = entity.getCompletado() ? 1 : 0;
        statement.bindLong(6, _tmp);
      }
    };
    this.__preparedStmtOfMarcarCompletado = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE evento SET completado = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Completable insert(final EventoEntity evento) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEventoEntity.insert(evento);
          __db.setTransactionSuccessful();
          return null;
        } finally {
          __db.endTransaction();
        }
      }
    });
  }

  @Override
  public Completable marcarCompletado(final String eventoId) {
    return Completable.fromCallable(new Callable<Void>() {
      @Override
      @Nullable
      public Void call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarcarCompletado.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, eventoId);
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
          __preparedStmtOfMarcarCompletado.release(_stmt);
        }
      }
    });
  }

  @Override
  public Flowable<List<EventoEntity>> getEventos() {
    final String _sql = "SELECT `evento`.`id` AS `id`, `evento`.`nombre` AS `nombre`, `evento`.`descripcion` AS `descripcion`, `evento`.`fechaInicio` AS `fechaInicio`, `evento`.`fechaFin` AS `fechaFin`, `evento`.`completado` AS `completado` FROM evento ORDER BY fechaInicio ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return RxRoom.createFlowable(__db, false, new String[] {"evento"}, new Callable<List<EventoEntity>>() {
      @Override
      @NonNull
      public List<EventoEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfNombre = 1;
          final int _cursorIndexOfDescripcion = 2;
          final int _cursorIndexOfFechaInicio = 3;
          final int _cursorIndexOfFechaFin = 4;
          final int _cursorIndexOfCompletado = 5;
          final List<EventoEntity> _result = new ArrayList<EventoEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EventoEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpNombre;
            _tmpNombre = _cursor.getString(_cursorIndexOfNombre);
            final String _tmpDescripcion;
            _tmpDescripcion = _cursor.getString(_cursorIndexOfDescripcion);
            final long _tmpFechaInicio;
            _tmpFechaInicio = _cursor.getLong(_cursorIndexOfFechaInicio);
            final long _tmpFechaFin;
            _tmpFechaFin = _cursor.getLong(_cursorIndexOfFechaFin);
            final boolean _tmpCompletado;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompletado);
            _tmpCompletado = _tmp != 0;
            _item = new EventoEntity(_tmpId,_tmpNombre,_tmpDescripcion,_tmpFechaInicio,_tmpFechaFin,_tmpCompletado);
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
