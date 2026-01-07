package com.diegodiaz.techwizards.data.remote.history

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
     * Registra una partida en el historial del jugador y actualiza su resumen.
     *
     * @param firebaseUid UID autenticado de Firebase.
     * @param partida DTO serializable para Firestore.
     * @throws Exception Si la escritura remota falla.
     * @security
     * - La ruta remota queda acotada a `/players/{uid}/history`.
     * - Actualiza solo métricas agregadas del jugador (wins/losses/coins).
     */
    suspend fun registrarPartida(firebaseUid: String, partida: PartidaHistoryDto) {
        require(firebaseUid.isNotBlank()) { "firebaseUid vacío" }
        require(partida.aliasJugador.isNotBlank()) { "aliasJugador vacío" }
        require(partida.resultado.isNotBlank()) { "resultado vacío" }
        require(partida.fechaMs > 0) { "fechaMs inválida" }
        val historyDocument = historyCollection(firebaseUid).document()
        val playerDocument = playerDocument(firebaseUid)
        val victoria = partida.resultado == "GANADO"
        val derrota = partida.resultado == "PERDIDO"

        firestore.runBatch { batch ->
            batch.set(
                historyDocument,
                mapOf(
                    "usuarioNumero" to partida.usuarioNumero,
                    "aliasJugador" to partida.aliasJugador,
                    "fechaMs" to partida.fechaMs,
                    "resultado" to partida.resultado,
                    "deltaMonedas" to partida.deltaMonedas,
                    "createdAt" to FieldValue.serverTimestamp(),
                )
            )
            batch.set(
                playerDocument,
                mapOf(
                    "usuarioNumero" to partida.usuarioNumero,
                    "alias" to partida.aliasJugador,
                    "wins" to FieldValue.increment(if (victoria) 1L else 0L),
                    "losses" to FieldValue.increment(if (derrota) 1L else 0L),
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )
        }.await()
    }

    /**
     * Recupera el historial remoto ordenado por fecha descendente.
     *
     * @param firebaseUid UID autenticado de Firebase.
     * @param limit Máximo de elementos a recuperar.
     * @return Lista de partidas remotas.
     * @throws Exception Si la consulta remota falla.
     * @security
     * - Solo consulta el historial del UID autenticado.
     * - No retorna datos sensibles fuera del scope del jugador.
     */
    suspend fun obtenerHistorial(firebaseUid: String, limit: Int): List<PartidaHistoryDto> {
        require(firebaseUid.isNotBlank()) { "firebaseUid vacío" }
        require(limit in 1..100) { "limit fuera de rango" }
        val snapshot = historyCollection(firebaseUid)
            .orderBy("fechaMs", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val usuarioNumero = doc.getLong("usuarioNumero") ?: return@mapNotNull null
            val aliasJugador = doc.getString("aliasJugador") ?: return@mapNotNull null
            val fechaMs = doc.getLong("fechaMs") ?: return@mapNotNull null
            val resultado = doc.getString("resultado") ?: return@mapNotNull null
            val deltaMonedas = doc.getLong("deltaMonedas")?.toInt() ?: 0
            PartidaHistoryDto(
                usuarioNumero = usuarioNumero,
                aliasJugador = aliasJugador,
                fechaMs = fechaMs,
                resultado = resultado,
                deltaMonedas = deltaMonedas,
            )
        }
    }

    private fun historyCollection(firebaseUid: String) =
        firestore.collection("players")
            .document(firebaseUid)
            .collection("history")

    private fun playerDocument(firebaseUid: String) =
        firestore.collection("players")
            .document(firebaseUid)
}