package com.diegodiaz.techwizards.data.remote.firestore

import com.diegodiaz.techwizards.domain.model.LeaderboardEntry

/**
 * DTOs mínimos para ejecutar queries de ranking en Firestore REST.
 *
 * @security
 * No expone datos sensibles; solo alias y métricas agregadas públicas.
 */
data class FirestoreRunQueryRequestDto(
    val structuredQuery: FirestoreStructuredQueryDto
)

data class FirestoreStructuredQueryDto(
    val from: List<FirestoreCollectionSelectorDto>,
    val orderBy: List<FirestoreOrderByDto> = emptyList(),
    val limit: Int? = null
)

data class FirestoreCollectionSelectorDto(
    val collectionId: String,
    val allDescendants: Boolean = false
)

data class FirestoreOrderByDto(
    val field: FirestoreFieldReferenceDto,
    val direction: String = "DESCENDING"
)

data class FirestoreFieldReferenceDto(
    val fieldPath: String
)

data class FirestoreRunQueryResponseDto(
    val document: FirestoreRankingDocumentDto? = null
)

data class FirestoreRankingDocumentDto(
    val name: String? = null,
    val fields: FirestoreRankingFieldsDto? = null
)

data class FirestoreRankingFieldsDto(
    val alias: FirestoreStringValueDto? = null,
    val wins: FirestoreIntegerValueDto? = null
)

data class FirestoreStringValueDto(
    val stringValue: String? = null
)

fun FirestoreRunQueryResponseDto.toLeaderboardEntry(position: Int): LeaderboardEntry? {
    val document = document ?: return null
    val alias = document.fields?.alias?.stringValue?.takeIf { it.isNotBlank() } ?: "Jugador"
    val wins = document.fields?.wins?.integerValue?.toIntOrNull()
    val id = document.name?.substringAfterLast('/')
    return LeaderboardEntry(
        id = id,
        alias = alias,
        score = wins ?: 0,
        position = position,
        wins = wins
    )
}