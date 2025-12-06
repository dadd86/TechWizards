package com.diegodiaz.techwizards.data.remote.dto

/**
 * DTO para respuestas de leaderboard remoto y
 * para el cuerpo de solicitud al publicar una puntuación.
 **/

data class ScoreRemoteDto(
    val id: String? = null,
    val player: String,
    val points: Int,
    val position: Int? = null,
    val prize: PrizeRemoteDto? = null
)
