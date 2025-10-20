package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad Room para los participantes de un match.
 *
 * @property matchId Identificador del match.
 * @property usuarioNum Identificador del usuario.
 * @property rol Rol textual persistido.
 * @property teamId Equipo asignado.
 * @property joinedAtMs Fecha de ingreso.
 * @property leftAtMs Fecha de salida.
 * @property score Puntaje acumulado.
 * @security
 * - Asegurar que las claves foráneas existan previo a insertar registros.
 * - `rol` debe validarse contra los valores soportados.
 */
@Entity(
    tableName = "MatchParticipant",
    primaryKeys = ["matchId", "usuarioNum"],
    indices = [
        Index(value = ["matchId"]),
        Index(value = ["usuarioNum"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["numero"],
            childColumns = ["usuarioNum"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MatchParticipantEntity(
    @ColumnInfo(name = "matchId")
    val matchId: String,
    @ColumnInfo(name = "usuarioNum")
    val usuarioNum: Long,
    @ColumnInfo(name = "rol")
    val rol: String?,
    @ColumnInfo(name = "teamId")
    val teamId: String?,
    @ColumnInfo(name = "joinedAtMs")
    val joinedAtMs: Long,
    @ColumnInfo(name = "leftAtMs")
    val leftAtMs: Long?,
    @ColumnInfo(name = "score")
    val score: Int,
)
