package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "match_participant",
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("matchId"),
        Index("userId"),
        Index(value = ["matchId", "userId"], unique = true) // 1 usuario por match
    ]
)
data class MatchParticipantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val matchId: Long,
    val userId: Long,
    val apodo: String?,
    val joinedAt: Long,
    val esGanador: Boolean
)
