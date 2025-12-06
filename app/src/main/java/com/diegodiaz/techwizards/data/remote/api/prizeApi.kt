package com.diegodiaz.techwizards.data.remote.api

import com.diegodiaz.techwizards.data.remote.dto.PrizeRemoteDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT

/**
 * API REST opcional para gestionar los premios ligados
 * a los mejores puntajes del leaderboard.
 *
 * La idea es que el backend exponga un "premio común"
 * (por ejemplo, una recompensa que se muestra al primer puesto).
 *
 * Las cabeceras de autorización se inyectan desde la capa de datos.
 */
interface PrizeApi {

    /**
     * Recupera el premio común actual asociado al mejor puntaje.
     */
    @GET("prize/common")
    suspend fun fetchCommonPrize(): PrizeRemoteDto

    /**
     * Actualiza el premio común asociado al mejor puntaje.
     *
     * @param bearerToken Token de autorización en formato
     *        `"Bearer x.y.z"` o `null` si el backend no lo exige.
     */
    @PUT("prize/common")
    suspend fun updateCommonPrize(
        @Header("Authorization") bearerToken: String?,
        @Body prize: PrizeRemoteDto
    ): PrizeRemoteDto
}
