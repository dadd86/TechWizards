package com.diegodiaz.techwizards.data.local.entity

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
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("matchId"), Index("usuarioId")]
)
data class MatchParticipantEntity(
    @PrimaryKey val id: String,
    val matchId: String,
    val usuarioId: String,
    val puntuacion: Int
)