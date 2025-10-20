package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que gestiona operaciones pendientes de sincronización.
 *
 * @property operationId Identificador único de la operación.
 * @property entityType Tipo de entidad objetivo.
 * @property entityId Identificador de la entidad objetivo.
 * @property op Operación solicitada.
 * @property payloadJson Carga útil serializada.
 * @property attempt Intentos realizados.
 * @property lastError Último error registrado.
 * @property createdAtMs Fecha de creación.
 * @property updatedAtMs Fecha del último intento.
 * @security
 * - Redactar payloads al imprimirlos.
 * - Limitar reintentos para evitar ataques de denegación de servicio.
 */
@Entity(
    tableName = "Outbox",
    indices = [Index(value = ["entityType", "entityId"])],
)
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