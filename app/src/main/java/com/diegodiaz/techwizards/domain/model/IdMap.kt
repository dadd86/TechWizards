package com.diegodiaz.techwizards.domain.model

/**
 * Correlación entre identificadores locales y remotos.
 *
 * @property localTable Nombre lógico de la tabla local.
 * @property localId Identificador local (UUID/PK).
 * @property remoteCollection Colección remota (Firestore u otro backend).
 * @property remoteId Identificador remoto de la entidad.
 * @security
 * - No persiste información sensible; solo claves técnicas.
 */
data class IdMap(
    val localTable: String,
    val localId: String,
    val remoteCollection: String,
    val remoteId: String,
)

