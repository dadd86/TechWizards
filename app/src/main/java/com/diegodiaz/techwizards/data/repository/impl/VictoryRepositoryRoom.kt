package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IVictoryLocationDao
import com.diegodiaz.techwizards.data.local.mapper.VictoryLocationLocalMapper
import com.diegodiaz.techwizards.data.transaction.TransactionRunner
import com.diegodiaz.techwizards.domain.model.VictoryLocation
import com.diegodiaz.techwizards.domain.repository.VictoryRepository

/**
 * Implementación de VictoryRepository basada en Room.
 */
class VictoryRepositoryRoom(
    private val dao: IVictoryLocationDao,
    private val transactionRunner: TransactionRunner,
    private val mapper: VictoryLocationLocalMapper
) : VictoryRepository {

    override suspend fun registrarUbicacion(location: VictoryLocation) {
        transactionRunner.run {
            val entity = mapper.toEntity(location)
            dao.insert(entity)
        }
    }

    override suspend fun obtenerUbicaciones(): List<VictoryLocation> {
        val entities = dao.getAll()
        return entities.map(mapper::toModel)
    }
}
