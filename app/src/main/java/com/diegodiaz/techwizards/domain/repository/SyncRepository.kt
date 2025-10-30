package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.IdMap
import com.diegodiaz.techwizards.domain.model.Outbox
import com.diegodiaz.techwizards.domain.model.Tombstone


/**
 * Coordina la sincronización offline-first mediante tablas Outbox, IdMap y Tombstone.
 *
 * @security
 * - Debe registrar auditoría sin incluir PII en logs.
 */
interface SyncRepository {
    /** Obtiene y bloquea la siguiente operación pendiente. */
    suspend fun obtenerPendientes(limit: Int): Result<List<Outbox>, AgentError>

    /** Marca una operación como reintentada y actualiza los metadatos. */
    suspend fun actualizarIntento(operationId: String, attempt: Int, lastError: String?): Result<Unit, AgentError>

    /**
     * Inserta o actualiza una operación de outbox garantizando idempotencia.
     *
     * @param operation Operación a encolar.
     * @return Resultado de la inserción.
     * @security
     * - La implementación debe validar campos para evitar desbordes y PII en `payloadJson`.
     */
    suspend fun upsertOutboxOperation(operation: Outbox): Result<Unit, AgentError>

    /** Inserta o actualiza un mapeo de identificadores. */
    suspend fun upsertIdMap(entry: IdMap): Result<Unit, AgentError>

    /** Registra un tombstone para borrado lógico. */
    suspend fun registrarTombstone(tombstone: Tombstone): Result<Unit, AgentError>
}