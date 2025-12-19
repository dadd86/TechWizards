package com.diegodiaz.techwizards.data.repository.impl.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.diegodiaz.techwizards.data.remote.match.MatchRealtimeFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.match.PlayerReadyDto
import com.diegodiaz.techwizards.data.remote.match.RollResultDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker que reintenta acciones de match cuando la red vuelve a estar disponible.
 */
class MatchActionRetryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val matchId = inputData.getString(KEY_MATCH_ID) ?: return@withContext Result.failure()
        val jugadorNumero = inputData.getLong(KEY_JUGADOR, -1L).takeIf { it >= 0 } ?: return@withContext Result.failure()
        val action = inputData.getString(KEY_ACTION) ?: return@withContext Result.failure()
        val valor = inputData.getInt(KEY_VALOR, 0)
        val realtime = MatchRealtimeFirebaseDataSource()
        try {
            when (action) {
                ACTION_READY -> realtime.marcarListo(matchId, PlayerReadyDto(jugadorNumero, valor))
                ACTION_ROLL -> realtime.registrarLanzamiento(matchId, RollResultDto(jugadorNumero, valor))
                else -> return@withContext Result.failure()
            }
            Result.success()
        } catch (error: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val ACTION_READY = "ready"
        private const val ACTION_ROLL = "roll"
        private const val KEY_ACTION = "action"
        private const val KEY_MATCH_ID = "match_id"
        private const val KEY_JUGADOR = "jugador"
        private const val KEY_VALOR = "valor"

        fun workName(action: String, matchId: String, jugador: Long) = "match_action_${action}_${matchId}_$jugador"

        fun dataReady(matchId: String, jugador: Long, caraElegida: Int): Data =
            Data.Builder()
                .putString(KEY_ACTION, ACTION_READY)
                .putString(KEY_MATCH_ID, matchId)
                .putLong(KEY_JUGADOR, jugador)
                .putInt(KEY_VALOR, caraElegida)
                .build()

        fun dataRoll(matchId: String, jugador: Long, caraObtenida: Int): Data =
            Data.Builder()
                .putString(KEY_ACTION, ACTION_ROLL)
                .putString(KEY_MATCH_ID, matchId)
                .putLong(KEY_JUGADOR, jugador)
                .putInt(KEY_VALOR, caraObtenida)
                .build()
    }
}