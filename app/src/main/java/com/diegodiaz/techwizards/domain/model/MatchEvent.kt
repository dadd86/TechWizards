package com.diegodiaz.techwizards.domain.model


/**
 * Evento inmutable registrado durante una partida.
 *
 * @property id Identificador global del evento.
 * @property matchId Partida asociada.
 * @property seq Secuencia incremental única por partida.
 * @property type Tipo de evento (MOVE, BET, ROLL, etc.).
 * @property actorNumero Número del jugador que originó la acción.
 * @property payloadJson Carga útil serializada en JSON.
 * @property createdAtMs Marca temporal de creación (epoch millis).
 * @security
 * - El payload debe sanitizarse antes de persistir; evita PII.
 */
data class MatchEvent(
    val id: String,
    val matchId: String,
    val seq: Long,
    val type: String,
    val actorNumero: Long,
    val payloadJson: String?,
    val createdAtMs: Long,
)