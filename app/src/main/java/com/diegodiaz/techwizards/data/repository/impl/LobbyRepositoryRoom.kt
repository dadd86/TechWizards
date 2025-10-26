package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.ILobbyDao
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Lobby
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await

// Se encarga de gestionar las salas (lobbies) del juego
class LobbyRepositoryRoom(
    private val lobbyDao: ILobbyDao
) {

    // -------- Rx nativo --------
    fun observarLobbiesRx() =
        lobbyDao.getAll().map { list -> list.map { it.toDomain() } }

    fun crearLobbyRx(nombre: String, capacidad: Int): Completable =
        lobbyDao.insert(
            LobbyEntity(
                id = "lobby_${System.currentTimeMillis()}",
                nombre = nombre,
                capacidad = capacidad,
                abierta = true
            )
        )

    fun cerrarLobbyRx(lobbyId: String): Completable =
        lobbyDao.cerrarLobby(lobbyId)

    // -------- Wrappers coroutines --------
    fun observarLobbies(): Flow<List<Lobby>> =
        observarLobbiesRx().asFlow()

    suspend fun crearLobby(nombre: String, capacidad: Int) {
        crearLobbyRx(nombre, capacidad).await()
    }

    suspend fun cerrarLobby(lobbyId: String) {
        cerrarLobbyRx(lobbyId).await()
    }
}
