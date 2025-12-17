package com.diegodiaz.techwizards.data.remote.match

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Fuente en tiempo real respaldada por Firebase Firestore.
 *
 * @security Solo se persisten identificadores numéricos y apuestas de dado.
 */
class MatchRealtimeFirebaseDataSource(
    private val firestore: FirebaseFirestore = Firebase.firestore
) : MatchRealtimeDataSource {

    override fun streamMatch(matchId: String): Flow<MatchDto?> = flowOf(null)

    override fun streamParticipantes(matchId: String): Flow<List<MatchParticipantDto>> =
        streamReady(matchId).map { readyList ->
            readyList.map { ready ->
                MatchParticipantDto(
                    matchId = matchId,
                    usuarioNumero = ready.jugadorNumero,
                    rol = "player",
                    teamId = null,
                    joinedAtMs = ready.timestampMs,
                    leftAtMs = null,
                    score = 0
                )
            }
        }

    override fun streamScores(matchId: String): Flow<List<MatchScoreDto>> =
        streamRollResults(matchId).map { rolls ->
            rolls.groupBy { it.jugadorNumero }.map { (jugador, lanzamientos) ->
                MatchScoreDto(
                    matchId = matchId,
                    usuarioNumero = jugador,
                    score = lanzamientos.sumOf { it.caraObtenida }
                )
            }
        }

    override fun streamReady(matchId: String): Flow<List<PlayerReadyDto>> =
        collectionFlow(readyCollection(matchId)) { snapshot ->
            snapshot.mapNotNull { document ->
                document.toObject(PlayerReadyDto::class.java)
                    ?.copy(timestampMs = document.getLong("timestampMs") ?: 0)
            }
        }

    override fun streamRollResults(matchId: String): Flow<List<RollResultDto>> =
        collectionFlow(rollCollection(matchId)) { snapshot ->
            snapshot.mapNotNull { document ->
                document.toObject(RollResultDto::class.java)
            }
        }

    override suspend fun marcarListo(matchId: String, ready: PlayerReadyDto) {
        readyCollection(matchId)
            .document(ready.jugadorNumero.toString())
            .set(ready.copy(timestampMs = System.currentTimeMillis()))
            .await()
    }

    override suspend fun registrarLanzamiento(matchId: String, lanzamiento: RollResultDto) {
        rollCollection(matchId)
            .document(lanzamiento.jugadorNumero.toString())
            .set(lanzamiento.copy(timestampMs = System.currentTimeMillis()))
            .await()
    }

    private fun readyCollection(matchId: String) =
        firestore.collection("matches").document(matchId).collection("ready")

    private fun rollCollection(matchId: String) =
        firestore.collection("matches").document(matchId).collection("rolls")

    private fun <T> collectionFlow(
        collection: com.google.firebase.firestore.CollectionReference,
        mapper: (List<com.google.firebase.firestore.DocumentSnapshot>) -> T
    ): Flow<T> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            error?.let { close(it) }
            if (snapshot != null) {
                trySend(mapper(snapshot.documents))
            }
        }
        awaitClose { listener.remove() }
    }
}