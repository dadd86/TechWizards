package com.diegodiaz.techwizards.data.remote.firestore

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API REST para consultar el ranking desde Firestore.
 *
 * @security
 * Requiere autenticación Bearer (Firebase ID token) y no registra tokens.
 */
interface FirestoreLeaderboardApi {

    @POST("documents:runQuery")
    suspend fun obtenerTopTen(
        @Body request: FirestoreRunQueryRequestDto
    ): List<FirestoreRunQueryResponseDto>
}