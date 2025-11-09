package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.GameSettings
import kotlinx.coroutines.flow.Flow

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
     * @throws AgentError No se lanza directamente; se encapsula en [Result.Err].
     */
    suspend fun guardarPreferencias(
        settings: GameSettings
    ): Result<Unit, AgentError>

    /**
     * Obtiene las preferencias actuales.
     *
     * @return Resultado con las preferencias o error encapsulado.
     * @throws AgentError No se lanza directamente; se envuelve en [Result.Err].
     */
    suspend fun obtenerPreferencias(): Result<GameSettings, AgentError>


    /**
     * Expone un flujo reactivo de las preferencias persistidas.
     *
     * @return Flujo continuo con las preferencias vigentes.
     * @security No incluye datos personales; únicamente banderas de configuración.
     */
    fun observarPreferencias(): Flow<GameSettings>
}