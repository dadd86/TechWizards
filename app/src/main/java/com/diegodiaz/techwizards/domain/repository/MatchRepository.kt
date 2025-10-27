package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Partida
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

/**
 * MatchRepository.kt
 *
 * Se encarga de las partidas jugadas.
 * Desde aquí se pedirá registrar los resultados o consultar el historial.
 *
 * 🔹 Esta interfaz define QUÉ operaciones puede hacer el dominio.
 * 🔹 Su implementación (MatchRepositoryRoom.kt) define CÓMO lo hace con Room.
 */
interface MatchRepository {

    // 🔹 RxJava — versión reactiva
    fun historialRx(usuarioId: String): Flowable<List<Partida>>
    fun registrarResultadoRx(partida: Partida, saldoNuevo: Int): Completable

    // 🔹 Coroutines — versión suspendida
    fun historial(usuarioId: String): Flow<List<Partida>>
    suspend fun registrarResultado(partida: Partida, saldoNuevo: Int)
}
