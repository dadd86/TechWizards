package com.diegodiaz.techwizards.domain.model

/**
 * Vista consolidada de un match para la UI.
 *
 * @security No incluye datos sensibles; solo identificadores y puntajes.
 */
data class MatchSnapshot(
    val match: Match?,
    val participantes: List<MatchParticipant>,
    val scores: List<MatchScore>,
    val remotoListo: Boolean
)