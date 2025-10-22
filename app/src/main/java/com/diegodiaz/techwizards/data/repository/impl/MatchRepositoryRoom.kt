package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IMonederoDao
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Partida
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await

class MatchRepositoryRoom(
    private val partidaDao: IPartidaDao,
    private val monederoDao: IMonederoDao
) {
    // -------- Rx nativo --------
    fun historialRx(usuarioId: String) =
        partidaDao.historial(usuarioId).map { list -> list.map { it.toDomain() } }

    fun registrarResultadoRx(partida: Partida, saldoNuevo: Int): Completable =
        partidaDao.insertar(partida.toEntity())
            .andThen(monederoDao.actualizarSaldo(partida.usuarioId, saldoNuevo))

    // -------- Wrappers coroutines (opcional) --------
    fun historial(usuarioId: String): Flow<List<Partida>> =
        historialRx(usuarioId).asFlow()

    suspend fun registrarResultado(partida: Partida, saldoNuevo: Int) {
        registrarResultadoRx(partida, saldoNuevo).await()
    }
}