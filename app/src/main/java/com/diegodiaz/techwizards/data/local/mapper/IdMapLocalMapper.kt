package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.IdMapEntity
import com.diegodiaz.techwizards.domain.model.IdMap

fun IdMapEntity.toDomain() = IdMap(
    id = id,               // Long? en dominio
    type = type,
    localId = localId,
    remoteId = remoteId,
    updatedAt = updatedAt  // <-- faltaba
)

fun IdMap.toEntity() = IdMapEntity(
    id = id ?: 0L,         // <-- entity exige Long (no null) -> 0 para autogenerar si procede
    type = type,
    localId = localId,
    remoteId = remoteId,
    updatedAt = updatedAt  // <-- faltaba
)


