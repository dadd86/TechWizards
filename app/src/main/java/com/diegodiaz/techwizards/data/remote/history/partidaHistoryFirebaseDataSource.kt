package com.diegodiaz.techwizards.data.remote.history

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Fuente remota de historial de partidas usando Firebase Firestore.
 *
 * @security
 * - Escribe únicamente datos de juego asociados al UID autenticado.
 * - No expone tokens ni datos sensibles.
 */
class PartidaHistoryFirebaseDataSource(
    private val firestore: FirebaseFirestore = Firebase.firestore,
) {

    /**
     * Registra una partida en la colección de historial del jugador.
     *
     * @param firebaseUid UID autenticado de Firebase.
     * @param partida DTO serializable para Firestore.
     * @throws Exception Si la escritura remota falla.
     * @security
     * - La ruta remota queda acotada a `/players/{uid}/history`.
     */
    suspend fun registrarPartida(firebaseUid: String, partida: PartidaHistoryDto) {
        historyCollection(firebaseUid)
            .document()
            .set(
                mapOf(
                    "usuarioNumero" to partida.usuarioNumero,
                    "aliasJugador" to partida.aliasJugador,
                    "fechaMs" to partida.fechaMs,
                    "resultado" to partida.resultado,
                    "deltaMonedas" to partida.deltaMonedas,
                    "createdAt" to FieldValue.serverTimestamp(),
                )
            )
            .await()
    }

    private fun historyCollection(firebaseUid: String) =
        firestore.collection("players")
            .document(firebaseUid)
            .collection("history")
}