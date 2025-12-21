package com.diegodiaz.techwizards.data.remote.score

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import com.diegodiaz.techwizards.data.remote.dto.BackendLoginRequestDto
import com.diegodiaz.techwizards.data.remote.dto.BackendLoginResponseDto

interface ScoreApi {

    @GET("leaderboard/top10")
    suspend fun fetchTopTen(
        @Header("Authorization") bearerToken: String? = null
    ): List<ScoreEntryDto>

    @POST("scores")
    suspend fun publicarScore(
        @Header("Authorization") bearerToken: String,
        @Body score: ScorePayload
    )

    @GET("prize/common")
    suspend fun fetchCommonPrize(
        @Header("Authorization") bearerToken: String? = null
    ): PrizeCommonDto

    @PUT("prize/common")
    suspend fun updatePrize(
        @Header("Authorization") bearerToken: String,
        @Body request: PrizeUpdateRequestDto
    ): PrizeCommonDto

    // Login (NO “loguea”, registra alias) -> REQUIERE Bearer Firebase ID Token
    @POST("login")
    suspend fun login(
        @Header("Authorization") bearerToken: String,
        @Body request: LoginRequest
    ): SessionResponseDto
}

