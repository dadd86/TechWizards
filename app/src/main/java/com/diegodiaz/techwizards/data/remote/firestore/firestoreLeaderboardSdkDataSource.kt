package com.diegodiaz.techwizards.data.remote.firestore

import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Fuente remota de ranking usando el SDK de Firestore.
 *
 * @security
 * - Lee únicamente datos públicos del leaderboard.
 * - No registra tokens ni datos sensibles.
 */
class FirestoreLeaderboardSdkDataSource(
    private val firestore: FirebaseFirestore = Firebase.firestore
) {

    /**
     * Obtiene el top ten ordenado por wins descendente desde Firestore.
     *
     * @return lista ordenada de entradas del leaderboard.
     * @security No persiste ni expone identificadores sensibles.
     */
    suspend fun obtenerTopTen(): List<LeaderboardEntry> {
        val snapshot = firestore.collection("players")
            .orderBy("wins", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .await()

        return snapshot.documents.mapIndexed { index, document ->
            val data = document.data.orEmpty()
            LeaderboardEntry(
                id = data["usuarioNumero"]?.toString() ?: document.id,
                alias = data["alias"]?.toString()
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?: data["aliasJugador"]?.toString()
                        ?.trim()
                        ?.takeIf { it.isNotBlank() }
                    ?: "Jugador",
                score = data["coins"].toIntOrZero(),
                position = index + 1,
                wins = data["wins"].toIntOrNull()
            )
        }
    }

    private fun Any?.toIntOrZero(): Int = toIntOrNull() ?: 0

    private fun Any?.toIntOrNull(): Int? = when (this) {
        is Int -> this
        is Long -> this.toInt()
        is Double -> this.toInt()
        is Float -> this.toInt()
        is Number -> this.toInt()
        is String -> this.toIntOrNull()
        else -> null
    }
}