package com.diegodiaz.techwizards.domain.model

/**
 * Marca de borrado (soft-delete) para sincronización.
 *
 * @property id         Identificador interno (nullable en dominio; en Room es autogenerado).
 * @property type       Tipo de recurso borrado ("match", "message", "lobby", etc.).
 * @property deletedId  ID del recurso borrado (puede ser local o remoto).
 * @property deletedAt  Momento del borrado en epoch millis.
 */
data class Tombstone(
    val id: Long? = null,
    val type: String,
    val deletedId: String,
    val deletedAt: Long
)
