package com.diegodiaz.techwizards.data.local.entity

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