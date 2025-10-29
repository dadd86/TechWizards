package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Lobby
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.LobbyEstado

/**
 * LobbyRepository.kt
 *
 * Gestiona los lobbies (salas de juego) disponibles en la app.
 * Desde aquí se consultan, crean o cierran las salas.
 *
 * 🔹 Define QUÉ puede hacer el dominio con los lobbies.
 * 🔹 La implementación (LobbyRepositoryRoom.kt) define CÓMO lo hace con Room.
 */
interface LobbyRepository {

    // 🔹 RxJava — versión reactiva
    fun getLobbiesRx(): Flowable<List<Lobby>>
    fun crearLobbyRx(lobby: Lobby): Completable
    fun cerrarLobbyRx(id: String): Completable

    // 🔹 Coroutines — versión suspendida
    fun getLobbies(): Flow<List<Lobby>>
    suspend fun crearLobby(lobby: Lobby)
    suspend fun cerrarLobby(id: String)
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