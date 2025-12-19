package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.ResolucionTiradaRemota
import com.diegodiaz.techwizards.domain.model.ResolucionTiradaResultado
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Aplica la resolución de tirada enviada por backend y sincroniza Room.
 *
 * @property juegoRepository Repositorio encargado de actualizar historial y monedero.
 * @property ioDispatcher Dispatcher para operaciones de I/O.
 * @security
 * - No almacena PII; redacta identificadores en logs y valida rangos de apuesta.
 */
class ResolverTiradaUseCase(
    private val juegoRepository: JuegoRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Calcula variación de monedas según el ganador y registra la partida.
     *
     * @param usuarioId Identificador local del jugador.
     * @param resolucion Payload devuelto por el backend con resultado y apuestas.
     * @return [Result] con los datos persistidos listos para UI.
     * @security
     * - Limita la apuesta a |monto| <= 500 para evitar desbordes de saldo.
     */
    suspend operator fun invoke(
        usuarioId: String,
        resolucion: ResolucionTiradaRemota,
    ): Result<ResolucionTiradaResultado, AgentError> =
        withContext(ioDispatcher) {
            val apuestaPropia = resolucion.apuestas.firstOrNull { it.usuarioId == usuarioId }
            val monto = (apuestaPropia?.monto ?: 10).coerceIn(1, 500)
            val gano = resolucion.winnerUserId == usuarioId

            val deltaMonedas = if (gano) monto else -monto
            val resultado = if (gano) Resultado.GANADO else Resultado.PERDIDO

            runCatching {
                juegoRepository.registrarResultadoRemoto(
                    usuarioId = usuarioId,
                    resultado = resultado,
                    cambioMonedas = deltaMonedas,
                )
            }.fold(
                onSuccess = { partida ->
                    DecentralizedLogger.i(
                        TAG,
                        "Tirada resuelta remoto face=${resolucion.rolledFace} ganador=${redact(resolucion.winnerUserId)}"
                    )
                    Result.Ok(
                        ResolucionTiradaResultado(
                            partida = partida,
                            rolledFace = resolucion.rolledFace,
                            gano = gano,
                            deltaMonedas = deltaMonedas,
                        )
                    )
                },
                onFailure = { error ->
                    DecentralizedLogger.e(
                        TAG,
                        "No se pudo registrar tirada winner=${redact(resolucion.winnerUserId)} delta=$deltaMonedas",
                        error
                    )
                    Result.Err(AgentError.Database(error))
                }
            )
        }

    private fun redact(id: String?): String = id?.take(2)?.plus("***") ?: "***"

    private companion object {
        private const val TAG = "ResolverTirada"
    }
}