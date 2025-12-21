package com.diegodiaz.techwizards.data.remote.score

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession

data class ScorePayload(
    val alias: String,
    val deltaMonedas: Int
)

data class LoginRequest(
    val alias: String
)

// Respuesta real que devuelve tu /login
data class SessionResponseDto(
    val token: String,
    val alias: String,
    val isAdmin: Boolean = false
)

fun SessionResponseDto.toDomain(): UserSession =
    UserSession(token = token, alias = alias, isAdmin = isAdmin)

// Respuesta real que devuelve tu /leaderboard/top10
data class ScoreEntryDto(
    val id: String? = null,
    val alias: String = "Jugador",
    val score: Int = 0,
    val position: Int = 0,
    val prizeName: String? = null,
    val prizeDescription: String? = null
)

fun ScoreEntryDto.toDomain(): LeaderboardEntry =
    LeaderboardEntry(
        id = id,
        alias = alias,
        score = score,
        position = position,
        prizeName = prizeName,
        prizeDescription = prizeDescription
    )
data class ScoreTopTenResponseDto(
    val items: List<ScoreTopTenItemDto> = emptyList()
)

data class ScoreTopTenItemDto(
    val userId: String? = null,
    val userName: String = "Jugador",
    val points: Int = 0,
    val timestamp: String? = null
)

fun ScoreTopTenItemDto.toDomain(position: Int): LeaderboardEntry =
    LeaderboardEntry(
        id = userId,
        alias = userName,
        score = points,
        position = position,
        prizeName = null,
        prizeDescription = null
    )
data class PrizeCommonDto(
    val descripcion: String,
    val valor: Int,
    val updatedAt: Long? = null
)

fun PrizeCommonDto.toDomain(): CommonPrize =
    CommonPrize(descripcion = descripcion, valor = valor)

data class PrizeUpdateRequestDto(
    val descripcion: String,
    val valor: Int
)

fun CommonPrize.toRequestDto(): PrizeUpdateRequestDto =
    PrizeUpdateRequestDto(descripcion = descripcion, valor = valor)


