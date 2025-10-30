package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `Match`.
 *
 * @security
 * - Claves foráneas mantienen consistencia con Lobby y Usuario.
 * - Índices facilitan auditorías rápidas por estado.
 */
@Entity(
    tableName = "Match",
    indices = [
        Index(value = ["estado", "createdAtMs"]),
        Index(value = ["lobbyId"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = LobbyEntity::class,
            parentColumns = ["id"],
            childColumns = ["lobbyId"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],
            childColumns = ["createdByNum"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MatchEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "lobbyId")
    val lobbyId: String?,
    @ColumnInfo(name = "modo")
    val modo: String,
    @ColumnInfo(name = "estado")
    val estado: String,
    @ColumnInfo(name = "createdByNum")
    val createdByNumero: Long,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
    @ColumnInfo(name = "startedAtMs")
    val startedAtMs: Long?,
    @ColumnInfo(name = "finishedAtMs")
    val finishedAtMs: Long?,

)
