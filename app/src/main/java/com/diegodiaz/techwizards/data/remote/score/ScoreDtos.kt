package com.diegodiaz.techwizards.data.remote.score

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import com.squareup.moshi.Json

data class ScoreEntryDto(
    val id: String? = null,
    val alias: String,
    val coins: Int,
    val position: Int? = null,
    val prizeName: String? = null,
    val prizeDescription: String? = null
)

data class ScorePayload(
    val alias: String,
    val score: Int
)

data class PrizeDto(
    @Json(name = "descripcion")
    val descripcion: String,
    @Json(name = "valor")
    val valor: Int,
    @Json(name = "updatedAt")
    val updatedAt: Long? = null
)

data class PrizeRequestDto(
    @Json(name = "descripcion")
    val descripcion: String,
    @Json(name = "valor")
    val valor: Int
)

data class LoginRequest(
    val alias: String
)

data class SessionResponseDto(
    val token: String,
    val alias: String,
    val isAdmin: Boolean? = null
)

fun ScoreEntryDto.toDomain(overridePosition: Int? = null) = LeaderboardEntry(
    id = id,
    alias = alias,
    score = coins,
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

fun CommonPrize.toRequestDto() = PrizeRequestDto(
    descripcion = descripcion,
    valor = valor
)

fun SessionResponseDto.toDomain() = UserSession(
    token = token,
    alias = alias,
    isAdmin = isAdmin ?: false
)

