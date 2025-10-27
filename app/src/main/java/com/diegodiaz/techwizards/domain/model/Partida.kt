package com.diegodiaz.techwizards.domain.model

data class Partida(
    val id: Long = 0,
    val perder: Boolean,
    val gano: Boolean,
    val deltaMonedas: Int,
    val usuarioId: String,
)
