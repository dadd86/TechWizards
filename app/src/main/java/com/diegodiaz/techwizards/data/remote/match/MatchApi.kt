package com.diegodiaz.techwizards.data.remote.match

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * API REST para sincronizar partidas multijugador.
 *
 * @security
 * - Los tokens se añaden desde el interceptor global, nunca en claro aquí.
 */
interface MatchApi {

    @GET("matches/{id}")
    suspend fun obtenerMatch(@Path("id") matchId: String): MatchDto

    @GET("matches/{id}/participants")
    suspend fun obtenerParticipantes(@Path("id") matchId: String): List<MatchParticipantDto>

    @GET("matches/{id}/scores")
    suspend fun obtenerScores(@Path("id") matchId: String): List<MatchScoreDto>

    @POST("matches")
    suspend fun guardarMatch(@Body match: MatchDto)

    @POST("matches/{id}/ready")
    suspend fun marcarListo(@Path("id") matchId: String, @Body ready: PlayerReadyDto)

    @POST("matches/{id}/roll")
    suspend fun registrarLanzamiento(
        @Path("id") matchId: String,
        @Body lanzamiento: RollResultDto
    )
}