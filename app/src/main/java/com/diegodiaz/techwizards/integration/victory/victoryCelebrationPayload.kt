package com.diegodiaz.techwizards.integration.victory

import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Datos mínimos para celebrar una victoria.
 *
 * @security
 * Solo contiene alias y datos de juego ya visibles para el usuario.
 */
data class VictoryCelebrationPayload(
    val aliasJugador: String,
    val deltaMonedas: Int
) {
    companion object {
        fun fromPartida(partida: Partida): VictoryCelebrationPayload =
            VictoryCelebrationPayload(
                aliasJugador = partida.aliasJugador,
                deltaMonedas = partida.deltaMonedas
            )
    }
}
