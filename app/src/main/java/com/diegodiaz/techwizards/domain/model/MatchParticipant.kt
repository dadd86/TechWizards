package com.diegodiaz.techwizards.domain.model

data class MatchParticipant(
    val id: Long,
    val matchId: Long,
    val userId: Long,
    val apodo: String? = null,
    val joinedAt: Long = System.currentTimeMillis(),
    val esGanador: Boolean = false
)
