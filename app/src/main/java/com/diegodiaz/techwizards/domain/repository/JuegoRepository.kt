package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.domain.model.Partida
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

/**
 * Se encarga de gestionar los datos del jugador (usuario, monedero e historial).
 *
 * 🔹 Esta interfaz define QUÉ se puede hacer desde el dominio.
 * 🔹 La implementación (JuegoRepositoryRoom.kt) define CÓMO se hace con Room.
 *
 * @security
 * - Todas las operaciones manipulan únicamente alias y saldos locales almacenados en Room.
 */
interface JuegoRepository {

    // 🔹 RxJava — versión reactiva
    fun observeSaldoRx(usuarioId: String): Flowable<Monedero>
    fun cargarUsuarioRx(usuarioId: String): io.reactivex.rxjava3.core.Maybe<Usuario>
    fun inicializarMonedasRx(usuario: Usuario, monedasIniciales: Int): Completable

    suspend fun sumarMonedas(usuarioId: String, delta: Int)


    // 🔹 Coroutines — versión suspendida
    fun observarSaldo(usuarioId: String): Flow<Monedero>
    suspend fun inicializarMonedas(usuario: Usuario, monedasIniciales: Int)
    /**
     * Reinicia el saldo del monedero, actualizando el usuario si es necesario.
     *
     * @param usuario Perfil local del jugador.
     * @param saldoNuevo Saldo que se aplicará al monedero.
     * @security
     * - Solo actualiza saldo y alias locales; no persiste PII.
     */
    suspend fun reiniciarMonedas(usuario: Usuario, saldoNuevo: Int)

    fun observarHistorial(usuarioId: String, limit: Int = 50): Flow<List<Partida>> //devuelve el historial de partidas
    fun observarMonedero(usuarioId: String): Flow<Monedero> //observa el monedero del usuario en tiempo real
    suspend fun lanzarDado(usuarioId: String): Partida //simular el lanzamiento de dado, modificar el saldo y guardar resultado

    /**
     * Registra un resultado proporcionado por un backend remoto.
     *
     * @param usuarioId Identificador del jugador local.
     * @param resultado Resultado declarado para la tirada.
     * @param cambioMonedas Delta aplicado sobre el monedero.
     * @param fechaMs Marca temporal opcional para auditoría.
     * @return Partida persistida en el historial local.
     */
    suspend fun registrarResultadoRemoto(
        usuarioId: String,
        resultado: Resultado,
        cambioMonedas: Int,
        fechaMs: Long = System.currentTimeMillis(),
    ): Partida
}