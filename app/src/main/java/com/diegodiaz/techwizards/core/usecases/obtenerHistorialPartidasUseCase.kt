package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Recupera el historial de partidas finalizadas para mostrar en la pantalla de historial.
 *
 * @property matchRepository Repositorio de partidas.
 * @property ioDispatcher Dispatcher destinado a I/O.
 * @security
 * - Solo expone datos necesarios para la UI sin PII adicional.
 */
class ObtenerHistorialPartidasUseCase(
    private val matchRepository: MatchRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Obtiene el historial limitado por [limite].
     *
     * @param limite Número máximo de partidas.
     * @return Resultado con la lista de partidas.
     * @security
     * - El límite previene extracciones masivas no autorizadas.
     */
    suspend operator fun invoke(limite: Int = 20): Result<List<Match>, AgentError> =
        withContext(ioDispatcher) {
            require(limite in 1..100) { "Límite inválido" }
            val resultado = matchRepository.obtenerHistorial(limite)
            if (resultado is Result.Ok) {
                DecentralizedLogger.i(
                    "HistorialPartidas",
                    "Historial recuperado cantidad=${resultado.value.size}",
                    )
            }
            resultado
        }
}