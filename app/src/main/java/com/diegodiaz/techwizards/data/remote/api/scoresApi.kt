package com.diegodiaz.techwizards.data.remote.api

import com.diegodiaz.techwizards.data.remote.dto.ScoreRemoteDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * API de leaderboard para leer y publicar puntuaciones remotas.
 *
 * - Recupera el top 10 del backend.
 * - Publica un nuevo score y devuelve el registro enriquecido (id, posición, premio, etc.).
 *
 * El backend espera el token tanto en la cabecera Authorization
 * como en el query param `auth`.
 */
interface ScoresApi {

    /**
     * Recupera el top 10 de puntuaciones.
     *
     * @param bearerToken Cabecera Authorization: "Bearer <token>"
     * @param authToken   Mismo token enviado como query param `auth`.
     */
    @GET("scores/top")
    suspend fun obtenerTopTen(
        @Header("Authorization") bearerToken: String,
        @Query("auth") authToken: String
    ): List<ScoreRemoteDto>

    /**
     * Publica una nueva puntuación y devuelve el registro tal y como
     * queda almacenado en el backend (con id y posición).
     *
     * @param bearerToken Cabecera Authorization: "Bearer <token>"
     * @param authToken   Mismo token enviado como query param `auth`.
     * @param score       Cuerpo con los datos de la puntuación.
     */
    @POST("scores")
    suspend fun publicarScore(
        @Header("Authorization") bearerToken: String,
        @Query("auth") authToken: String,
        @Body score: ScoreRemoteDto
    ): ScoreRemoteDto
}
