package com.diegodiaz.techwizards.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para el registro de eventos de un match.
 *
 * @property id Identificador del evento.
 * @property matchId Match relacionado.
 * @property sequence Secuencia incremental por match.
 * @property type Tipo de evento.
 * @property actorNum Usuario que ejecutó la acción.
 * @property payloadJson Datos asociados.
 * @property createdAtMs Marca temporal de creación.
 * @security
 * - Evitar almacenar payloads sin sanear en `payloadJson`.
 * - Validar que actorNum exista en la tabla de usuarios.
 */
@Entity(
    tableName = "MatchEvent",
    indices = [
        Index(value = ["matchId", "sequence"], unique = true),
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
    @ColumnInfo(name = "sequence")
    val sequence: Long,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "actorNum")
    val actorNum: Long,
    @ColumnInfo(name = "payloadJson")
    val payloadJson: String?,
    @ColumnInfo(name = "createdAtMs")
    val createdAtMs: Long,
)
