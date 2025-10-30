package com.diegodiaz.techwizards.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `MatchScore`.
 *
 * @security
 * - Claves foráneas aseguran integridad con partidas y usuarios.
 * - Solo almacena puntajes numéricos sin PII.
 */
@Entity(
    tableName = "MatchScore",
    primaryKeys = ["matchId", "usuarioNum"],
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
data class MatchScoreEntity(
    @ColumnInfo(name = "matchId")
    val matchId: String,
    @ColumnInfo(name = "usuarioNum")
    val usuarioNumero: Long,
    @ColumnInfo(name = "score")
    val score: Int,
)

