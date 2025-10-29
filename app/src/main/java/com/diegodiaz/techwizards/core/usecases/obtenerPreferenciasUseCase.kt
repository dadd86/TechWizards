package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Obtiene las preferencias locales almacenadas en DataStore.
 *
 * @property settingsRepository Repositorio de preferencias.
 * @property ioDispatcher Dispatcher para I/O.
 * @security
 * - Solo retorna banderas booleanas, sin exponer PII.
 */
class ObtenerPreferenciasUseCase(
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /** Recupera las preferencias actuales. */
    suspend operator fun invoke(): Result<GameSettings, AgentError> =
        withContext(ioDispatcher) { settingsRepository.obtenerPreferencias() }
}