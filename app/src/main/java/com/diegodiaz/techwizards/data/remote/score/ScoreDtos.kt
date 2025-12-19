package com.diegodiaz.techwizards.data.remote.score

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import com.squareup.moshi.Json
import java.time.Instant

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
    @Json(name = "description")
    val descripcion: String,
    @Json(name = "value")
    val valor: Int,
    @Json(name = "updatedAt")
    val updatedAt: String? = null
)

data class PrizeRequestDto(
    @Json(name = "description")
    val descripcion: String,
    @Json(name = "value")
    val valor: Int
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
    updatedAt = updatedAt?.toEpochMillis()
)

fun CommonPrize.toDto() = PrizeDto(
    descripcion = descripcion,
    valor = valor,
    updatedAt = updatedAt?.toIsoInstant()
)

fun CommonPrize.toRequestDto() = PrizeRequestDto(
    descripcion = descripcion,
    valor = valor
)

fun SessionResponseDto.toDomain() = UserSession(
    token = token,
    alias = alias
)

private fun String.toEpochMillis(): Long? =
    runCatching { Instant.parse(this).toEpochMilli() }.getOrNull()

private fun Long.toIsoInstant(): String =
    Instant.ofEpochMilli(this).toString()
