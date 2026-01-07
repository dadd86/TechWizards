package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.PartidaHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso para leer historial remoto desde Firebase.
 *
 * @property repository Repositorio remoto de historial.
 * @property ioDispatcher Dispatcher para operaciones I/O.
 * @security
 * - Limita el tamaño de consulta y requiere UID válido.
 */
class ObservarHistorialRemotoUseCase(
    private val repository: PartidaHistoryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    /**
     * Obtiene historial remoto del jugador.
     *
     * @param firebaseUid UID autenticado.
     * @param limit Cantidad máxima de registros.
     * @return Resultado con el historial remoto.
     * @security
     * - No expone datos fuera del UID autenticado.
     */
    suspend operator fun invoke(
        firebaseUid: String?,
        limit: Int = 50,
    ): Result<List<Partida>, AgentError> = withContext(ioDispatcher) {
        val uid = firebaseUid?.takeIf { it.isNotBlank() }
            ?: return@withContext Result.Err(AgentError.Validation("firebaseUid vacío"))
        repository.obtenerHistorial(uid, limit)
    }
}