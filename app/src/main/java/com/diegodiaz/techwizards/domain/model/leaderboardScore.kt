package com.diegodiaz.techwizards.domain.model


data class LeaderboardScore(
    val id: String? = null,
    val playerAlias: String,
    val score: Int,
    val prizeName: String? = null
)
