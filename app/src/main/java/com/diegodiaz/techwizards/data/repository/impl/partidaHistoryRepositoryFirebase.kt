package com.diegodiaz.techwizards.data.repository.impl

import android.util.Log
import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.core.usecases.LoginBackendUseCase
import com.diegodiaz.techwizards.data.remote.history.PartidaHistoryDto
import com.diegodiaz.techwizards.data.remote.history.PartidaHistoryFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.remote.score.ScorePayload
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.PartidaHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PartidaHistoryRepositoryFirebase(
    private val dataSource: PartidaHistoryFirebaseDataSource,
    private val scoreApi: ScoreApi,
    private val sessionManager: SessionManager,
    private val loginBackendUseCase: LoginBackendUseCase, // ✅ inyectado
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PartidaHistoryRepository {

    override suspend fun registrarPartida(
        firebaseUid: String,
        partida: Partida,
    ): Result<Unit, AgentError> = withContext(ioDispatcher) {

        Log.d("HISTORY", "registrarPartida() START uid=$firebaseUid partida=$partida")

        if (firebaseUid.isBlank()) {
            Log.e("HISTORY", "firebaseUid vacío ❌")
            return@withContext Result.Err(AgentError.Validation("firebaseUid vacío"))
        }

        return@withContext try {
            // =====================================================
            // 1) FIRESTORE -> HISTORIAL + COINS (CRÍTICO)
            // =====================================================
            Log.d("HISTORY", "1) Guardando partida en Firestore...")

            dataSource.registrarPartida(
                firebaseUid = firebaseUid,
                partida = partida.toHistoryDto(),
            )

            Log.d("HISTORY", "1) Firestore OK ✅")

            // =====================================================
            // 2) BACKEND -> RANKING (NO CRÍTICO)
            // =====================================================
            Log.d("HISTORY", "2) Intentando publicar score en backend...")

            val publicadoBackend = try {
                val token = sessionManager.session.value?.token

                if (token.isNullOrBlank()) {
                    Log.d("HISTORY", "No hay backend token -> loginBackend con alias='${partida.aliasJugador}'")
                    val res = loginBackendUseCase.execute(partida.aliasJugador) // ✅ ahora sí
                    Log.d("HISTORY", "loginBackend isSuccess=${res.isSuccess}")
                } else {
                    Log.d("HISTORY", "Ya hay backend token, no hago login")
                }

                publicarScoreEnBackend(partida)
            } catch (e: Exception) {
                Log.e("SCORE", "Excepción publicando score en backend ❌ (Firestore YA OK)", e)
                false
            }

            Log.d("HISTORY", "2) Backend publicado? $publicadoBackend")
            Log.d("HISTORY", "registrarPartida() DONE ✅")

            Result.Ok(Unit)
        } catch (e: Exception) {
            Log.e("HISTORY", "ERROR guardando partida en Firestore ❌", e)
            Result.Err(AgentError.Unknown(e))
        }
    }

    private suspend fun publicarScoreEnBackend(partida: Partida): Boolean {
        Log.d(
            "SCORE",
            "publicarScoreEnBackend() PRE delta=${partida.deltaMonedas} alias='${partida.aliasJugador}'"
        )

        if (partida.deltaMonedas == 0) {
            Log.d("SCORE", "delta=0 -> NO se llama a /scores")
            return false
        }

        // ✅ OJO: tu session guarda token en .token (no backendToken)
        val tokenBackend = sessionManager.session.value?.token
        if (tokenBackend.isNullOrBlank()) {
            Log.e("SCORE", "tokenBackend NULL -> NO se llama a /scores")
            return false
        }

        val payload = ScorePayload(
            alias = partida.aliasJugador,
            deltaMonedas = partida.deltaMonedas
        )

        Log.d("SCORE", "POST /scores payload=$payload")

        scoreApi.publicarScore(
            bearerToken = "Bearer $tokenBackend",
            score = payload
        )

        Log.d("SCORE", "POST /scores OK ✅")
        return true
    }

    private fun Partida.toHistoryDto(): PartidaHistoryDto = PartidaHistoryDto(
        usuarioNumero = usuarioNumero,
        aliasJugador = aliasJugador,
        fechaMs = fecha,
        resultado = resultado.name,
        deltaMonedas = deltaMonedas,
    )
}
