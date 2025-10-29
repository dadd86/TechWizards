package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para la tabla `Message`.
 *
 * @security
 * - Mantiene relaciones fuertes con Match y Usuario evitando mensajes huérfanos.
 * - Requiere que el texto llegue sanitizado desde capas superiores.
 */
@Entity(
    tableName = "Message",
    indices = [
        Index(value = ["matchId", "createdAtMs"]),
        Index(value = ["senderNum"]),
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
            childColumns = ["senderNum"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "matchId")
    val matchId: String,
    @ColumnInfo(name = "senderNum")
    val senderNumero: Long,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
)