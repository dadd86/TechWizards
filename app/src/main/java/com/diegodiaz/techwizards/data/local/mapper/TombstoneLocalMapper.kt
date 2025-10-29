package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.TombstoneEntity
import com.diegodiaz.techwizards.domain.model.Tombstone

/**
 * Conversión entre tombstones y dominio.
 *
 * @security
 * - Solo traslada metadatos de borrado lógico sin PII.
 */
fun TombstoneEntity.toDomain(): Tombstone =
    Tombstone(
        tableName = tableName,
        entityId = entityId,
        deletedAtMs = deletedAtMs,
    )

fun Tombstone.toEntity(): TombstoneEntity =
    TombstoneEntity(
        tableName = tableName,
        entityId = entityId,
        deletedAtMs = deletedAtMs,
    )
