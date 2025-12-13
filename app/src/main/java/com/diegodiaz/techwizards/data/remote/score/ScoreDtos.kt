package com.diegodiaz.techwizards.data.remote.score

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession

internal data class ScoreEntryDto(
    val id: String? = null,
    val alias: String,
    val score: Int,
    val position: Int? = null,
    val prizeName: String? = null,
    val prizeDescription: String? = null
)

internal data class ScorePayload(
    val alias: String,
    val score: Int
)

internal data class PrizeDto(
    val descripcion: String,
    val valor: Int,
    val updatedAt: Long? = null
)

internal data class LoginRequest(
    val alias: String
)

internal data class SessionResponseDto(
    val token: String,
    val alias: String
)

internal fun ScoreEntryDto.toDomain(overridePosition: Int? = null) = LeaderboardEntry(
    id = id,
    alias = alias,
    score = score,
    position = overridePosition ?: position,
    prizeName = prizeName,
    prizeDescription = prizeDescription
)


internal fun PrizeDto.toDomain() = CommonPrize(
    descripcion = descripcion,
    valor = valor,
    updatedAt = updatedAt
)

internal fun CommonPrize.toDto() = PrizeDto(
    descripcion = descripcion,
    valor = valor,
    updatedAt = updatedAt
)

internal fun SessionResponseDto.toDomain() = UserSession(
    userId = alias,
    displayName = alias,
    accessToken = token
)