package com.diegodiaz.techwizards.data.remote.score

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

/**
 * API REST para ranking y premios.
 *
 * @security
 * Las cabeceras de autorización se inyectan en cada llamada.
 */
interface ScoreApi {

    @GET("leaderboard/top10")
    suspend fun fetchTopTen(): List<ScoreEntryDto>

    @POST("scores")
    suspend fun publishScore(
        @Header("Authorization") bearer: String?,
        @Body payload: ScorePayload
    )

    @GET("prize/common")
    suspend fun fetchPrize(): PrizeDto

    @PUT("prize/common")
    suspend fun updatePrize(
        @Header("Authorization") bearer: String?,
        @Body prize: PrizeDto
    ): PrizeDto

    @POST("login")
    suspend fun login(@Body request: LoginRequest): SessionResponseDto
}