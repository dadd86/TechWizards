package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.victoryLocation

/**
 * Contrato de acceso a las ubicaciones de victoria.
 *
 * Siguiendo el estilo de JuegoRepository:
 * - Sin Result ni AgentError en esta capa.
 * - Las excepciones de datos se propagan y las gestionan los casos de uso.
 */
interface VictoryRepository {

    /**
     * Registra una nueva ubicación de victoria.
     */
    suspend fun registrarUbicacion(location: victoryLocation)

    /**
     * Obtiene las ubicaciones asociadas a una partida concreta.
     *
     * Si no tenéis matchId, podéis cambiar esto por otro criterio o
     * simplemente devolver la lista completa.
     */
    suspend fun obtenerUbicacionesPorMatch(matchId: Long): List<victoryLocation>
}
