package com.diegodiaz.techwizards.domain.model


/**
 * Operación idempotente pendiente de sincronizar con el backend remoto.
 *
 * @property operationId Identificador único del intento.
 * @property entityType Tipo de entidad (Match, MatchEvent, Message, etc.).
 * @property entityId Identificador local de la entidad afectada.
 * @property op Operación a ejecutar (CREATE, UPDATE, DELETE).
 * @property payloadJson Snapshot serializado para la operación.
 * @property attempt Número de reintentos realizados.
 * @property lastError Último error registrado para diagnóstico.
 * @property createdAtMs Marca temporal de encolado.
 * @property updatedAtMs Marca temporal del último intento.
 * @security
 * - No debe contener datos sensibles sin cifrar dentro del payload.
 */
data class Outbox(
    val operationId: String,
    val entityType: String,
    val entityId: String,
    val op: String,
    val payloadJson: String,
    val attempt: Int,
    val lastError: String?,
    val createdAtMs: Long,
    val updatedAtMs: Long,
)
