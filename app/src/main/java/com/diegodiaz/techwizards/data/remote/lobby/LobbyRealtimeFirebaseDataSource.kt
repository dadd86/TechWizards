package com.diegodiaz.techwizards.data.remote.lobby

import com.diegodiaz.techwizards.domain.model.Lobby
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Fuente en tiempo real para lobbies usando Firebase Firestore.
 *
 * @security Solo expone IDs numéricos y metadatos del lobby.
 */
class LobbyRealtimeFirebaseDataSource(
    private val firestore: FirebaseFirestore = Firebase.firestore
) {

    /**
     * Observa el lobby remoto por identificador.
     *
     * @param lobbyId Identificador del lobby remoto.
     * @return Flujo con el estado más reciente del lobby.
     * @security No expone PII; usa IDs numéricos de jugadores.
     */
    fun streamLobby(lobbyId: String): Flow<LobbyRealtimeSnapshot?> = callbackFlow {
        val listener = firestore.collection("lobbies")
            .document(lobbyId)
            .addSnapshotListener { snapshot, error ->
                error?.let { close(it) }
                if (snapshot != null && snapshot.exists()) {
                    val jugadores = snapshot.get("jugadoresConectados") as? List<*> ?: emptyList<Any>()
                    val jugadoresNormalizados = jugadores.mapNotNull { (it as? Number)?.toLong() }
                    val estado = snapshot.getString("estado")
                    val updatedAt = snapshot.getTimestamp("updatedAt")?.toDate()?.time
                    trySend(
                        LobbyRealtimeSnapshot(
                            lobbyId = lobbyId,
                            jugadoresConectados = jugadoresNormalizados,
                            estado = estado,
                            updatedAtMs = updatedAt
                        )
                    )
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Crea el lobby remoto si no existe.
     *
     * @param lobby Lobby a persistir.
     * @security No expone datos sensibles; persiste solo metadatos del lobby.
     */
    suspend fun crearLobby(lobby: Lobby) {
        val docRef = firestore.collection("lobbies").document(lobby.id)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            if (!snapshot.exists()) {
                transaction.set(
                    docRef,
                    mapOf(
                        "codigo" to lobby.codigo,
                        "estado" to lobby.estado.name,
                        "creadorNumero" to lobby.creadorNumero,
                        "jugadoresConectados" to listOf(lobby.creadorNumero),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            } else {
                transaction.update(
                    docRef,
                    mapOf(
                        "jugadoresConectados" to FieldValue.arrayUnion(lobby.creadorNumero),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
            }
        }.await()
    }

    /**
     * Agrega el jugador al lobby remoto existente.
     *
     * @param lobbyId Identificador del lobby.
     * @param jugadorNumero Identificador del jugador.
     * @security No expone datos sensibles; solo usa IDs numéricos.
     */
    suspend fun unirseLobby(lobbyId: String, jugadorNumero: Long) {
        firestore.collection("lobbies")
            .document(lobbyId)
            .update(
                mapOf(
                    "jugadoresConectados" to FieldValue.arrayUnion(jugadorNumero),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            .await()
    }
}

data class LobbyRealtimeSnapshot(
    val lobbyId: String,
    val jugadoresConectados: List<Long>,
    val estado: String?,
    val updatedAtMs: Long?
)