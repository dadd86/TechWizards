package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "match_event",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("matchId")]
)
data class MatchEventEntity(
    @PrimaryKey val id: String,
    val matchId: String,
    val timestamp: Long,
    val tipo: String,       // tipo de evento: "START", "TURN", "END"...
    val descripcion: String // descripción breve del evento
)
