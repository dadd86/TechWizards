package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Evento
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.flow.Flow

/**
 * EventoRepository.kt
 *
 * Se encarga de los eventos del juego (misiones, desafíos, torneos...).
 * Aquí definimos las operaciones disponibles sobre los eventos almacenados.
 *
 * 🔹 Define QUÉ puede hacer el dominio con los eventos.
 * 🔹 La implementación (EventoRepositoryRoom.kt) define CÓMO se ejecuta en Room.
 */
interface EventoRepository {

    // 🔹 RxJava — versión reactiva
    fun obtenerEventosRx(): Flowable<List<Evento>>
    fun marcarCompletadoRx(id: String): Completable

    // 🔹 Coroutines — versión suspendida
    fun obtenerEventos(): Flow<List<Evento>>
    suspend fun marcarCompletado(id: String)
}
