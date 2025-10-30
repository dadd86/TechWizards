package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.model.MatchScore


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
}
