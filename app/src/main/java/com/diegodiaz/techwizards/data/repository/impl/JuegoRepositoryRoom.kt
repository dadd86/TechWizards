package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.*
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow

/**
 * Implementación de [JuegoRepository] respaldada por Room.
 *
 * @param usuarioDao DAO para operaciones con usuarios.
 * @param monederoDao DAO para monedero.
 * @param partidaDao DAO de partidas para historial.
 * @security
 * - Respeta el esquema definido en `PrimerSQL.sql` y evita exponer PII.
 */
class JuegoRepositoryRoom(
    private val usuarioDao: IUsuarioDao,
    private val monederoDao: IMonederoDao,
    private val partidaDao: IPartidaDao

) : JuegoRepository {
    /**
     * Observa el saldo usando el flujo reactivo de Room.
     *
     * @param usuarioId Identificador del jugador.
     * @return [Flowable] con el monedero en dominio.
     * @throws IllegalStateException Propaga errores de Room si ocurren.
     * @security
     * - Solo expone saldo numérico asociado a IDs locales.
     */
    override fun observeSaldoRx(usuarioId: String): Flowable<Monedero> =
        resolveUsuarioNumeroRx(usuarioId)
            .flatMapPublisher { monederoDao.observeSaldo(it) }
            .map { it.toDomain() }

    /**
     * Carga el usuario usando la API RxJava.
     *
     * @param usuarioId Identificador del jugador.
     * @return [Maybe] que emite el usuario si existe.
     * @throws IllegalStateException Room propaga las violaciones de integridad.
     * @security
     * - Mantiene los campos limitados al esquema local.
     */
    override fun cargarUsuarioRx(usuarioId: String): Maybe<Usuario> =
        resolveUsuarioEntityRx(usuarioId).map { it.toDomain() }

    /**
     * Inicializa el usuario y el monedero usando RxJava.
     *
     * @param usuario Usuario a persistir.
     * @param monedasIniciales Saldo inicial.
     * @return [Completable] cuando la transacción finaliza.
     * @throws IllegalStateException Room propaga cualquier error de constraint.
     * @security
     * - Solo persiste alias, saldos y timestamps locales.
     */
    override fun inicializarMonedasRx(usuario: Usuario, monedasIniciales: Int): Completable =
        usuarioDao.upsert(usuario.toEntity())
            .andThen(
                monederoDao.upsert(
                    MonederoEntity(
                        id = "wallet_${usuario.numero}",
                        usuarioNumero = usuario.numero,
                        saldo = monedasIniciales
                    )
                )
            )

    // ------- Coroutines ---------
    /**
     * Observa el saldo del monedero usando corrutinas.
     *
     * @param usuarioId Identificador del jugador.
     * @return Flujo con el monedero convertido a dominio.
     * @throws IllegalStateException Room comunica errores mediante excepciones.
     * @security
     * - Solo expone montos y alias locales.
     */
    override fun observarSaldo(usuarioId: String): Flow<Monedero> =
        flow {
            val usuarioNumero = resolveUsuarioNumero(usuarioId)
            if (usuarioNumero == null) {
                DecentralizedLogger.w(
                    "JuegoRepository",
                    "observarSaldo sin usuario local para ${redactId(usuarioId)}"
                )
                return@flow
            }
            emitAll(monederoDao.observeSaldo(usuarioNumero).asFlow().map { it.toDomain() })
        }

    /**
     * Garantiza que exista el usuario y su monedero antes de iniciar la sesión de juego.
     *
     * @param usuario Perfil local del jugador.
     * @param monedasIniciales Saldo inicial usado en caso de crear el monedero por primera vez.
     */
    override suspend fun inicializarMonedas(usuario: Usuario, monedasIniciales: Int) {
        usuarioDao.upsertSuspend(usuario.toEntity())
        val saldoActual = monederoDao.getMonederoSimple(usuario.numero)?.saldo ?: monedasIniciales
        monederoDao.upsertSuspend(
            MonederoEntity(
                id = "wallet_${usuario.numero}",
                usuarioNumero = usuario.numero,
                saldo = saldoActual
            )
        )
    }

    /**
     * Observa el historial de partidas adjuntando el alias registrado al momento.
     *
     * @param usuarioId Identificador del jugador en texto.
     * @param limit Máximo de partidas a recuperar.
     * @return Flujo con partidas de dominio listas para UI.
     * @throws IllegalStateException Room gestiona errores de consulta.
     * @security
     * - Solo propaga alias y resultados locales.
     */
    override fun observarHistorial(usuarioId: String, limit: Int): Flow<List<Partida>> {
        DecentralizedLogger.d(
            "JuegoRepository",
            "observarHistorial usuarioId=${redactId(usuarioId)} limit=$limit"
        )
        return flow {
            val usuarioNumero = resolveUsuarioNumero(usuarioId)
            if (usuarioNumero == null) {
                DecentralizedLogger.w(
                    "JuegoRepository",
                    "Historial sin usuario local para ${redactId(usuarioId)}"
                )
                emit(emptyList())
                return@flow
            }
            emitAll(
                partidaDao.observarHistorial(usuarioNumero, limit)
                    .map { lista -> lista.map { it.toDomain() } }
            )
        }
    }

    /**
     * Observa cambios en el monedero en tiempo real.
     *
     * @param usuarioId Identificador del jugador.
     * @return Flujo de monedero.
     * @throws IllegalStateException Room expone fallas a través de excepciones.
     * @security
     * - Solo maneja saldo y referencias locales.
     */
    override fun observarMonedero(usuarioId: String): Flow<Monedero> {
        return flow {
            val usuarioNumero = resolveUsuarioNumero(usuarioId)
            if (usuarioNumero == null) {
                DecentralizedLogger.w(
                    "JuegoRepository",
                    "Monedero sin usuario local para ${redactId(usuarioId)}"
                )
                return@flow
            }
            emitAll(monederoDao.observeSaldo(usuarioNumero).asFlow().map { it.toDomain() })
        }
    }

    /**
     * Registra un lanzamiento de dado, actualizando saldo y guardando alias del usuario.
     *
     * @param usuarioId Identificador del jugador.
     * @return Partida registrada con alias y delta de monedas.
     * @throws IllegalStateException Si el usuario no existe o hay problemas de integridad.
     * @security
     * - Solo persiste alias y resultados, en línea con el esquema local.
     */
    override suspend fun lanzarDado(usuarioId: String): Partida {
        val usuarioNumero = resolveUsuarioNumero(usuarioId)
            ?: error("Usuario inexistente para lanzarDado")
        val monedero = monederoDao.getMonederoSimple(usuarioNumero)
        val saldoInicial = monedero?.saldo ?: MONEDAS_INICIALES
        if (monedero == null) {
            monederoDao.upsertSuspend(
                MonederoEntity(
                    id = "wallet_$usuarioNumero",
                    usuarioNumero = usuarioNumero,
                    saldo = saldoInicial
                )
            )
        }
        var saldo = saldoInicial

        val usuario = usuarioDao.getByNumero(usuarioNumero)
            ?: UsuarioEntity(
                numero = usuarioNumero,
                alias = "Player $usuarioNumero",
                fechaAltaMs = System.currentTimeMillis(),
                monedas = saldoInicial,
                ganoUltimaPartida = false,
                firebaseUid = null
            ).also { usuarioDao.upsertSuspend(it) }

        val dado = (1..6).random()
        val gano = dado == 6
        val cambioMonedas = if (gano) 30 else -10
        // Limita el saldo a mínimo 0
        saldo = maxOf(saldo + cambioMonedas, 0)

        val partidaEntity = PartidaEntity(
            usuarioNumero = usuarioNumero,
            fecha = System.currentTimeMillis(),
            resultado = if (gano) Resultado.GANADO else Resultado.PERDIDO,
            cambioMonedas = cambioMonedas,
            nombreJugador = usuario.alias
        )
        val partidaId = partidaDao.insert(partidaEntity)

        monederoDao.actualizarSaldo(usuarioNumero, saldo)

        return partidaEntity.copy(id = partidaId).toDomain(usuario.alias)
    }

    /**
     * Registra una tirada resuelta por backend, reutilizando el historial local.
     *
     * @param usuarioId Identificador del jugador.
     * @param resultado Resultado declarado para la tirada.
     * @param cambioMonedas Delta de monedas aplicado.
     * @param fechaMs Marca temporal opcional para la partida.
     * @return Partida persistida con alias del usuario.
     * @security
     * - Solo escribe alias y saldos locales; no expone PII adicional.
     */
    override suspend fun registrarResultadoRemoto(
        usuarioId: String,
        resultado: Resultado,
        cambioMonedas: Int,
        fechaMs: Long,
    ): Partida {
        val usuarioNumero = resolveUsuarioNumero(usuarioId)
            ?: error("Usuario inexistente para registrar resultado remoto")
        val usuario = usuarioDao.getByNumero(usuarioNumero)
            ?: error("Usuario inexistente para registrar partida remota")
        val saldoActual = monederoDao.getMonederoSimple(usuarioNumero)?.saldo ?: 0
        val saldoAjustado = maxOf(saldoActual + cambioMonedas, 0)

        val partidaEntity = PartidaEntity(
            usuarioNumero = usuarioNumero,
            fecha = fechaMs,
            resultado = resultado,
            cambioMonedas = cambioMonedas,
            nombreJugador = usuario.alias
        )
        val partidaId = partidaDao.insert(partidaEntity)

        monederoDao.actualizarSaldo(usuarioNumero, saldoAjustado)

        return partidaEntity.copy(id = partidaId).toDomain(usuario.alias)
    }

    override suspend fun sumarMonedas(usuarioId: String, delta: Int) {
        require(delta != 0) { "delta no puede ser 0" }

        val usuarioNumero = resolveUsuarioNumero(usuarioId)
            ?: error("Usuario inexistente para sumar monedas")
        val monedero = monederoDao.getMonederoSimple(usuarioNumero)

        val saldoActual = monedero?.saldo ?: MONEDAS_INICIALES
        val nuevoSaldo = maxOf(saldoActual + delta, 0)

        // Si no existía monedero, lo creamos
        if (monedero == null) {
            monederoDao.upsertSuspend(
                MonederoEntity(
                    id = "wallet_$usuarioNumero",
                    usuarioNumero = usuarioNumero,
                    saldo = nuevoSaldo
                )
            )
        } else {
            monederoDao.actualizarSaldo(usuarioNumero, nuevoSaldo)
        }
    }

    private suspend fun resolveUsuarioNumero(usuarioId: String): Long? {
        val numero = usuarioId.toLongOrNull()
        if (numero != null) return numero
        return usuarioDao.getByFirebaseUid(usuarioId)?.numero
    }

    private fun resolveUsuarioNumeroRx(usuarioId: String): Maybe<Long> {
        val numero = usuarioId.toLongOrNull()
        return if (numero != null) {
            Maybe.just(numero)
        } else {
            usuarioDao.getByFirebaseUidRx(usuarioId).map { it.numero }
        }
    }

    private fun resolveUsuarioEntityRx(usuarioId: String): Maybe<UsuarioEntity> {
        val numero = usuarioId.toLongOrNull()
        return if (numero != null) {
            usuarioDao.getByNumeroRx(numero)
        } else {
            usuarioDao.getByFirebaseUidRx(usuarioId)
        }
    }
}
private const val MONEDAS_INICIALES = 100

private fun redactId(id: String?): String =
    id?.takeLast(2)?.padStart(4, '*') ?: "***"