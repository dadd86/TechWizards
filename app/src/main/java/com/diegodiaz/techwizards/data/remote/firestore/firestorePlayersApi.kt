package com.diegodiaz.techwizards.data.remote.firestore

import retrofit2.http.GET
import retrofit2.http.Path

/**
 * API REST para leer documentos de jugadores desde Firestore.
 *
 * @security
 * Usa autenticación Bearer (Firebase ID token) y no registra tokens.
 */
interface FirestorePlayersApi {

    @GET("players/{userId}")
    suspend fun obtenerJugador(
        @Path("userId") userId: String
    ): FirestorePlayerDocumentDto
}