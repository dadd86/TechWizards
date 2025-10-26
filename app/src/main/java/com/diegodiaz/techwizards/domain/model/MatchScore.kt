package com.diegodiaz.techwizards.domain.model

data class MatchScore(
    val id: Long,
    val matchId: Long,
    val participantId: Long,
    val puntos: Int = 0
)
