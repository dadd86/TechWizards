package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IVictoryLocationDao
import com.diegodiaz.techwizards.data.local.mapper.VictoryLocationLocalMapper
import com.diegodiaz.techwizards.data.transaction.TransactionRunner
import com.diegodiaz.techwizards.domain.model.victoryLocation
import com.diegodiaz.techwizards.domain.repository.VictoryRepository

/**
 * Implementación Room del repositorio de ubicaciones de victoria.
 */
class VictoryRepositoryRoom(
    private val dao: IVictoryLocationDao,
    private val transactionRunner: TransactionRunner,
    private val mapper: VictoryLocationLocalMapper
) : VictoryRepository {

    override suspend fun registrarUbicacion(location: victoryLocation) {
        transactionRunner.run {
            val entity = mapper.toEntity(location)
            dao.insert(entity)
        }
    }

    override suspend fun obtenerUbicacionesPorMatch(matchId: Long): List<victoryLocation> {
        val entities = dao.getByMatch(matchId)
        return entities.map(mapper::toModel)
    }
}
