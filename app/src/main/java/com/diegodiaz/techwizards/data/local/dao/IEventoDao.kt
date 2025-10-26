package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.EventoEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface IEventoDao {

    /** Devuelve todos los eventos registrados. */
    @Query("SELECT * FROM evento ORDER BY fechaInicio ASC")
    fun getAll(): Flowable<List<EventoEntity>>

    /** Inserta o actualiza un evento en la base de datos. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(evento: EventoEntity): Completable

    /** Marca un evento como completado para un usuario concreto. */
    @Query("UPDATE evento SET completado = 1 WHERE id = :eventoId")
    fun marcarCompletado(eventoId: String, usuarioId: String): Completable
}
