package com.diegodiaz.techwizards.domain.model

data class Lobby(
    val id: Long,
    val nombre: String,
    val ownerId: Long,
    val participanteIds: List<Long> = emptyList(),
    val creadoEn: Long = System.currentTimeMillis(),
    val esPublico: Boolean = true
)
