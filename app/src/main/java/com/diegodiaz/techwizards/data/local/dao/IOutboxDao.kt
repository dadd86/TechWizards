package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.OutboxEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

@Dao
interface IOutboxDao {

    // ---- Lecturas ----
    @Query("SELECT * FROM outbox WHERE id = :id LIMIT 1")
    fun getById(id: Long): Maybe<OutboxEntity>

    /** Pendientes por orden FIFO (más antiguos primero). */
    @Query("SELECT * FROM outbox WHERE entregado = 0 ORDER BY creadoEn ASC")
    fun listPending(): Flowable<List<OutboxEntity>>

    /** Siguiente elemento pendiente (si existe). */
    @Query("SELECT * FROM outbox WHERE entregado = 0 ORDER BY creadoEn ASC LIMIT 1")
    fun nextPending(): Maybe<OutboxEntity>

    @Query("SELECT * FROM outbox WHERE tipo = :tipo AND entregado = 0 ORDER BY creadoEn ASC")
    fun listPendingByType(tipo: String): Flowable<List<OutboxEntity>>

    // ---- Escrituras ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(entity: OutboxEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(list: List<OutboxEntity>): Completable

    @Query("UPDATE outbox SET entregado = 1 WHERE id = :id")
    fun markDelivered(id: Long): Completable

    @Query("UPDATE outbox SET reintentos = reintentos + 1 WHERE id = :id")
    fun incrementRetry(id: Long): Completable

    @Query("DELETE FROM outbox WHERE id = :id")
    fun deleteById(id: Long): Completable

    @Query("DELETE FROM outbox WHERE entregado = 1")
    fun purgeDelivered(): Completable
}

