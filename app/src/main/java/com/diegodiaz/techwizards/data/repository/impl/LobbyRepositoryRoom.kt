package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.ILobbyDao
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.domain.model.Lobby
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.rx3.await

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

    /** Crea un nuevo lobby con el nombre y capacidad indicados. */
    fun crearLobbyRx(nombre: String, capacidad: Int): Completable =
        lobbyDao.insert(
            LobbyEntity(
                id = "lobby_${System.currentTimeMillis()}",
                nombre = nombre,
                capacidad = capacidad,
                abierta = true
            )
        )

    /** Cierra un lobby existente por su ID. */
    fun cerrarLobbyRx(lobbyId: String): Completable =
        lobbyDao.cerrarLobby(lobbyId)

    // -------- Wrappers coroutines (opcional) --------

    fun observarLobbies(): Flow<List<Lobby>> = callbackFlow {
        val disposable = observarLobbiesRx()
            .subscribe(
                { lobbies -> trySend(lobbies).isSuccess },
                { error -> close(error) }
            )
        awaitClose { disposable.dispose() }
    }

    suspend fun crearLobby(nombre: String, capacidad: Int) {
        crearLobbyRx(nombre, capacidad).await()
    }

    suspend fun cerrarLobby(lobbyId: String) {
        cerrarLobbyRx(lobbyId).await()
    }
}
