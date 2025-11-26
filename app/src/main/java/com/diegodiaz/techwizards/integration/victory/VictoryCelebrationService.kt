package com.diegodiaz.techwizards.integration.victory

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import java.util.concurrent.TimeUnit

/**
 * Servicio que dispara la celebración de una victoria.
 *
 * @param payload Datos mínimos para la celebración.
 * @return `Unit` tras encolar el trabajo.
 * @throws IllegalStateException No se lanza directamente; los errores se delegan a WorkManager.
 * @security No maneja PII; solo alias y datos de partida.
 */
interface VictoryCelebrationService {
    fun celebrate(payload: VictoryCelebrationPayload)
}

/**
 * Implementación basada en WorkManager para ejecutar celebraciones en segundo plano.
 *
 * @param appContext Contexto de aplicación requerido por WorkManager.
 * @return Instancia lista para encolar trabajos.
 * @throws IllegalArgumentException No se lanza; WorkManager gestiona errores de contexto.
 * @security No registra PII al crear peticiones.
 */
class WorkManagerVictoryCelebrationService(
    private val appContext: Context
) : VictoryCelebrationService {

    override fun celebrate(payload: VictoryCelebrationPayload) {
        runCatching {
            val request = OneTimeWorkRequestBuilder<VictoryCelebrationWorker>()
                .setInputData(payload.toWorkData())
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    10,
                    TimeUnit.SECONDS
                )
                .build()

            WorkManager.getInstance(appContext).enqueue(request)
            DecentralizedLogger.i(TAG, "Celebración programada con WorkManager")
        }.onFailure { error ->
            DecentralizedLogger.e(TAG, "No se pudo programar la celebración", error)
        }
    }

    /**
     * Convierte el payload de celebración a `Data` para WorkManager.
     *
     * @param payload Datos del jugador y captura.
     * @return `Data` con alias y variación de monedas.
     * @throws IllegalStateException No se lanza.
     * @security No serializa información sensible adicional.
     */
    private fun VictoryCelebrationPayload.toWorkData(): Data =
        Data.Builder()
            .putString(VictoryCelebrationWorker.KEY_ALIAS, aliasJugador)
            .putInt(VictoryCelebrationWorker.KEY_DELTA_MONEDAS, deltaMonedas)
            .putLong(VictoryCelebrationWorker.KEY_TIMESTAMP, timestampMillis)
            .putString(VictoryCelebrationWorker.KEY_SCREENSHOT_PATH, screenshotPath)
            .build()

    private companion object {
        private const val TAG = "VictoryService"
    }
}
