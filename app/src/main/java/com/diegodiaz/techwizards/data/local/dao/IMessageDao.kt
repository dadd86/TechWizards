package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.MessageEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

/**
 * DAO para `Message`.
 *
 * @security
 * - Consultas parametrizadas y sanitización previa evitan inyección.
 * - Solo se almacenan mensajes filtrados por capas superiores.
 */
@Dao
interface IMessageDao {

    /** Upsert Rx (para integrarlo con repos Rx). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: MessageEntity): Completable

    /** Stream Rx por partida, ordenado por fecha ascendente. */
    @Query("SELECT * FROM Message WHERE matchId = :matchId ORDER BY createdAtMs ASC")
    fun getMensajes(matchId: String): Flowable<List<MessageEntity>>

    /** Inserción Rx opcional (si no quieres upsert). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: MessageEntity): Completable

    /** Inserción suspend (útil desde coroutines). */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(message: MessageEntity)

    /** Listado reciente por partida (coroutines). */
    @Query("SELECT * FROM Message WHERE matchId = :matchId ORDER BY createdAtMs DESC LIMIT :limite")
    suspend fun listarRecientes(matchId: String, limite: Int): List<MessageEntity>
}
