package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.data.local.dao.IMonederoDao
import com.diegodiaz.techwizards.data.local.dao.IUsuarioDao
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.domain.repository.UsuarioRepository
import java.time.Clock

/**
 * Implementación Room para [UsuarioRepository].
 */
class UsuarioRepositoryRoom(
    private val usuarioDao: IUsuarioDao,
    private val monederoDao: IMonederoDao,
    private val clock: Clock = Clock.systemUTC(),
    private val monedasIniciales: Int = 100,
) : UsuarioRepository {

    override suspend fun obtenerUsuarioPrincipal(): Result<Usuario, AgentError> =
        wrap {
            ensureUsuario().toDomain()
        }

    override suspend fun actualizarSaldo(usuario: Usuario, nuevoSaldo: Int): Result<Unit, AgentError> =
        wrap {
            require(nuevoSaldo >= 0) { "Saldo negativo" }
            val filas = usuarioDao.actualizarSaldo(usuario.numero, nuevoSaldo)
            if (filas == 0) throw IllegalStateException("Usuario inexistente")
            monederoDao.actualizarSaldoSuspend(usuario.numero, nuevoSaldo)
            Unit
        }

    override suspend fun actualizarUltimoResultado(usuario: Usuario, gano: Boolean): Result<Unit, AgentError> =
        wrap {
            val filas = usuarioDao.actualizarUltimoResultado(usuario.numero, gano)
            if (filas == 0) throw IllegalStateException("Usuario inexistente")
            Unit
        }

    private suspend fun ensureUsuario() =
        usuarioDao.obtenerUsuarioPrincipal() ?: run {
            val nuevo = Usuario(
                numero = DEFAULT_USUARIO_NUMERO,
                alias = DEFAULT_ALIAS,
                fechaAltaMs = clock.instant().toEpochMilli(),
                monedas = monedasIniciales,
                ganoUltimaPartida = false,
                firebaseUid = null,
            )
            usuarioDao.upsertSuspend(nuevo.toEntity())
            monederoDao.upsertSuspend(
                MonederoEntity(
                    id = "wallet_${nuevo.numero}",
                    usuarioNumero = nuevo.numero,
                    saldo = monedasIniciales,
                )
            )
            nuevo.toEntity()
        }

    private suspend fun <T> wrap(block: suspend () -> T): Result<T, AgentError> =
        try {
            Result.Ok(block())
        } catch (ex: IllegalArgumentException) {
            Result.Err(AgentError.Validation(ex.message ?: "Validación"))
        } catch (ex: Throwable) {
            Result.Err(AgentError.Database(ex))
        }

    companion object {
        private const val DEFAULT_USUARIO_NUMERO = 1L
        private const val DEFAULT_ALIAS = "Aprendiz"
    }
}