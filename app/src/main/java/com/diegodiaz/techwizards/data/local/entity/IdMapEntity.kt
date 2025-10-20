package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

/**
 * Entidad Room que vincula IDs locales con sus equivalentes remotos.
 *
 * @property localTable Nombre de la tabla local.
 * @property localId Identificador local persistido.
 * @property remoteCollection Colección remota correspondiente.
 * @property remoteId Identificador remoto asociado.
 * @security
 * - No almacenar secretos dentro de los identificadores.
 * - Usar únicamente para propósitos de sincronización controlada.
 */
@Entity(
    tableName = "IdMap",
    primaryKeys = ["localTable", "localId"],
    indices = [Index(value = ["remoteCollection", "remoteId"], unique = true)],
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
