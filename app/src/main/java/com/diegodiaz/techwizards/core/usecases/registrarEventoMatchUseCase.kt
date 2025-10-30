package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.model.Outbox
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import com.diegodiaz.techwizards.domain.repository.SyncRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import java.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Registra un evento del match y lo encola en outbox para sincronización remota.
 *
 * @property matchRepository Repositorio de partidas.
 * @property syncRepository Repositorio de sincronización offline-first.
 * @property ioDispatcher Dispatcher utilizado para operaciones de I/O.
 * @property clock Función que retorna el timestamp actual en milisegundos.
 * @property idGenerator Generador de identificadores únicos para la operación.
 * @security
 * - Valida campos críticos para evitar corrupción de datos y desbordes.
 * - Enmascara identificadores en logs y evita incluir PII en payloads.
 */
class RegistrarEventoMatchUseCase(
    private val matchRepository: MatchRepository,
    private val syncRepository: SyncRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val idGenerator: () -> String = { UUID.randomUUID().toString() },
) {

    /**
     * Persiste el [event] y crea una operación de outbox ligada a [remoteCollection].
     *
     * @param event Evento a guardar.
     * @param remoteCollection Nombre lógico de la colección remota (Firestore/API).
     * @return Resultado vacío en caso de éxito.
     * @security
     * - Limita el tamaño de payloads y normaliza JSON para evitar inyección.
     */
    suspend operator fun invoke(
        event: MatchEvent,
        remoteCollection: String = DEFAULT_REMOTE_COLLECTION,
    ): Result<Unit, AgentError> =
        withContext(ioDispatcher) {
            if (!remoteCollectionRegex.matches(remoteCollection)) {
                return@withContext Result.Err(AgentError.Validation("coleccion_remota_invalida"))
            }
            if (event.id.isBlank()) return@withContext Result.Err(AgentError.Validation("evento_id_vacio"))
            if (event.matchId.isBlank()) return@withContext Result.Err(AgentError.Validation("match_id_vacio"))
            if (event.type.isBlank()) return@withContext Result.Err(AgentError.Validation("evento_tipo_vacio"))
            if (event.seq < 0) return@withContext Result.Err(AgentError.Validation("evento_seq_invalido"))

            val (sanitizedPayload, payloadOverflow) = sanitizePayload(event.payloadJson)
            if (payloadOverflow) {
                return@withContext Result.Err(AgentError.Validation("evento_payload_excede_limite"))
            }

            val sanitizedEvent = event.copy(payloadJson = sanitizedPayload)
            when (val resultadoEvento = matchRepository.registrarEvento(sanitizedEvent)) {
                is Result.Err -> {
                    DecentralizedLogger.e(
                        TAG,
                        "Fallo evento match=${redact(sanitizedEvent.matchId)} tipo=${sanitizedEvent.type}",
                        (resultadoEvento.error as? AgentError.Database)?.cause,
                    )
                    return@withContext resultadoEvento
                }

                is Result.Ok -> {
                    DecentralizedLogger.i(
                        TAG,
                        "Evento registrado match=${redact(sanitizedEvent.matchId)} seq=${sanitizedEvent.seq}",
                    )
                }
            }

            val outboxPayload = sanitizedEvent.payloadJson ?: buildEventPayload(sanitizedEvent)
            val now = clock()
            val outbox = Outbox(
                operationId = idGenerator(),
                entityType = remoteCollection,
                entityId = sanitizedEvent.id,
                op = "CREATE",
                payloadJson = outboxPayload,
                attempt = 0,
                lastError = null,
                createdAtMs = now,
                updatedAtMs = now,
            )

            when (val resultadoOutbox = syncRepository.upsertOutboxOperation(outbox)) {
                is Result.Err -> {
                    DecentralizedLogger.w(
                        TAG,
                        "Outbox no encolado op=${redact(outbox.operationId)}",
                        (resultadoOutbox.error as? AgentError.Database)?.cause,
                    )
                    return@withContext resultadoOutbox
                }

                is Result.Ok -> {
                    DecentralizedLogger.d(
                        TAG,
                        "Outbox encolado op=${redact(outbox.operationId)}",
                    )
                }
            }

            Result.Ok(Unit)
        }

    private fun sanitizePayload(payload: String?): Pair<String?, Boolean> {
        val raw = payload ?: return null to false
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return null to false
        return if (trimmed.length <= MAX_PAYLOAD_LENGTH) trimmed to false else null to true
    }

    private fun buildEventPayload(event: MatchEvent): String {
        val sanitizedType = event.type.escapeJson()
        val sanitizedMatch = event.matchId.escapeJson()
        val sanitizedId = event.id.escapeJson()
        return buildString {
            append('{')
            append("\"eventId\":\"")
            append(sanitizedId)
            append("\",\"matchId\":\"")
            append(sanitizedMatch)
            append("\",\"type\":\"")
            append(sanitizedType)
            append("\",\"actor\":")
            append(event.actorNumero)
            append(',')
            append("\"createdAtMs\":")
            append(event.createdAtMs)
            append(',')
            append("\"seq\":")
            append(event.seq)
            append('}')
        }
    }

    private fun String.escapeJson(): String =
        this.replace("\\", "\\\\")
            .replace("\"", "\\\"")

    private fun redact(value: String): String =
        if (value.length <= 4) "***" else value.take(2) + "***" + value.takeLast(2)

    private companion object {
        private const val MAX_PAYLOAD_LENGTH = 8192
        private const val DEFAULT_REMOTE_COLLECTION = "matchEvents"
        private const val TAG = "RegistrarEventoMatch"
        private val remoteCollectionRegex = Regex("[A-Za-z][A-Za-z0-9_-]{2,64}")
    }
}