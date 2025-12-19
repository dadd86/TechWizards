package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.model.MatchScore
import com.diegodiaz.techwizards.domain.model.MatchSnapshot
import kotlinx.coroutines.flow.Flow


/**
 * Acceso a partidas y eventos asociados.
 *
 * @security
 * - Debe registrar auditoría con el logger descentralizado.
 */
interface MatchRepository {
    /**
     * Persiste o actualiza la definición de un match.
     */
    suspend fun upsertMatch(match: Match): Result<Unit, AgentError>

    /**
     * Registra un evento inmutable del match.
     */
    suspend fun registrarEvento(evento: MatchEvent): Result<Unit, AgentError>

    /**
     * Obtiene el historial de partidas finalizadas ordenadas por tiempo de finalización.
     */
    suspend fun obtenerHistorial(limite: Int): Result<List<Match>, AgentError>

    /**
     * Almacena el marcador final de un jugador.
     */
    suspend fun guardarScore(score: MatchScore): Result<Unit, AgentError>

    /**
     * Observa el estado consolidado del match desde la fuente remota.
     */
    fun observarEstado(matchId: String): Flow<MatchSnapshot>

    /**
     * Marca que el jugador eligió cara y confirmó su apuesta.
     */
    suspend fun marcarListo(
        matchId: String,
        jugadorNumero: Long,
        caraElegida: Int
    ): Result<Unit, AgentError>

    /**
     * Registra el lanzamiento de dado del jugador.
     */
    suspend fun registrarLanzamiento(
        matchId: String,
        jugadorNumero: Long,
        caraObtenida: Int
    ): Result<Unit, AgentError>
}
