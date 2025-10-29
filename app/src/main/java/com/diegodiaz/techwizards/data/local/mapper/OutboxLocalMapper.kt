package com.diegodiaz.techwizards.data.local.mapper

import com.diegodiaz.techwizards.data.local.entity.OutboxEntity
import com.diegodiaz.techwizards.domain.model.Outbox

/**
 * Mapeo entre operaciones outbox y dominio.
 *
 * @security
 * - Mantiene campos idempotentes para auditoría sin incluir PII.
 */
fun OutboxEntity.toDomain(): Outbox =
    Outbox(
        operationId = operationId,
        entityType = entityType,
        entityId = entityId,
        op = op,
        payloadJson = payloadJson,
        attempt = attempt,
        lastError = lastError,
        createdAtMs = createdAtMs,
        updatedAtMs = updatedAtMs,
    )

fun Outbox.toEntity(): OutboxEntity =
    OutboxEntity(
        operationId = operationId,
        entityType = entityType,
        entityId = entityId,
        op = op,
        payloadJson = payloadJson,
        attempt = attempt,
        lastError = lastError,
        createdAtMs = createdAtMs,
        updatedAtMs = updatedAtMs,
    )