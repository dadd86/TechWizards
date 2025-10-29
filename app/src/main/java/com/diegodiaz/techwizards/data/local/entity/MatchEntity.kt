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
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("lobbyId")]
)
data class MatchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val lobbyId: Long?,
    val status: String,         // guardamos el enum como texto
    val inicioEn: Long,
    val finEn: Long?
)
