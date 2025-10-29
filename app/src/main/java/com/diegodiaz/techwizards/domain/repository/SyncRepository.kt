package com.diegodiaz.techwizards.domain.repository

import io.reactivex.rxjava3.core.Completable
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.IdMap
import com.diegodiaz.techwizards.domain.model.OutboxOperation
import com.diegodiaz.techwizards.domain.model.Tombstone

/**
 * SyncRepository.kt
 *
 * Gestiona la sincronización de datos entre la base local y un posible servidor.
 * En el contexto actual, se usa principalmente para limpiar o reiniciar datos.
 *
 * 🔹 Define QUÉ acciones puede hacer el dominio a nivel global.
 * 🔹 La implementación (SyncRepositoryRoom.kt) define CÓMO se ejecuta en Room.
 */
interface SyncRepository {

    // 🔹 RxJava — versión reactiva
    fun limpiarTodoRx(): Completable

    // 🔹 Coroutines — versión suspendida
    suspend fun limpiarTodo()
    /** Obtiene y bloquea la siguiente operación pendiente. */
    suspend fun obtenerPendientes(limit: Int): Result<List<OutboxOperation>, AgentError>

    /** Marca una operación como reintentada y actualiza los metadatos. */
    suspend fun actualizarIntento(operationId: String, attempt: Int, lastError: String?): Result<Unit, AgentError>

    /** Inserta o actualiza un mapeo de identificadores. */
    suspend fun upsertIdMap(entry: IdMap): Result<Unit, AgentError>

    /** Registra un tombstone para borrado lógico. */
    suspend fun registrarTombstone(tombstone: Tombstone): Result<Unit, AgentError>
}
