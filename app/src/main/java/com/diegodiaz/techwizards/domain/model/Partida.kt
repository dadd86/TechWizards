package com.diegodiaz.techwizards.domain.model

data class Partida(
    val id: String, //EJEMPLO
    val usuarioId: String,
    val fecha: Long,
    val resultado: String,
    val cambioMonedas: Int
)
