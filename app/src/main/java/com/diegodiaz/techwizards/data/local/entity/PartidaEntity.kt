package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "partida",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId"), Index("fecha")]
)
data class PartidaEntity(
    @PrimaryKey val id: String,
    val usuarioId: String,
    val fecha: Long,       // epoch millis
    val resultado: String, // "GANADO" | "PERDIDO"
    val cambioMonedas: Int
)