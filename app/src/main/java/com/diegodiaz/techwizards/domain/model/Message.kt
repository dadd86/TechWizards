package com.diegodiaz.techwizards.domain.model

/**
 * Representa un mensaje de chat dentro de una partida.
 */
data class Message(
    val id: String,
    val matchId: String,
    val remitenteId: String,
    val contenido: String,
    val timestamp: Long
)
