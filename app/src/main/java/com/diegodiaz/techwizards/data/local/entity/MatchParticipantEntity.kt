package com.diegodiaz.techwizards.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad Room para la tabla `MatchParticipant`.
 *
 * @security
 * - Claves compuestas evitan duplicidad por jugador y match.
 * - No expone información personal adicional al número interno.
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
    val usuarioNumero: Long,
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