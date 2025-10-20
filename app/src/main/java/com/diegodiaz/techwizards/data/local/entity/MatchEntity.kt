package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla Match.
 *
 * @property id Identificador global.
 * @property lobbyId Lobby de origen, si existe.
 * @property modo Modo de juego.
 * @property estado Estado actual del match.
 * @property createdByNum Usuario host.
 * @property createdAtMs Fecha de creación.
 * @property startedAtMs Fecha de inicio.
 * @property finishedAtMs Fecha de cierre.
 * @security
 * - Mantener `estado` alineado con el dominio para prevenir inconsistencias.
 * - Evitar exponer `createdByNum` fuera de contexto seguro.
 */
@Entity(
    tableName = "Match",
    indices = [
        Index(value = ["estado", "createdAtMs"]),
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
    val createdByNum: Long,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
    @ColumnInfo(name = "startedAtMs")
    val startedAtMs: Long?,
    @ColumnInfo(name = "finishedAtMs")
    val finishedAtMs: Long?,
)
