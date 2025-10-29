package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.TombstoneEntity
import com.diegodiaz.techwizards.domain.model.Tombstone

fun TombstoneEntity.toDomain() = Tombstone(
    id = id,
    type = type,
    deletedId = deletedId,
    deletedAt = deletedAt
)

fun Tombstone.toEntity() = TombstoneEntity(
    id = id ?: 0L,
    type = type,
    deletedId = deletedId,
    deletedAt = deletedAt
)
