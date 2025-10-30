package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.IdMapEntity
import com.diegodiaz.techwizards.domain.model.IdMap


/**
 * Conversión entre IdMap y dominio.
 *
 * @security
 * - Mantiene la correspondencia 1:1 entre IDs sin exponer datos sensibles.
 */
fun IdMapEntity.toDomain(): IdMap =
    IdMap(
        localTable = localTable,
        localId = localId,
        remoteCollection = remoteCollection,
        remoteId = remoteId,
    )

fun IdMap.toEntity(): IdMapEntity =
    IdMapEntity(
        localTable = localTable,
        localId = localId,
        remoteCollection = remoteCollection,
        remoteId = remoteId,
    )
