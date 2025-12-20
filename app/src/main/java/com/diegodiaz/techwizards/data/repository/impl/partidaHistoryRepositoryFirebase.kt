package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.data.remote.history.PartidaHistoryFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.history.PartidaHistoryDto
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.PartidaHistoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación Firebase de [PartidaHistoryRepository].
 *
 * @security
 * - Persistencia remota por UID con datos mínimos de juego.
 */
class PartidaHistoryRepositoryFirebase(
    private val dataSource: PartidaHistoryFirebaseDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PartidaHistoryRepository {

    override suspend fun registrarPartida(
        firebaseUid: String,
        partida: Partida,
    ): Result<Unit, AgentError> = withContext(ioDispatcher) {
        if (firebaseUid.isBlank()) {
            return@withContext Result.Err(AgentError.Validation("firebaseUid vacío"))
        }

        return@withContext try {
            dataSource.registrarPartida(
                firebaseUid = firebaseUid,
                partida = partida.toHistoryDto(),
            )
            Result.Ok(Unit)
        } catch (e: Exception) {
            Result.Err(AgentError.Unknown(e))
        }
    }

    private fun Partida.toHistoryDto(): PartidaHistoryDto = PartidaHistoryDto(
        usuarioNumero = usuarioNumero,
        aliasJugador = aliasJugador,
        fechaMs = fecha,
        resultado = resultado.name,
        deltaMonedas = deltaMonedas,
    )
}