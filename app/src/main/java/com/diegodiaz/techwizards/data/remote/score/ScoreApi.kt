package com.diegodiaz.techwizards.data.remote.score

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/**
 * API REST para ranking y premios.
 *
 * @security
 * Las cabeceras de autorización se inyectan en cada llamada.
 */
interface ScoreApi {

    @GET("leaderboard/top10")
    suspend fun fetchTopTen(
        @Header("Authorization") bearerToken: String?
    ): List<ScoreEntryDto>

    @GET("leaderboard")
    suspend fun fetchLeaderboard(
        @Header("Authorization") bearerToken: String?,
        @Query("limit") limit: Int = 10
    ): List<ScoreEntryDto>

    @POST("scores")
    suspend fun publishScore(
        @Header("Authorization") bearerToken: String?,
        @Body payload: ScorePayload
    )

    @GET("prize/common")
    suspend fun fetchPrize(
        @Header("Authorization") bearerToken: String?
    ): PrizeDto

    @PUT("prize/common")
    suspend fun updatePrize(
        @Header("Authorization") bearerToken: String?,
        @Body prize: PrizeDto
    ): PrizeDto

    @POST("login")
    suspend fun login(@Body request: LoginRequest): SessionResponseDto
}