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
    fun streamReady(matchId: String): Flow<List<PlayerReadyDto>>
    fun streamRollResults(matchId: String): Flow<List<RollResultDto>>
    suspend fun marcarListo(matchId: String, ready: PlayerReadyDto)
    suspend fun registrarLanzamiento(matchId: String, lanzamiento: RollResultDto)
}