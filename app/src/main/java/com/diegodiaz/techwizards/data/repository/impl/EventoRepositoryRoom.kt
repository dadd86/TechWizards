package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IEventoDao
import com.diegodiaz.techwizards.data.local.entity.EventoEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Evento
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
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
        eventoDao.getAll().map { list -> list.map { it.toDomain() } }

    /** Marca un evento como completado por un usuario. */
    fun completarEventoRx(eventoId: String, usuarioId: String): Completable =
        eventoDao.marcarCompletado(eventoId, usuarioId)

    // -------- Wrappers coroutines (opcional) --------

    fun observarEventos(): Flow<List<Evento>> =
        observarEventosRx().asFlow()

    suspend fun completarEvento(eventoId: String, usuarioId: String) {
        completarEventoRx(eventoId, usuarioId).await()
    }
}
