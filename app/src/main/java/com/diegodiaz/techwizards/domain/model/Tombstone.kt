package com.diegodiaz.techwizards.domain.model

/**
 * Registro de borrado lógico para replicación confiable.
 *
 * @property tableName Nombre de la tabla local afectada.
 * @property entityId Identificador de la entidad eliminada.
 * @property deletedAtMs Marca temporal del borrado lógico.
 * @security
 * - No contiene datos sensibles; solo metadatos de eliminación.
 */
data class Tombstone(
    val tableName: String,
    val entityId: String,
    val deletedAtMs: Long,
)