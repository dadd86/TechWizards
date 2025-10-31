package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IMonederoDao
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.dao.IUsuarioDao
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.data.transaction.TransactionRunner
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import kotlin.random.Random

/**
 * Implementación de [JuegoRepository] respaldada por Room.
 *
 * @property usuarioDao DAO para la tabla `Usuario`.
 * @property monederoDao DAO para la tabla `Monedero`.
 * @property partidaDao DAO para la tabla `Partida`.
 * @property transactionRunner Ejecuta operaciones atomizadas.
 * @property clock Fuente de tiempo para sellos de auditoría.
 * @property random Generador de resultados del dado.
 */
class JuegoRepositoryRoom(
    private val usuarioDao: IUsuarioDao,
    private val monederoDao: IMonederoDao,
    private val partidaDao: IPartidaDao,
    private val transactionRunner: TransactionRunner,
    private val clock: Clock = Clock.systemUTC(),
    private val random: Random = Random.Default,
    private val monedasIniciales: Int = 100,
    ) : JuegoRepository {

        override fun observeSaldoRx(usuarioId: String): Flowable<Monedero> =
            monederoDao.observeSaldo(usuarioId.toUsuarioNumero()).map { it.toDomain() }

        override fun cargarUsuarioRx(usuarioId: String): Maybe<Usuario> =
            usuarioDao.getByNumeroRx(usuarioId.toUsuarioNumero()).map { it.toDomain() }

        override fun inicializarMonedasRx(usuario: Usuario, monedasIniciales: Int): Completable =
            usuarioDao.upsert(usuario.toEntity())
                .andThen(
                    monederoDao.upsert(
                        MonederoEntity(
                            id = walletId(usuario.numero),
                            usuarioNumero = usuario.numero,
                            saldo = monedasIniciales,
                        )
                    )
                )

        override fun observarSaldo(usuarioId: String): Flow<Monedero> =
            observarMonedero(usuarioId)

        override suspend fun inicializarMonedas(usuario: Usuario, monedasIniciales: Int) {
            transactionRunner {
                usuarioDao.upsertSuspend(usuario.toEntity())
                monederoDao.upsertSuspend(
                    MonederoEntity(
                        id = walletId(usuario.numero),
                        usuarioNumero = usuario.numero,
                        saldo = monedasIniciales,
                    )
                )
            }
        }

        override fun observarHistorial(usuarioId: String, limit: Int): Flow<List<Partida>> =
            flow {
                val numero = usuarioId.toUsuarioNumero()
                ensureUsuario(numero)
                emitAll(partidaDao.historialFlow(numero, limit).map { list -> list.map { it.toDomain() } })
            }

        override fun observarMonedero(usuarioId: String): Flow<Monedero> =
            flow {
                val numero = usuarioId.toUsuarioNumero()
                ensureUsuario(numero)
                emitAll(
                    monederoDao.observeSaldoFlow(numero)
                        .filterNotNull()
                        .map { it.toDomain() }
                )
            }

        override suspend fun lanzarDado(usuarioId: String): Partida =
            transactionRunner {
                val numero = usuarioId.toUsuarioNumero()
                val usuario = ensureUsuario(numero)
                val tiro = random.nextInt(1, 7)
                val gano = tiro >= UMBRAL_EXITO
                val delta = if (gano) GANANCIA else PERDIDA
                val nuevoSaldo = (usuario.monedas + delta).coerceAtLeast(0)

                usuarioDao.actualizarSaldo(numero, nuevoSaldo)
                monederoDao.actualizarSaldoSuspend(numero, nuevoSaldo)
                usuarioDao.actualizarUltimoResultado(numero, gano)

                val timestamp = clock.instant().toEpochMilli()
                val resultado = if (gano) Resultado.GANADO else Resultado.PERDIDO
                val partidaEntity = PartidaEntity(
                    id = 0L,
                    usuarioNumero = numero,
                    fecha = timestamp,
                    resultado = resultado,
                    cambioMonedas = delta,
                )
                val partidaId = partidaDao.insertarSuspend(partidaEntity)
                partidaEntity.copy(id = partidaId).toDomain()
            }

        private suspend fun ensureUsuario(numero: Long): Usuario {
            val existente = usuarioDao.getByNumero(numero)?.toDomain()
            if (existente != null) {
                return existente
            }

            val nuevo = Usuario(
                numero = numero,
                alias = DEFAULT_ALIAS,
                fechaAltaMs = clock.instant().toEpochMilli(),
                monedas = monedasIniciales,
                ganoUltimaPartida = false,
                firebaseUid = null,
            )

            usuarioDao.upsertSuspend(nuevo.toEntity())
            monederoDao.upsertSuspend(
                MonederoEntity(
                    id = walletId(numero),
                    usuarioNumero = numero,
                    saldo = monedasIniciales,
                )
            )
            return nuevo
        }

        private fun String.toUsuarioNumero(): Long = toLongOrNull() ?: DEFAULT_USUARIO_NUMERO

        private fun walletId(numero: Long) = "wallet_$numero"

        companion object {
            private const val DEFAULT_ALIAS = "Aprendiz"
            private const val UMBRAL_EXITO = 4
            private const val GANANCIA = 10
            private const val PERDIDA = -5
            private const val DEFAULT_USUARIO_NUMERO = 1L
        }
    }