package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.domain.model.Match
import com.diegodiaz.techwizards.domain.model.MatchEvent
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import com.diegodiaz.techwizards.domain.repository.UsuarioRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Registra el lanzamiento del dado, actualiza el saldo y guarda el evento en la bitácora.
 *
 * @property usuarioRepository Repositorio de datos del jugador.
 * @property matchRepository Repositorio de partidas y eventos.
 * @property ioDispatcher Dispatcher para operaciones de I/O.
 * @security
 * - Ofusca identificadores en logs utilizando el logger descentralizado.
 */
class RegistrarLanzamientoUseCase(
    private val usuarioRepository: UsuarioRepository,
    private val matchRepository: MatchRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Ejecuta el registro de evento y actualización de saldo.
     *
     * @param match Partida en la que ocurre el lanzamiento.
     * @param event Evento de lanzamiento a persistir.
     * @param monedasDelta Delta de monedas a aplicar (puede ser negativo).
     * @param gano Indica si el jugador ganó el lanzamiento.
     * @return Resultado vacío que indica éxito o error.
     * @security
     * - Valida entradas para impedir saldos negativos y logs con PII.
     */
    suspend operator fun invoke(
        match: Match,
        event: MatchEvent,
        monedasDelta: Int,
        gano: Boolean,
    ): Result<Unit, AgentError> =
        withContext(ioDispatcher) {
            require(monedasDelta in -500..500) { "Delta de monedas inválido" }
            when (val eventoResultado = matchRepository.registrarEvento(event)) {
                is Result.Err -> {
                    DecentralizedLogger.error(
                        event = "registroEventoFallido",
                        meta = mapOf(
                            "matchId" to loggingDecentralizedLogger.redact(match.id),
                            "tipo" to event.type,
                        ),
                        throwable = (eventoResultado.error as? AgentError.Database)?.cause,
                    )
                    return@withContext eventoResultado
                }

                is Result.Ok -> {
                    loggingDecentralizedLogger.info(
                        event = "eventoRegistrado",
                        meta = mapOf(
                            "matchId" to loggingDecentralizedLogger.redact(match.id),
                            "seq" to event.seq,
                        ),
                    )
                }
            }

            val usuarioResultado = usuarioRepository.obtenerUsuarioPrincipal()
            val usuario = when (usuarioResultado) {
                is Result.Err -> return@withContext usuarioResultado
                is Result.Ok -> usuarioResultado.value
            }

            val nuevoSaldo = (usuario.monedas + monedasDelta).coerceAtLeast(0)
            when (val saldoResultado = usuarioRepository.actualizarSaldo(usuario, nuevoSaldo)) {
                is Result.Err -> return@withContext saldoResultado
                is Result.Ok -> {
                    loggingDecentralizedLogger.info(
                        event = "saldoActualizado",
                        meta = mapOf(
                            "usuario" to loggingDecentralizedLogger.redact(usuario.numero.toString()),
                            "saldo" to nuevoSaldo,
                        ),
                    )
                }
            }

            when (val resultado = usuarioRepository.actualizarUltimoResultado(usuario, gano)) {
                is Result.Err -> {
                    loggingDecentralizedLogger.warn(
                        event = "ultimoResultadoNoActualizado",
                        meta = mapOf("usuario" to loggingDecentralizedLogger.redact(usuario.numero.toString())),
                    )
                    return@withContext resultado
                }

                is Result.Ok -> {
                    loggingDecentralizedLogger.info(
                        event = "ultimoResultadoActualizado",
                        meta = mapOf(
                            "usuario" to loggingDecentralizedLogger.redact(usuario.numero.toString()),
                            "gano" to gano,
                        ),
                    )
                }
            }

            Result.Ok(Unit)
        }
}