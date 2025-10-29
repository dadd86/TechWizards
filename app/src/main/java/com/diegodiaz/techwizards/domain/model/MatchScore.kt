package com.diegodiaz.techwizards.domain.model

/**
 * Marcador consolidado para un jugador dentro de un match.
 *
 * @property matchId Identificador de la partida.
 * @property usuarioNumero Número de usuario asociado.
 * @property score Puntaje final.
 * @security
 * - Solo almacena números, sin información sensible.
 */
data class MatchScore(
    val matchId: String,
    val usuarioNumero: Long,
    val score: Int,
)