package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.Usuario


/**
 * Acceso a la tabla `Usuario` alineado con el modelo SQL del juego.
 *
 * @security
 * - Las implementaciones deben redactar identificadores antes de loguearlos.
 */
interface UsuarioRepository {
    /**
     * Obtiene al jugador principal de la instalación.
     *
     * @return Usuario actual o error si no se inicializó.
     * @security No retorna columnas sensibles más allá del UID opcional.
     */
    suspend fun obtenerUsuarioPrincipal(): Result<Usuario, AgentError>

    /**
     * Actualiza el saldo de monedas del jugador.
     *
     * @param usuario Jugador a actualizar.
     * @param nuevoSaldo Saldo objetivo no negativo.
     * @return Resultado vacío.
     * @security Se valida que el saldo no sea negativo para evitar corrupción.
     */
    suspend fun actualizarSaldo(usuario: Usuario, nuevoSaldo: Int): Result<Unit, AgentError>

    /**
     * Persiste el último resultado de partida ganado/perdido.
     *
     * @param usuario Jugador a actualizar.
     * @param gano Si ganó la última partida registrada.
     * @return Resultado vacío.
     * @security Evita registrar valores inconsistentes al validar entradas.
     */
    suspend fun actualizarUltimoResultado(usuario: Usuario, gano: Boolean): Result<Unit, AgentError>
}