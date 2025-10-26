package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "match",
    foreignKeys = [
        ForeignKey(
            entity = LobbyEntity::class,
            parentColumns = ["id"],
            childColumns = ["lobbyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lobbyId")]
)
data class MatchEntity(
    @PrimaryKey val id: String,
    val lobbyId: String,
    val estado: String,
    val fechaInicio: Long,
    val fechaFin: Long?
)