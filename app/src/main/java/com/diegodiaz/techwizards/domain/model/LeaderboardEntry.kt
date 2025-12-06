package com.diegodiaz.techwizards.domain.model

data class LeaderboardEntry(
    val id: String,
    val playerName: String,
    val points: Int,
    val position: Int,
    val prizeName: String?,
    val prizeDescription: String?
)
