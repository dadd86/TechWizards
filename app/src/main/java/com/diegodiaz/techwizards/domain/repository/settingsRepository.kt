package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.core.common.Result


/**
 * Maneja las preferencias locales del jugador.
 *
 * @security
 * - No almacena datos sensibles, solo flags de configuración.
 */
interface SettingsRepository {

    /**
     * Guarda las preferencias del jugador.
     *
     * @param settings Preferencias a guardar.
     * @return Resultado vacío en éxito.
     */
    suspend fun guardarPreferencias(
        settings: GameSettings
    ): Result<Unit, AgentError>

    suspend fun obtenerPreferencias(): Result<GameSettings, AgentError>
}