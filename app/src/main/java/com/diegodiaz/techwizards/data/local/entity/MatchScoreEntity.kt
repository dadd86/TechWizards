package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad Room para el marcador final por match.
 *
 * @property matchId Identificador del match.
 * @property usuarioNum Usuario al que pertenece el marcador.
 * @property score Puntaje final registrado.
 * @security
 * - Restringir acceso de lectura a personal autorizado.
 * - Validar que el score sea no negativo.
 */
@Entity(
    tableName = "MatchScore",
    primaryKeys = ["matchId", "usuarioNum"],
    indices = [Index(value = ["usuarioNum"])],
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
    val usuarioNum: Long,
    @ColumnInfo(name = "score")
    val score: Int,
)
