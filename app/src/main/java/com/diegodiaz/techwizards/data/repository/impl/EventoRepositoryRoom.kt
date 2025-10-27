package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IEventoDao
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.domain.model.Evento
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.rx3.await

/**
 * Gestiona los eventos del juego (torneos, retos, misiones...).
 * Se encarga de obtener los eventos, marcarlos como completados, etc.
 */
class EventoRepositoryRoom(
    private val eventoDao: IEventoDao
) {

    // -------- Rx nativo --------

    /** Devuelve un flujo con todos los eventos disponibles. */
    fun observarEventosRx() =
        eventoDao.getEventos().map { list -> list.map { it.toDomain() } }

    /** Marca un evento como completado. */
    fun completarEventoRx(eventoId: String): Completable =
        eventoDao.marcarCompletado(eventoId)

    // -------- Wrappers coroutines (sin asFlow) --------

    fun observarEventos(): Flow<List<Evento>> = callbackFlow {
        val subscription: Disposable = observarEventosRx()
            .subscribe(
                { eventos -> trySend(eventos).isSuccess },
                { error -> close(error) }
            )

        awaitClose { subscription.dispose() }
    }

    suspend fun completarEvento(eventoId: String) {
        completarEventoRx(eventoId).await()
    }
}
