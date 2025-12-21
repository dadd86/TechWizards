package com.diegodiaz.techwizards.data.remote.dto

data class LeaderboardRemoteDto(
    val id: String? = null,
    val alias: String,
    val score: Int,
    val position: Int? = null,
    val prizeName: String? = null,
    val prizeDescription: String? = null
)
