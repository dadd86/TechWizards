package com.diegodiaz.techwizards.domain.model
import com.diegodiaz.techwizards.data.local.entity.Resultado

data class Partida(
    val id: Long = 0L,
    val usuarioNumero: Long,
    val fecha: Long,
    val resultado: Resultado,
    val deltaMonedas: Int,
    val nombreJugador: String
)
/**
 * Genera una descripción amigable del resultado de la partida.
 */
fun Partida.formatoResumen(): String {
    val alias = nombreJugador.ifBlank { "Jugador" }
    return when (resultado) {
        Resultado.GANADO -> "$alias ganó (+$deltaMonedas)"
        Resultado.PERDIDO -> "$alias perdió ($deltaMonedas)"
    }
}