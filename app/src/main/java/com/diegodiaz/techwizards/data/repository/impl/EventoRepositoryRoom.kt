package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IEventoDao
import com.diegodiaz.techwizards.data.local.entity.EventoEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.domain.model.Evento
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await

/**
 * Gestiona los eventos del juego (torneos, retos, misiones...).
 */
class EventoRepositoryRoom(
    private val eventoDao: IEventoDao
) {
    // -------- Rx nativo --------

    /** Devuelve un flujo con todos los eventos disponibles. */
    fun observarEventosRx(): Flowable<List<Evento>> =
        eventoDao.getEventos()
            .map { list: List<EventoEntity> -> list.map { it.toDomain() } }

    /** Marca un evento como completado. */
    fun completarEventoRx(eventoId: String): Completable =
        eventoDao.marcarCompletado(eventoId)

    /** Wrapper coroutines sin usar asFlow() */
    fun observarEventos(): Flow<List<Evento>> = callbackFlow {
        val sub: Disposable = observarEventosRx()
            .subscribe(
                { eventos: List<Evento> -> trySend(eventos).isSuccess },
                { error: Throwable -> close(error) }
            )
        awaitClose { sub.dispose() }
    }



    suspend fun completarEvento(eventoId: String) {
        completarEventoRx(eventoId).await()
    }
}
