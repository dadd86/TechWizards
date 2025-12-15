package com.diegodiaz.techwizards.data.remote.match

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Abstracción de una fuente en tiempo real (Firestore/SSE/WS).
 */
interface MatchRealtimeDataSource {
    fun streamMatch(matchId: String): Flow<MatchDto?>
    fun streamParticipantes(matchId: String): Flow<List<MatchParticipantDto>>
    fun streamScores(matchId: String): Flow<List<MatchScoreDto>>
    fun streamRemotoListo(matchId: String): Flow<Boolean>
    suspend fun marcarListo(matchId: String, ready: PlayerReadyDto)
    suspend fun registrarLanzamiento(matchId: String, lanzamiento: RollResultDto)
}

/**
 * Implementación en memoria que simula actualizaciones remotas.
 */
class InMemoryMatchRealtimeDataSource : MatchRealtimeDataSource {
    private val matchFlows = mutableMapOf<String, MutableStateFlow<MatchDto?>>()
    private val participantesFlows = mutableMapOf<String, MutableStateFlow<List<MatchParticipantDto>>>()
    private val scoreFlows = mutableMapOf<String, MutableStateFlow<List<MatchScoreDto>>>()
    private val readyFlows = mutableMapOf<String, MutableStateFlow<Boolean>>()

    override fun streamMatch(matchId: String): Flow<MatchDto?> =
        flowFor(matchId, matchFlows, null)

    override fun streamParticipantes(matchId: String): Flow<List<MatchParticipantDto>> =
        flowFor(matchId, participantesFlows, emptyList())

    override fun streamScores(matchId: String): Flow<List<MatchScoreDto>> =
        flowFor(matchId, scoreFlows, emptyList())

    override fun streamRemotoListo(matchId: String): Flow<Boolean> =
        flowFor(matchId, readyFlows, false)

    override suspend fun marcarListo(matchId: String, ready: PlayerReadyDto) {
        // En un backend real esto dispararía un update en Firestore/WS.
        readyFlows.getOrPut(matchId) { MutableStateFlow(false) }.value = true
        // También incrementa el jugador en la lista de participantes como demo.
        val lista = participantesFlows.getOrPut(matchId) { MutableStateFlow(emptyList()) }
        if (lista.value.none { it.usuarioNumero == ready.jugadorNumero }) {
            val nuevo = lista.value + MatchParticipantDto(
                matchId = matchId,
                usuarioNumero = ready.jugadorNumero,
                rol = "player",
                teamId = null,
                joinedAtMs = System.currentTimeMillis(),
                leftAtMs = null,
                score = 0
            )
            lista.value = nuevo
        }
    }

    override suspend fun registrarLanzamiento(matchId: String, lanzamiento: RollResultDto) {
        val scores = scoreFlows.getOrPut(matchId) { MutableStateFlow(emptyList()) }
        val existing = scores.value.toMutableList()
        val index = existing.indexOfFirst { it.usuarioNumero == lanzamiento.jugadorNumero }
        if (index >= 0) {
            val actualizado = existing[index].copy(score = existing[index].score + lanzamiento.caraObtenida)
            existing[index] = actualizado
        } else {
            existing.add(
                MatchScoreDto(
                    matchId = matchId,
                    usuarioNumero = lanzamiento.jugadorNumero,
                    score = lanzamiento.caraObtenida
                )
            )
        }
        scores.value = existing
    }

    private fun <T> flowFor(
        matchId: String,
        store: MutableMap<String, MutableStateFlow<T>>,
        defaultValue: T
    ): StateFlow<T> = store.getOrPut(matchId) { MutableStateFlow(defaultValue) }.asStateFlow()
}