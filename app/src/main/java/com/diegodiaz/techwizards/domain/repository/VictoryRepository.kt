package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.VictoryLocation

/**
 * Repositorio de ubicaciones de victoria.
 */
interface VictoryRepository {

    /**
     * Registra una nueva ubicación de victoria.
     */
    suspend fun registrarUbicacion(location: VictoryLocation)

    /**
     * Obtiene todas las ubicaciones registradas, ordenadas por fecha.
     */
    suspend fun obtenerUbicaciones(): List<VictoryLocation>
}
