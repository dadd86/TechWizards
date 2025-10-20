package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Entidad Room para los registros de borrado lógico.
 *
 * @property tableName Tabla afectada.
 * @property entityId Identificador de la entidad.
 * @property deletedAtMs Fecha del borrado.
 * @security
 * - Los consumidores deben purgar tombstones antiguos tras sincronizar.
 * - Evitar exponer detalles de borrado a clientes no autorizados.
 */
@Entity(
    tableName = "Tombstone",
    primaryKeys = ["tableName", "entityId"],
)
data class TombstoneEntity(
    @ColumnInfo(name = "tableName")
    val tableName: String,
    @ColumnInfo(name = "entityId")
    val entityId: String,
    @ColumnInfo(name = "deletedAtMs")
    val deletedAtMs: Long,
)
