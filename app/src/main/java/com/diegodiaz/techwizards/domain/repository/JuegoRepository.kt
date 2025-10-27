package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Usuario
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

/**
 * JuegoRepository.kt
 *
 * Se encarga de gestionar los datos del jugador (usuario y monedas).
 * Aquí se definen las operaciones principales relacionadas con el monedero.
 *
 * 🔹 Esta interfaz define QUÉ se puede hacer desde el dominio.
 * 🔹 La implementación (JuegoRepositoryRoom.kt) define CÓMO se hace con Room.
 */
interface JuegoRepository {

    // 🔹 RxJava — versión reactiva
    fun observeSaldoRx(usuarioId: String): Flowable<Monedero>
    fun cargarUsuarioRx(usuarioId: String): io.reactivex.rxjava3.core.Maybe<Usuario>
    fun inicializarMonedasRx(usuario: Usuario, monedasIniciales: Int): Completable

    // 🔹 Coroutines — versión suspendida
    fun observarSaldo(usuarioId: String): Flow<Monedero>
    suspend fun inicializarMonedas(usuario: Usuario, monedasIniciales: Int)
}
