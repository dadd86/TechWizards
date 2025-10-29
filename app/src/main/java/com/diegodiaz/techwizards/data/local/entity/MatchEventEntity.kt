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
        ),
        ForeignKey(
            entity = MatchParticipantEntity::class,
            parentColumns = ["id"],
            childColumns = ["actorParticipantId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("matchId"), Index("actorParticipantId")]
)
data class MatchEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val matchId: Long,
    val tipo: String,
    val timestamp: Long,
    val actorParticipantId: Long?,
    val payload: String?
)

