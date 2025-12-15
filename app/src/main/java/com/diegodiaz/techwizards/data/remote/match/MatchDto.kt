package com.diegodiaz.techwizards.data.remote.match

/**
 * DTO principal de partida.
 */
data class MatchDto(
    val id: String,
    val lobbyId: String?,
    val modo: String,
    val estado: String,
    val createdByNumero: Long,
    val createdAtMs: Long,
    val startedAtMs: Long?,
    val finishedAtMs: Long?
)

/**
 * DTO para participantes.
 */
data class MatchParticipantDto(
    val matchId: String,
    val usuarioNumero: Long,
    val rol: String?,
    val teamId: String?,
    val joinedAtMs: Long,
    val leftAtMs: Long?,
    val score: Int
)

/**
 * DTO para puntuaciones.
 */
data class MatchScoreDto(
    val matchId: String,
    val usuarioNumero: Long,
    val score: Int
)

/**
 * Estado de listo del jugador (apuesta confirmada).
 */
data class PlayerReadyDto(
    val jugadorNumero: Long,
    val caraElegida: Int
)

/**
 * Resultado de lanzamiento de dado.
 */
data class RollResultDto(
    val jugadorNumero: Long,
    val caraObtenida: Int
)