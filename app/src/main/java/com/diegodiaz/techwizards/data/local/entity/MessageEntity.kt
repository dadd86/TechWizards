package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para mensajes de chat.
 *
 * @property id Identificador del mensaje.
 * @property matchId Match destino.
 * @property senderNum Usuario emisor.
 * @property text Contenido sanitizado.
 * @property createdAtMs Marca temporal de envío.
 * @security
 * - Almacenar solo texto moderado.
 * - `senderNum` debe consultarse bajo contexto autenticado.
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
    val senderNum: Long,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
)
