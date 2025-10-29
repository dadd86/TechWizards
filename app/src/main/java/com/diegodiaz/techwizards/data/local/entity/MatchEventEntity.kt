package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `MatchEvent`.
 *
 * @security
 * - Relaciona eventos con Match y Usuario asegurando consistencia.
 * - Índice único mantiene orden total de eventos.
 */
@Entity(
    tableName = "MatchEvent",
    indices = [
        Index(value = ["matchId", "seq"], unique = true),
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
            childColumns = ["actorNum"],
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
