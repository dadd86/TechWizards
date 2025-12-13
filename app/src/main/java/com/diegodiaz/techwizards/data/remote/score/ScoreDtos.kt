package com.diegodiaz.techwizards.data.remote.score

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession

data class ScoreEntryDto(
    val id: String? = null,
    val alias: String,
    val score: Int,
    val position: Int? = null,
    val prizeName: String? = null,
    val prizeDescription: String? = null
)

data class ScorePayload(
    val alias: String,
    val score: Int
)

data class PrizeDto(
    val descripcion: String,
    val valor: Int,
    val updatedAt: Long? = null
)

data class LoginRequest(
    val alias: String
)

data class SessionResponseDto(
    val token: String,
    val alias: String
)

fun ScoreEntryDto.toDomain(overridePosition: Int? = null) = LeaderboardEntry(
    id = id,
    alias = alias,
    score = score,
    position = overridePosition ?: position,
    prizeName = prizeName,
    prizeDescription = prizeDescription
)

fun PrizeDto.toDomain() = CommonPrize(
    descripcion = descripcion,
    valor = valor,
    updatedAt = updatedAt
)

fun CommonPrize.toDto() = PrizeDto(
    descripcion = descripcion,
    valor = valor,
    updatedAt = updatedAt
)

fun SessionResponseDto.toDomain() = UserSession(
    token = token,
    alias = alias
)
