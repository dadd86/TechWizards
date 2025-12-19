package com.diegodiaz.techwizards.domain.model

data class CommonPrize(
    val descripcion: String,
    val valor: Int,
    val updatedAt: Long? = null
)
