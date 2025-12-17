package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.data.remote.match.MatchApi
import com.diegodiaz.techwizards.data.remote.match.MatchRealtimeDataSource
import com.diegodiaz.techwizards.data.remote.match.MatchRemoteMapper
import com.diegodiaz.techwizards.data.remote.match.PlayerReadyDto
import com.diegodiaz.techwizards.data.remote.match.RollResultDto
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.model.MatchSnapshot
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * Implementación remota de MatchRepository usando Retrofit y una fuente realtime (Firestore/SSE).
 */
class MatchRepositoryRemote(
    private val api: MatchApi,
    private val realtime: MatchRealtimeDataSource,
    private val mapper: MatchRemoteMapper
) : MatchRepository {
    override suspend fun upsertMatch(match: Match): Result<Unit, AgentError> =
        runSafe { api.guardarMatch(mapper.toDto(match)) }

    override suspend fun registrarEvento(evento: MatchEvent): Result<Unit, AgentError> =
        Result.Err(AgentError.Validation("Eventos remotos no implementados"))

    override suspend fun obtenerHistorial(limite: Int): Result<List<Match>, AgentError> =
        runSafe {
            api.obtenerMatch("recent") // En un backend real habría endpoint paginado
            emptyList()
        }

    override suspend fun guardarScore(score: MatchScore): Result<Unit, AgentError> =
        runSafe { api.registrarLanzamiento(score.matchId, RollResultDto(score.usuarioNumero, score.score)) }

    override fun observarEstado(matchId: String): Flow<MatchSnapshot> {
        val matchFlow = realtime.streamMatch(matchId).map { dto -> dto?.let(mapper::toDomain) }
        val participantesFlow = realtime.streamParticipantes(matchId).map { list -> list.map(mapper::toDomain) }
        val scoreFlow = realtime.streamScores(matchId).map { list -> list.map(mapper::toDomain) }
        val readyFlow = realtime.streamReady(matchId)
        val rollFlow = realtime.streamRollResults(matchId)

        return combine(matchFlow, participantesFlow, scoreFlow, readyFlow, rollFlow) { match, participantes, scores, readyList, rollList ->
            val carasElegidas = readyList.associate { it.jugadorNumero to it.caraElegida }
            val lanzamientos = rollList.associate { it.jugadorNumero to it.caraObtenida }
            val (ganadorRonda, empate) = resolverGanador(lanzamientos)

            MatchSnapshot(
                match = match,
                participantes = participantes,
                scores = scores,
                remotoListo = carasElegidas.size >= 2,
                carasElegidas = carasElegidas,
                lanzamientos = lanzamientos,
                ganadorRonda = ganadorRonda,
                empate = empate
            )
        }
    }

    override suspend fun marcarListo(
        matchId: String,
        jugadorNumero: Long,
        caraElegida: Int
    ): Result<Unit, AgentError> =
        runSafe { realtime.marcarListo(matchId, PlayerReadyDto(jugadorNumero, caraElegida)) }

    override suspend fun registrarLanzamiento(
        matchId: String,
        jugadorNumero: Long,
        caraObtenida: Int
    ): Result<Unit, AgentError> =
        runSafe { realtime.registrarLanzamiento(matchId, RollResultDto(jugadorNumero, caraObtenida)) }

    private inline fun <T> runSafe(block: () -> T): Result<T, AgentError> = try {
        Result.Ok(block())
    } catch (error: Exception) {
        Result.Err(AgentError.Unknown(error))
    }

    private fun resolverGanador(lanzamientos: Map<Long, Int>): Pair<Long?, Boolean> {
        if (lanzamientos.isEmpty()) return null to false
        val max = lanzamientos.maxByOrNull { it.value } ?: return null to false
        val jugadoresConMax = lanzamientos.filterValues { it == max.value }.keys
        val empate = jugadoresConMax.size > 1
        val ganador = if (empate) null else max.key
        return ganador to empate
    }
}