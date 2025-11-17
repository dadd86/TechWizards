package com.diegodiaz.techwizards.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad Room que guarda la ubicación del jugador al ganar.
 */
@Entity(
    tableName = "victory_location",
    indices = [
        Index("matchId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = MatchEntity::class, // Ajusta si el nombre de la entidad de partida es otro
            parentColumns = ["id"],
            childColumns = ["matchId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VictoryLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val matchId: Long?,
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long
)
