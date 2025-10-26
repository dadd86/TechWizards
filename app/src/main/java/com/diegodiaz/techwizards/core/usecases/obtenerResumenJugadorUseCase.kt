package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.repository.UsuarioRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Recupera el estado del jugador para mostrarlo en el menú principal.
 *
 * @property usuarioRepository Repositorio de jugadores.
 * @property ioDispatcher Dispatcher para I/O.
 * @security
 * - Evita exponer información sensible; únicamente retorna datos visibles al usuario.
 */
class ObtenerResumenJugadorUseCase(
    private val usuarioRepository: UsuarioRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    /**
     * Obtiene el jugador actual.
     *
     * @return Resultado con el usuario.
     */
    suspend operator fun invoke(): Result<Usuario, AgentError> =
        withContext(ioDispatcher) { usuarioRepository.obtenerUsuarioPrincipal() }
}