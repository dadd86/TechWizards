package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.ColumnInfo

/**
 * Entidad Room para la tabla `Lobby`.
 *
 * @security
 * - Restricciones de FK garantizan integridad con `Usuario`.
 * - Evita duplicidad de códigos mediante índice único.
 */
@Entity(
    tableName = "Lobby",
    indices = [
        Index(value = ["estado"]),
        Index(value = ["codigo"], unique = true),
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],
            childColumns = ["creadorNum"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class LobbyEntity(
    val nombre: String,
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "codigo")
    val codigo: String?,
    @ColumnInfo(name = "modo")
    val modo: String,
    @ColumnInfo(name = "estado")
    val estado: String,
    @ColumnInfo(name = "creadorNum")
    val creadorNumero: Long,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
)
