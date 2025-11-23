package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.VictoryLocation
import com.diegodiaz.techwizards.domain.repository.VictoryRepository

/**
 * Caso de uso para registrar la ubicación del jugador al ganar una partida.
 *
 * No se usa interface; sigue el patrón general del proyecto (clases simples).
 */
class RegistrarUbicacionVictoriaUseCase(
    private val victoryRepository: VictoryRepository
) {

    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        accuracyMetres: Double? = null,
        timestampMillis: Long = System.currentTimeMillis()
    ) {
        val location = VictoryLocation(
            id = null,
            latitude = latitude,
            longitude = longitude,
            accuracyMetres = accuracyMetres,
            capturedAtMs = timestampMillis
        )

        victoryRepository.registrarUbicacion(location)
    }
}
