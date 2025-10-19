package com.diegodiaz.techwizards.domain.model

data class Partida(
    val id: Long,
    val fechaMs: Long,
    val gano: Boolean,
    val deltaMonedas: Int
)
