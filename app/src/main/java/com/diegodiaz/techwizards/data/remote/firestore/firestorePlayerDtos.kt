package com.diegodiaz.techwizards.data.remote.firestore

/**
 * DTOs mínimos para leer documentos de jugadores desde Firestore REST.
 *
 * @security
 * Solo expone métricas agregadas públicas (ej. wins) y evita datos sensibles.
 */
data class FirestorePlayerDocumentDto(
    val fields: FirestorePlayerFieldsDto? = null
)

data class FirestorePlayerFieldsDto(
    val wins: FirestoreIntegerValueDto? = null
)

data class FirestoreIntegerValueDto(
    val integerValue: String? = null
)

fun FirestorePlayerDocumentDto.winsOrNull(): Int? =
    fields?.wins?.integerValue?.toIntOrNull()