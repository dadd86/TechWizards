package com.diegodiaz.techwizards.integration.victory

import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Datos mínimos para celebrar una victoria.
 *
 * @param aliasJugador Alias del jugador victorioso.
 * @param deltaMonedas Diferencia de monedas ganada.
 * @param timestampMillis Momento en que se detectó la victoria.
 * @return `VictoryCelebrationPayload` con información segura de victoria.
 * @throws IllegalStateException No se lanza.
 * @security Solo contiene alias y datos de juego ya visibles para el usuario.
 */
data class VictoryCelebrationPayload(
    val aliasJugador: String,
    val deltaMonedas: Int,
    val timestampMillis: Long
) {
    companion object {
        /**
         * Genera un payload desde la entidad `Partida`.
         *
         * @param partida Partida con resultado de victoria.
         * @return Payload listo para ser enviado a `VictoryCelebrationService`.
         * @throws IllegalStateException No se lanza.
         * @security No expone datos adicionales del jugador.
         */
        fun fromPartida(partida: Partida): VictoryCelebrationPayload =
            VictoryCelebrationPayload(
                aliasJugador = partida.aliasJugador,
                deltaMonedas = partida.deltaMonedas,
                timestampMillis = System.currentTimeMillis()
            )
    }
}
