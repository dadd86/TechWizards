package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.VictoryLocationEntity
import com.diegodiaz.techwizards.domain.model.VictoryLocation


class VictoryLocationLocalMapper {

    fun toEntity(model: VictoryLocation): VictoryLocationEntity =
        VictoryLocationEntity(
            id = model.id ?: 0L,
            latitude = model.latitude,
            longitude = model.longitude,
            accuracyMetres = model.accuracyMetres,
            capturedAtMs = model.capturedAtMs
        )

    fun toModel(entity: VictoryLocationEntity): VictoryLocation =
        VictoryLocation(
            id = entity.id,
            latitude = entity.latitude,
            longitude = entity.longitude,
            accuracyMetres = entity.accuracyMetres,
            capturedAtMs = entity.capturedAtMs
        )
}
