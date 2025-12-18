package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.diegodiaz.techwizards.data.local.cache.MatchSnapshotLocalDataSource
import com.diegodiaz.techwizards.data.repository.impl.work.MatchActionRetryWorker
import com.diegodiaz.techwizards.data.remote.match.MatchApi
import com.diegodiaz.techwizards.data.remote.match.MatchRealtimeDataSource
import com.diegodiaz.techwizards.data.remote.match.MatchRemoteMapper
import com.diegodiaz.techwizards.data.remote.match.PlayerReadyDto
import com.diegodiaz.techwizards.data.remote.match.RollResultDto
import com.diegodiaz.techwizards.data.repository.impl.MatchRepositoryRoom
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.model.MatchSnapshot
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.map
import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
/**
 * Implementación remota de MatchRepository usando Retrofit y una fuente realtime (Firestore/SSE).
 */
class MatchRepositoryRemote(
    private val api: MatchApi,
    private val realtime: MatchRealtimeDataSource,
    private val mapper: MatchRemoteMapper,
    private val scoreRepository: ScoreRepository,
    private val sessionManager: SessionManager,
    private val mirrorRoom: MatchRepositoryRoom?,
    private val snapshotLocalDataSource: MatchSnapshotLocalDataSource?,
    appContext: Context
) : MatchRepository {

    private val ultimoScorePublicado = mutableMapOf<Long, Int>()
    private val workManager = WorkManager.getInstance(appContext)

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

        val remoto = combine(matchFlow, participantesFlow, scoreFlow, readyFlow, rollFlow) { match, participantes, scores, readyList, rollList ->
            val carasElegidas = readyList.associate { it.jugadorNumero to it.caraElegida }
            val lanzamientos = rollList.associate { it.jugadorNumero to it.caraObtenida }
            val (ganadorRonda, empate) = resolverGanador(
                lanzamientos = lanzamientos,
                scores = scores
            )

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
        }.onEach { snapshot ->
            if (snapshot.match != null) {
                mirrorRoom?.guardarSnapshot(snapshot)
                snapshotLocalDataSource?.guardar(matchId, snapshot)
            }
        }
        val cacheFlow = mirrorRoom?.observarSnapshot(matchId) ?: snapshotLocalDataSource?.observar(matchId)?.filterNotNull()
        ?: emptyFlow()

        return merge(cacheFlow, remoto)
    }

    override suspend fun marcarListo(
        matchId: String,
        jugadorNumero: Long,
        caraElegida: Int
    ): Result<Unit, AgentError> =
        runSafe(
            block = { realtime.marcarListo(matchId, PlayerReadyDto(jugadorNumero, caraElegida)) },
            onNetworkError = {
                enqueueRetry(
                    MatchActionRetryWorker.workName("ready", matchId, jugadorNumero),
                    MatchActionRetryWorker.dataReady(matchId, jugadorNumero, caraElegida)
                )
            }
        )

    override suspend fun registrarLanzamiento(
        matchId: String,
        jugadorNumero: Long,
        caraObtenida: Int
    ): Result<Unit, AgentError> =
        runSafe(
            block = { realtime.registrarLanzamiento(matchId, RollResultDto(jugadorNumero, caraObtenida)) },
            onNetworkError = {
                enqueueRetry(
                    MatchActionRetryWorker.workName("roll", matchId, jugadorNumero),
                    MatchActionRetryWorker.dataRoll(matchId, jugadorNumero, caraObtenida)
                )
            }
        )

    private suspend fun <T> runSafe(
        block: suspend () -> T,
        onNetworkError: (() -> Unit)? = null
    ): Result<T, AgentError> = try {
        Result.Ok(block())
    } catch (error: Exception) {
        val mapped = when (error) {
            is IOException -> AgentError.Network
            else -> AgentError.Unknown(error)
        }
        if (mapped is AgentError.Network) {
            onNetworkError?.invoke()
        }
        Result.Err(mapped)
    }

    private fun enqueueRetry(name: String, data: androidx.work.Data) {
        val request = OneTimeWorkRequestBuilder<MatchActionRetryWorker>()
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .setInputData(data)
            .build()
        workManager.enqueueUniqueWork(name, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }

    private suspend fun resolverGanador(
        lanzamientos: Map<Long, Int>,
        scores: List<MatchScore>
    ): Pair<Long?, Boolean> {
        if (lanzamientos.isEmpty()) return null to false
        val max = lanzamientos.maxByOrNull { it.value } ?: return null to false
        val jugadoresConMax = lanzamientos.filterValues { it == max.value }.keys
        val empate = jugadoresConMax.size > 1
        val ganador = if (empate) null else max.key
        if (ganador != null && !empate) {
            publicarPuntuacionAcumulada(
                ganador = ganador,
                lanzamientos = lanzamientos,
                scores = scores
            )
        }
        return ganador to empate
    }
    private suspend fun publicarPuntuacionAcumulada(
        ganador: Long,
        lanzamientos: Map<Long, Int>,
        scores: List<MatchScore>
    ) {
        val session = sessionManager.session.value ?: return
        val acumulado = scores.firstOrNull { it.usuarioNumero == ganador }?.score
            ?: lanzamientos[ganador]
            ?: 0
        val ultimoPublicado = ultimoScorePublicado[ganador]
        if (ultimoPublicado != null && acumulado <= ultimoPublicado) return

        runCatching {
            scoreRepository.publicarPuntuacion(session, acumulado)
        }.onSuccess {
            ultimoScorePublicado[ganador] = acumulado
        }
    }
}