package com.diegodiaz.techwizards.data.remote.match

/**
 * DTO principal de partida.
 */
data class MatchDto(
    val id: String = "",
    val lobbyId: String? = null,
    val modo: String = "online",
    val estado: String = "PENDING",
    val createdByNumero: Long = 0,
    val createdAtMs: Long = 0,
    val startedAtMs: Long? = null,
    val finishedAtMs: Long? = null
)

/**
 * DTO para participantes.
 */
data class MatchParticipantDto(
    val matchId: String = "",
    val usuarioNumero: Long = 0,
    val rol: String? = null,
    val teamId: String? = null,
    val joinedAtMs: Long = 0,
    val leftAtMs: Long? = null,
    val score: Int = 0
)

/**
 * DTO para puntuaciones.
 */
data class MatchScoreDto(
    val matchId: String = "",
    val usuarioNumero: Long = 0,
    val score: Int = 0
)

/**
 * Estado de listo del jugador (apuesta confirmada).
 */
data class PlayerReadyDto(
    val jugadorNumero: Long = 0,
    val caraElegida: Int = 0,
    val timestampMs: Long = 0
)

/**
 * Resultado de lanzamiento de dado.
 */
data class RollResultDto(
    val jugadorNumero: Long = 0,
    val caraObtenida: Int = 0,
    val timestampMs: Long = 0
)