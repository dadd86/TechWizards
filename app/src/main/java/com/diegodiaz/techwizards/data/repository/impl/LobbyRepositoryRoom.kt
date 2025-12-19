package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.ILobbyDao
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Lobby
import com.diegodiaz.techwizards.domain.model.LobbyEstado
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.rx3.await
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

/**
 * Se encarga de gestionar las salas (lobbies) del juego.
 */
class LobbyRepositoryRoom(
    private val lobbyDao: ILobbyDao
) {

    // -------- Rx nativo --------

    /** Observa todos los lobbies existentes en la base de datos. */
    fun observarLobbiesRx() =
        lobbyDao.getAll().map { list -> list.map { it.toDomain() } }

    /** Observa lobbies por estado. */
    fun observarPorEstadoRx(estado: LobbyEstado, limite: Int): Flowable<List<Lobby>> =
        lobbyDao.listByEstado(estado.name, limite).map { list -> list.map { it.toDomain() } }

    /** Obtiene un lobby por id. */
    fun obtenerPorIdRx(lobbyId: String): Maybe<Lobby> =
        lobbyDao.getById(lobbyId).map { it.toDomain() }

    /** Crea un lobby inicial (estado PENDING). */
    fun crearLobbyRx(
        nombre: String,
        creadorNumero: Long,
        modo: String,
        codigo: String? = null
    ): Completable {
        val now = System.currentTimeMillis()
        val codigoNormalizado = codigo?.trim()?.ifBlank { null }
        val entity = LobbyEntity(
            id = codigoNormalizado ?: "lobby_$now",
            nombre = nombre,
            codigo = codigoNormalizado,
            modo = modo,
            estado = LobbyEstado.PENDING.name,
            creadorNumero = creadorNumero,
            createdAtMs = now
        )
        return lobbyDao.insert(entity)
    }

    /**
     * Busca un lobby disponible para emparejar, excluyendo el del creador actual.
     *
     * @security No expone datos sensibles; solo metadatos de lobby.
     */
    suspend fun buscarLobbyDisponible(
        creadorNumero: Long,
        limite: Int = 5
    ): Lobby? {
        val lobbies = lobbyDao.listarPorEstado(LobbyEstado.PENDING.name, limite)
            .map { it.toDomain() }
        return lobbies.firstOrNull { it.creadorNumero != creadorNumero }
    }

    /**
     * Crea un lobby persistente para matchmaking local.
     *
     * @security No expone datos sensibles; usa identificadores numéricos internos.
     */
    suspend fun crearLobby(
        nombre: String,
        creadorNumero: Long,
        modo: String,
        codigo: String? = null
    ): Lobby {
        val now = System.currentTimeMillis()
        val codigoNormalizado = codigo?.trim()?.ifBlank { null }
        val lobby = Lobby(
            id = codigoNormalizado ?: "lobby_$now",
            nombre = nombre,
            codigo = codigoNormalizado,
            modo = modo,
            estado = LobbyEstado.PENDING,
            creadorNumero = creadorNumero,
            createdAtMs = now
        )
        lobbyDao.upsert(lobby.toEntity())
        return lobby
    }
    /**
     * Inserta o actualiza un lobby existente.
     *
     * @param lobby Lobby a persistir.
     * @security No expone datos sensibles; persiste solo metadatos del lobby.
     */
    suspend fun upsertLobby(lobby: Lobby) {
        lobbyDao.upsert(lobby.toEntity())
    }


/** Cierra un lobby existente (estado CLOSED). */
fun cerrarLobbyRx(lobbyId: String): Completable =
    lobbyDao.updateEstado(lobbyId, LobbyEstado.CLOSED.name)
}