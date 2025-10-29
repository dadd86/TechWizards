package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Message
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

/**
 * ChatRepository.kt
 *
 * Gestiona los mensajes dentro del juego (chat general o de partida).
 * Desde aquí se consultan los mensajes y se envían nuevos.
 *
 * 🔹 Define QUÉ operaciones puede realizar el dominio.
 * 🔹 La implementación (ChatRepositoryRoom.kt) define CÓMO se hace en Room.
 */
interface ChatRepository {

    // 🔹 RxJava — versión reactiva
    fun obtenerMensajesRx(): Flowable<List<Message>>
    fun enviarMensajeRx(message: Message): Completable

    // 🔹 Coroutines — versión suspendida
    fun obtenerMensajes(): Flow<List<Message>>
    suspend fun enviarMensaje(message: Message)
}
