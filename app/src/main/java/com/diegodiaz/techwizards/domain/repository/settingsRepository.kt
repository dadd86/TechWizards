package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.domain.model.Usuario

/**
 * Acceso a datos del jugador principal.
 *
 * @security
 * - Las implementaciones deben usar el logger descentralizado y sanitizar identificadores.
 */
interface UsuarioRepository {
    /**
     * Obtiene al jugador actual.
     *
     * @return Resultado con el usuario o error tipado.
     * @security
     * - Evita exponer firebaseUid en capas superiores sin redactarlo.
     */
    suspend fun obtenerUsuarioPrincipal(): Result<Usuario, AgentError>

    /**
     * Actualiza el saldo de monedas garantizando límites no negativos.
     *
     * @param usuario Usuario a actualizar.
     * @param nuevoSaldo Nuevo saldo de monedas.
     * @return Resultado vacío indicando éxito o error tipado.
     * @security
     * - Validar el origen del cambio para evitar manipulaciones.
     */
    suspend fun actualizarSaldo(usuario: Usuario, nuevoSaldo: Int): Result<Unit, AgentError>

    /**
     * Persiste el último resultado de partida para mostrar en la UI.
     *
     * @param usuario Usuario afectado.
     * @param gano Indica si ganó la partida.
     * @security
     * - No emite logs con identificadores directos sin redactar.
     */
    suspend fun actualizarUltimoResultado(usuario: Usuario, gano: Boolean): Result<Unit, AgentError>
}