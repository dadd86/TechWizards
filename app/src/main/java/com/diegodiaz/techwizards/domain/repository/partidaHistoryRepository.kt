package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Partida

/**
 * Repositorio remoto para historial de partidas por jugador.
 */
interface PartidaHistoryRepository {

    suspend fun registrarPartida(
        firebaseUid: String,
        partida: Partida,
    ): Result<Unit, AgentError>
}
