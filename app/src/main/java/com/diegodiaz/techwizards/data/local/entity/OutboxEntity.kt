package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `Outbox`.
 *
 * @security
 * - Solo contiene metadatos de sincronización; evita payloads con PII.
 * - Permite auditoría de reintentos gracias a marcas temporales.
 */
@Entity(tableName = "Outbox")
data class OutboxEntity(
    @PrimaryKey
    @ColumnInfo(name = "operationId")
    val operationId: String,
    @ColumnInfo(name = "entityType")
    val entityType: String,
    @ColumnInfo(name = "entityId")
    val entityId: String,
    @ColumnInfo(name = "op")
    val op: String,
    @ColumnInfo(name = "payloadJson")
    val payloadJson: String,
    @ColumnInfo(name = "attempt")
    val attempt: Int,
    @ColumnInfo(name = "lastError")
    val lastError: String?,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
    @ColumnInfo(name = "updatedAtMs")
    val updatedAtMs: Long,
)
