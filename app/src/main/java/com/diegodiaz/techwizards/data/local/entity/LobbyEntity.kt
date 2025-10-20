package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa la tabla Lobby.
 *
 * @property id Identificador global del lobby.
 * @property codigo Código único compartible.
 * @property modo Modo de juego asociado.
 * @property estado Estado persistido del lobby.
 * @property creadorNum Número del usuario creador.
 * @property createdAtMs Marca temporal de creación.
 * @security
 * - Redactar `codigo` antes de registrarlo en logs.
 * - `creadorNum` solo debe exponerse a componentes autorizados.
 */
@Entity(
    tableName = "Lobby",
    indices = [
        Index(value = ["codigo"], unique = true),
        Index(value = ["estado"]),
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
    val creadorNum: Long,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
)