package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.cache.MatchSnapshotLocalDataSource
import com.diegodiaz.techwizards.data.local.dao.IMatchDao
import com.diegodiaz.techwizards.data.local.dao.IMatchParticipantDao
import com.diegodiaz.techwizards.data.local.dao.IMatchScoreDao
import com.diegodiaz.techwizards.data.local.dao.IMonederoDao
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.data.transaction.TransactionRunner
import com.diegodiaz.techwizards.domain.model.MatchSnapshot
import com.diegodiaz.techwizards.domain.model.Partida
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await
import io.reactivex.rxjava3.core.Flowable

class MatchRepositoryRoom(
    private val matchDao: IMatchDao,
    private val matchParticipantDao: IMatchParticipantDao,
    private val matchScoreDao: IMatchScoreDao,
    private val partidaDao: IPartidaDao,
    private val monederoDao: IMonederoDao,
    private val transactionRunner: TransactionRunner,
    private val snapshotLocalDataSource: MatchSnapshotLocalDataSource
) {
    // -------- Rx nativo --------
    fun historialRx(usuarioId: Long): Flowable<List<Partida>> =
        partidaDao.historial(usuarioId).map { list -> list.map { it.toDomain() } }


    // -------- Wrappers coroutines (opcional) --------
    fun historial(usuarioId: Long): Flow<List<Partida>> {
        return historialRx(usuarioId).asFlow()
    }

    suspend fun registrarResultado(partida: Partida, saldoNuevo: Int) {
        partidaDao.insert(partida.toEntity())
        monederoDao.actualizarSaldo(partida.usuarioNumero, saldoNuevo)
    }
    suspend fun guardarSnapshot(snapshot: MatchSnapshot) {
        val match = snapshot.match ?: return
        transactionRunner {
            matchDao.upsert(match.toEntity())
            snapshot.participantes.forEach { matchParticipantDao.upsert(it.toEntity()) }
            snapshot.scores.forEach { matchScoreDao.upsert(it.toEntity()) }
        }
        snapshotLocalDataSource.guardar(match.id, snapshot)
    }

    fun observarSnapshot(matchId: String): Flow<MatchSnapshot> {
        val readyFlow = snapshotLocalDataSource.observar(matchId)
        return combine(
            matchDao.observarPorId(matchId),
            matchParticipantDao.observarPorMatch(matchId),
            matchScoreDao.observarPorMatch(matchId),
            readyFlow
        ) { matchEntity, participantes, scores, cache ->
            val lanzamientos = cache?.lanzamientos ?: emptyMap()
            val (ganador, empate) = resolverGanador(lanzamientos)
            MatchSnapshot(
                match = matchEntity?.toDomain(),
                participantes = participantes.map { it.toDomain() },
                scores = scores.map { it.toDomain() },
                remotoListo = cache?.remotoListo ?: false,
                carasElegidas = cache?.carasElegidas ?: emptyMap(),
                lanzamientos = lanzamientos,
                ganadorRonda = cache?.ganadorRonda ?: ganador,
                empate = cache?.empate ?: empate
            )
        }
    }

    private fun resolverGanador(lanzamientos: Map<Long, Int>): Pair<Long?, Boolean> {
        if (lanzamientos.isEmpty()) return null to false
        val max = lanzamientos.maxByOrNull { it.value } ?: return null to false
        val jugadoresConMax = lanzamientos.filterValues { it == max.value }.keys
        val empate = jugadoresConMax.size > 1
        val ganador = if (empate) null else max.key
        return ganador to empate
    }


}