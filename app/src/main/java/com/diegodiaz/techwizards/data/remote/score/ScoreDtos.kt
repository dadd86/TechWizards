package com.diegodiaz.techwizards.data.remote.score

import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.model.UserSession
import com.squareup.moshi.Json
import com.google.gson.annotations.SerializedName

data class ScoreEntryDto(
    val id: String? = null,
    val alias: String? = null,
    @Json(name = "userName")
    @SerializedName("userName")
    val userName: String? = null,
    val score: Int? = null,
    @Json(name = "points")
    @SerializedName("points")
    val points: Int? = null,
    val position: Int? = null,
    val prizeName: String? = null,
    val prizeDescription: String? = null
)
data class ScoreTopTenResponse(
    val items: List<ScoreEntryDto>
)


data class ScorePayload(
    val alias: String,
    val score: Int
)

data class PrizeDto(
    @Json(name = "descripcion")
    @SerializedName("descripcion")
    val descripcion: String,
    @Json(name = "valor")
    @SerializedName("valor")
    val valor: Int,
    @Json(name = "updatedAt")
    @SerializedName("updatedAt")
    val updatedAt: Long? = null
)

data class PrizeRequestDto(
    @Json(name = "descripcion")
    @SerializedName("descripcion")
    val descripcion: String,
    @Json(name = "valor")
    @SerializedName("valor")
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
    alias = alias ?: userName.orEmpty(),
    score = score ?: points ?: 0,
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

