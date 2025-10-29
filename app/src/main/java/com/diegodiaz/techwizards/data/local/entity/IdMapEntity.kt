package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Entidad Room para la tabla `IdMap`.
 *
 * @security
 * - Solo persiste mapeos de identificadores técnicos sin PII.
 * - Índices únicos evitan colisiones de IDs remotos.
 */
@Entity(
    tableName = "IdMap",
    primaryKeys = ["localTable", "localId"],
    indices = [
        Index(value = ["remoteCollection", "remoteId"], unique = true),
    ],
)
data class IdMapEntity(
    @ColumnInfo(name = "localTable")
    val localTable: String,
    @ColumnInfo(name = "localId")
    val localId: String,
    @ColumnInfo(name = "remoteCollection")
    val remoteCollection: String,
    @ColumnInfo(name = "remoteId")
    val remoteId: String,
)
