package com.diegodiaz.techwizards.domain.model

data class MatchEvent(
    val id: Long,
    val matchId: Long,
    val tipo: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actorParticipantId: Long? = null,
    val payload: String? = null
)
