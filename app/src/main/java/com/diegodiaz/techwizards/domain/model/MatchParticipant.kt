package com.diegodiaz.techwizards.domain.model


/**
 * Jugador asociado a una partida en curso o histórica.
 *
 * @property matchId Identificador de la partida.
 * @property usuarioNumero Número del usuario participante.
 * @property rol Rol asignado en la partida (host/player).
 * @property teamId Etiqueta opcional del equipo.
 * @property joinedAtMs Marca temporal de ingreso.
 * @property leftAtMs Marca de salida si abandonó.
 * @property score Puntuación acumulada dentro de la partida.
 * @security
 * - No contiene datos personales, únicamente referencias internas.
 */
data class MatchParticipant(
    val matchId: String,
    val usuarioNumero: Long,
    val rol: String?,
    val teamId: String?,
    val joinedAtMs: Long,
    val leftAtMs: Long?,
    val score: Int,
)