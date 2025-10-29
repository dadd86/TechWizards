package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Message
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result

/**
 * Maneja el chat de los matches.
 *
 * @security
 * - Sanitiza el contenido y evita exponer PII.
 */
interface ChatRepository {
    /** Envía un mensaje al chat del match. */
    suspend fun enviarMensaje(message: Message): Result<Unit, AgentError>

    /** Obtiene el historial reciente de mensajes del match. */
    suspend fun obtenerMensajes(matchId: String, limite: Int): Result<List<Message>, AgentError>
}
