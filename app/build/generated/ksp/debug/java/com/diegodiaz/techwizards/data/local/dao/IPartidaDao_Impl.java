package com.diegodiaz.techwizards.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.rxjava3.RxRoom;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity;
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
public final class IPartidaDao_Impl implements IPartidaDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PartidaEntity> __insertionAdapterOfPartidaEntity;

  public IPartidaDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPartidaEntity = new EntityInsertionAdapter<PartidaEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `partida` (`id`,`usuarioId`,`fecha`,`resultado`,`cambioMonedas`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PartidaEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getUsuarioId());
        statement.bindLong(3, entity.getFecha());
        statement.bindString(4, entity.getResultado());
        statement.bindLong(5, entity.getCambioMonedas());
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
  public Flowable<List<PartidaEntity>> historial(final String usuarioId) {
    final String _sql = "SELECT * FROM partida WHERE usuarioId = ? ORDER BY fecha DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, usuarioId);
    return RxRoom.createFlowable(__db, false, new String[] {"partida"}, new Callable<List<PartidaEntity>>() {
      @Override
      @NonNull
      public List<PartidaEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsuarioId = CursorUtil.getColumnIndexOrThrow(_cursor, "usuarioId");
          final int _cursorIndexOfFecha = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha");
          final int _cursorIndexOfResultado = CursorUtil.getColumnIndexOrThrow(_cursor, "resultado");
          final int _cursorIndexOfCambioMonedas = CursorUtil.getColumnIndexOrThrow(_cursor, "cambioMonedas");
          final List<PartidaEntity> _result = new ArrayList<PartidaEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PartidaEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpUsuarioId;
            _tmpUsuarioId = _cursor.getString(_cursorIndexOfUsuarioId);
            final long _tmpFecha;
            _tmpFecha = _cursor.getLong(_cursorIndexOfFecha);
            final String _tmpResultado;
            _tmpResultado = _cursor.getString(_cursorIndexOfResultado);
            final int _tmpCambioMonedas;
            _tmpCambioMonedas = _cursor.getInt(_cursorIndexOfCambioMonedas);
            _item = new PartidaEntity(_tmpId,_tmpUsuarioId,_tmpFecha,_tmpResultado,_tmpCambioMonedas);
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
