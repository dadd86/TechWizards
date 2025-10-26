package com.diegodiaz.techwizards.domain.model

data class Message(
    val id: Long,
    val authorId: Long,
    val lobbyId: Long? = null,
    val matchId: Long? = null,
    val texto: String,
    val creadoEn: Long = System.currentTimeMillis()
)
