package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.PartidaHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso para registrar historial remoto en Firebase.
 *
 * @property repository Repositorio remoto de historial.
 * @property ioDispatcher Dispatcher para operaciones I/O.
 * @security
 * - Usa UID autenticado y evita almacenar PII adicional.
 */
class RegistrarHistorialRemotoUseCase(
    private val repository: PartidaHistoryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    /**
     * Registra la partida en el historial remoto del jugador.
     *
     * @param firebaseUid UID autenticado.
     * @param partida Partida registrada localmente.
     * @return Resultado de la operación.
     * @security
     * - Valida UID no vacío y delega la escritura al repositorio remoto.
     */
    suspend operator fun invoke(
        firebaseUid: String?,
        partida: Partida,
    ): Result<Unit, AgentError> = withContext(ioDispatcher) {
        val uid = firebaseUid?.takeIf { it.isNotBlank() }
            ?: return@withContext Result.Err(AgentError.Validation("firebaseUid vacío"))

        repository.registrarPartida(uid, partida)
    }
}