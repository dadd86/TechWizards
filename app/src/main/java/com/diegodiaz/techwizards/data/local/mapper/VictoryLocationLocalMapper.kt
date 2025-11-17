package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.VictoryLocationEntity
import com.diegodiaz.techwizards.domain.model.victoryLocation

/**
 * Mapper entre dominio (victoryLocation) y persistencia (VictoryLocationEntity).
 */
class VictoryLocationLocalMapper {

    fun toEntity(model: victoryLocation): VictoryLocationEntity =
        VictoryLocationEntity(
            id = model.id ?: 0L,
            matchId = model.matchId,
            latitude = model.latitude,
            longitude = model.longitude,
            timestampMillis = model.timestampMillis
        )

    fun toModel(entity: VictoryLocationEntity): victoryLocation =
        victoryLocation(
            id = entity.id,
            matchId = entity.matchId,
            latitude = entity.latitude,
            longitude = entity.longitude,
            timestampMillis = entity.timestampMillis
        )
}
