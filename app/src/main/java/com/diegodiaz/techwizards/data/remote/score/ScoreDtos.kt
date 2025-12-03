package com.diegodiaz.techwizards.data.remote.score

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession

internal data class ScoreEntryDto(
    val alias: String,
    val score: Int
)

internal data class ScorePayload(
    val alias: String,
    val score: Int
)

internal data class PrizeDto(
    val descripcion: String,
    val valor: Int
)

internal data class LoginRequest(
    val alias: String
)

internal data class SessionResponseDto(
    val token: String,
    val alias: String
)

internal fun ScoreEntryDto.toDomain() = LeaderboardEntry(alias = alias, score = score)

internal fun PrizeDto.toDomain() = CommonPrize(descripcion = descripcion, valor = valor)

internal fun CommonPrize.toDto() = PrizeDto(descripcion = descripcion, valor = valor)

internal fun SessionResponseDto.toDomain() = UserSession(token = token, alias = alias)