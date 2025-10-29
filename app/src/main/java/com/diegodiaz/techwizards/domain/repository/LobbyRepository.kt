package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Lobby
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

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
}
