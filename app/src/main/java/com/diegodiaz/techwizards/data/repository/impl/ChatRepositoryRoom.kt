package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IMessageDao
import com.diegodiaz.techwizards.data.local.entity.MessageEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Message
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await

/**
 * Gestiona los mensajes de chat dentro de una partida o lobby.
 */
class ChatRepositoryRoom(
    private val messageDao: IMessageDao
) {
    // -------- Rx nativo --------

    /** Observa los mensajes en un lobby concreto. */
    fun observarMensajesRx(lobbyId: String) =
        messageDao.getMensajes(lobbyId).map { list -> list.map { it.toDomain() } }

    /** Envía un nuevo mensaje al lobby. */
    fun enviarMensajeRx(mensaje: Message): Completable =
        messageDao.insert(mensaje.toEntity())

    // -------- Wrappers coroutines (opcional) --------

    fun observarMensajes(lobbyId: String): Flow<List<Message>> =
        observarMensajesRx(lobbyId).asFlow()

    suspend fun enviarMensaje(mensaje: Message) {
        enviarMensajeRx(mensaje).await()
    }
}
