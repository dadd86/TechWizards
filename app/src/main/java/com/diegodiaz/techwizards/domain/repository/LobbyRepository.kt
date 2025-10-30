package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Lobby
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.LobbyEstado


/**
 * Contrato de acceso a lobbies para coordinación previa al match.
 *
 * @security
 * - Las implementaciones deben registrar auditoría usando el logger descentralizado.
 */
interface LobbyRepository {
    /**
     * Crea o actualiza un lobby.
     *
     * @param lobby Lobby a persistir.
     * @return Resultado vacío.
     */
    suspend fun upsertLobby(lobby: Lobby): Result<Unit, AgentError>

    /**
     * Cambia el estado del lobby de forma atómica.
     *
     * @param lobbyId Identificador del lobby.
     * @param nuevoEstado Estado destino.
     * @return Resultado vacío.
     */
    suspend fun actualizarEstado(lobbyId: String, nuevoEstado: LobbyEstado): Result<Unit, AgentError>

    /**
     * Obtiene un lobby por identificador.
     *
     * @param lobbyId Identificador buscado.
     * @return Lobby encontrado o error de validación.
     */
    suspend fun obtenerPorId(lobbyId: String): Result<Lobby, AgentError>

    /**
     * Lista lobbies filtrados por estado.
     *
     * @param estado Estado requerido.
     * @param limite Límite de resultados (1..100).
     */
    suspend fun listarPorEstado(estado: LobbyEstado, limite: Int): Result<List<Lobby>, AgentError>
}