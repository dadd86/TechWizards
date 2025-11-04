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
        Index(value = ["createdByNum"]),
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
data class MatchEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "matchId")
    val matchId: String,
    @ColumnInfo(name = "seq")
    val seq: Long,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "actorNum")
    val actorNumero: Long,
    @ColumnInfo(name = "payloadJson")
    val payloadJson: String?,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
)
