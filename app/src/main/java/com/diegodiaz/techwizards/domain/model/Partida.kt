package com.diegodiaz.techwizards.domain.model
import com.diegodiaz.techwizards.data.local.entity.Resultado

data class Partida(
    val id: Long = 0L,
    val usuarioNumero: Long,
    val fecha: Long,
    val resultado: Resultado,
    val deltaMonedas: Int
)
