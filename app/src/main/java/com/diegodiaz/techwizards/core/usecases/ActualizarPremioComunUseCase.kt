package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Caso de uso para actualizar el premio común de manera segura.
 *
 * @property scoreRepository Fuente remota para leaderboard/premios.
 * @property sessionManager Gestiona la sesión autenticada necesaria para autorizar la operación.
 * @property ioDispatcher Dispatcher para aislar llamadas de red en I/O.
 * @security No expone el token y valida inputs mínimos antes de enviar al backend.
 */
class ActualizarPremioComunUseCase(
    private val scoreRepository: ScoreRepository,
    private val sessionManager: SessionManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    /**
     * Valida los datos del premio y delega en el repositorio remota para persistirlo.
     *
     * @param descripcion Texto visible del premio.
     * @param valor Valor entero positivo.
     * @return `Result.Ok` con el premio actualizado o `Result.Err` en caso de error/validación.
     */
    suspend operator fun invoke(descripcion: String, valor: Int): Result<CommonPrize, AgentError> =
        withContext(ioDispatcher) {
            val session = sessionManager.session.value
                ?: return@withContext Result.Err(AgentError.Validation("Se requiere sesión para actualizar el premio"))

            if (descripcion.isBlank()) return@withContext Result.Err(AgentError.Validation("La descripción no puede estar vacía"))
            if (valor !in 1..100_000) return@withContext Result.Err(AgentError.Validation("El valor debe estar entre 1 y 100000"))

            runCatching {
                scoreRepository.actualizarPremioComun(session, CommonPrize(descripcion.trim(), valor))
            }.fold(
                onSuccess = { Result.Ok(it) },
                onFailure = { Result.Err(AgentError.Unknown(it)) }
            )
        }
}