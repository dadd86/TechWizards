package com.diegodiaz.techwizards.domain.model

/**
 * Mapa de IDs Local ↔ Remoto para sincronización.
 *
 * @property id        Identificador interno (nullable en dominio; en Room es autoGenerate Long).
 * @property type      Tipo de recurso ("usuario", "match", "message", etc.).
 * @property localId   ID generado localmente (UUID, etc.).
 * @property remoteId  ID del servidor (null hasta que sincroniza).
 * @property updatedAt Marca temporal en epoch millis.
 */
data class IdMap(
    val id: Long? = null,
    val type: String,
    val localId: String,
    val remoteId: String?,
    val updatedAt: Long
)

