package com.diegodiaz.techwizards.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Entidad Room para la tabla `Tombstone`.
 *
 * @security
 * - Conserva solo metadatos de eliminación evitando exposición de datos.
 * - Clave compuesta impide duplicados para la misma entidad.
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