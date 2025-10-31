package com.diegodiaz.techwizards.domain.model

data class Partida(
    val id: Long = 0L,
    val usuarioNumero: Long,
    val fecha: Long,
    val resultado: Resultado,
    val deltaMonedas: Int
)
