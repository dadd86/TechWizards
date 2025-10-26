package com.diegodiaz.techwizards.domain.model

data class Usuario(
    val id: String,
    val nombre: String,
    val monedas: Int,
    val alias: String? = null,
    val avatarUrl: String? = null,
    val creadoEn: Long = System.currentTimeMillis()
)
