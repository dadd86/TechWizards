package com.diegodiaz.techwizards.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegodiaz.techwizards.data.local.entity.OutboxEntity

/**
 * DAO para la tabla `Outbox`.
 *
 * @security
 * - Consultas parametrizadas para prevenir inyección SQL.
 * - Solo almacena metadatos de sincronización, sin PII.
 */
@Dao
interface IOutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: OutboxEntity)

    @Query("SELECT * FROM Outbox ORDER BY createdAtMs ASC LIMIT :limit")
    suspend fun obtenerPendientes(limit: Int): List<OutboxEntity>

    @Query("UPDATE Outbox SET attempt = :attempt, lastError = :lastError, updatedAtMs = :updatedAtMs WHERE operationId = :operationId")
    suspend fun actualizarIntento(operationId: String, attempt: Int, lastError: String?, updatedAtMs: Long): Int

    @Query("DELETE FROM outbox")
    fun borrarTodo()
}
