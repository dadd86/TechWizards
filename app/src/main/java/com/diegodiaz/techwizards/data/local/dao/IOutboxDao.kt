package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.OutboxEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

/**
 * DAO para la tabla 'outbox', usada para almacenar operaciones pendientes
 * (sincronización con el servidor u otras tareas asíncronas).
 */
@Dao
interface IOutboxDao {

    @Query("SELECT * FROM outbox")
    fun getAll(): Flowable<List<OutboxEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(item: OutboxEntity): Completable

    @Query("DELETE FROM outbox WHERE id = :id")
    fun eliminarPorId(id: String): Completable

    @Query("DELETE FROM outbox")
    fun borrarTodo()
}
