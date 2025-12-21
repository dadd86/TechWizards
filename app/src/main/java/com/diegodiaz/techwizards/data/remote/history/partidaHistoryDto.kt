package com.diegodiaz.techwizards.data.remote.history

/**
 * DTO de Firestore para historial de partidas por jugador.
 *
 * @property usuarioNumero Identificador local del jugador.
 * @property aliasJugador Alias visible en historial.
 * @property fechaMs Marca de tiempo en milisegundos.
 * @property resultado Resultado de la partida (GANADO/PERDIDO).
 * @property deltaMonedas Variación de monedas.
 * @security
 * - Solo contiene datos de juego; no incluye PII ni tokens.
 */
data class PartidaHistoryDto(
    val usuarioNumero: Long = 0L,
    val aliasJugador: String = "",
    val fechaMs: Long = 0L,
    val resultado: String = "",
    val deltaMonedas: Int = 0,
)