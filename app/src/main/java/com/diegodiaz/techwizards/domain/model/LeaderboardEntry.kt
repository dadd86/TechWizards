package com.diegodiaz.techwizards.domain.model

data class LeaderboardEntry(
    val id: String? = null,
    val alias: String,
    val score: Int,
    val position: Int? = null,
    val prizeName: String? = null,
    val prizeDescription: String? = null
)
