package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.domain.repository.SettingsRepository
import com.diegodiaz.techwizards.util.loggingDecentralizedLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Actualiza las preferencias locales del jugador.
 *
 * @property settingsRepository Repositorio de preferencias.
 * @property ioDispatcher Dispatcher dedicado a operaciones de I/O.
 * @security
 * - No persiste datos sensibles, solo banderas de configuración.
 */
class ActualizarPreferenciasUseCase(
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Persiste las nuevas preferencias verificando coherencia.
     *
     * @param settings Preferencias a almacenar.
     * @return Resultado vacío en éxito.
     */
    suspend operator fun invoke(settings: GameSettings): Result<Unit, AgentError> =
        withContext(ioDispatcher) {
            loggingDecentralizedLogger.info(
                event = "preferenciasActualizadas",
                meta = mapOf(
                    "music" to settings.musicEnabled,
                    "sfx" to settings.sfxEnabled,
                    "temaOscuro" to settings.darkThemeEnabled,
                ),
            )
            settingsRepository.guardarPreferencias(settings)
        }
}