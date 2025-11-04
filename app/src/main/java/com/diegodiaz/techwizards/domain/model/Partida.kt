package com.diegodiaz.techwizards.domain.model
import com.diegodiaz.techwizards.data.local.entity.Resultado

/**
 * Representa el resultado de una partida almacenada en persistencia local.
 *
 * @property id Identificador interno autogenerado.
 * @property usuarioNumero Clave foránea del jugador que disputó la partida.
 * @property aliasJugador Alias vigente del jugador al registrar el resultado.
 * @property fecha Marca de tiempo del registro en milisegundos.
 * @property resultado Indica si la partida fue ganada o perdida.
 * @property deltaMonedas Variación de saldo provocada por la partida.
 */
data class Partida(
    val id: Long = 0L,
    val usuarioNumero: Long,
    val aliasJugador: String,
    val fecha: Long,
    val resultado: Resultado,
    val deltaMonedas: Int
)