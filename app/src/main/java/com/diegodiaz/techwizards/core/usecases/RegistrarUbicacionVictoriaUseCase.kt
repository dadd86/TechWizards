package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.VictoryLocation
import com.diegodiaz.techwizards.domain.repository.VictoryRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger

/**
 * Caso de uso para registrar la ubicación del jugador al ganar una partida.
 *
 * @param victoryRepository Repositorio encargado de persistir ubicaciones.
 * @security No almacena datos personales; solo coordenadas anónimas y precisión.
 */
class RegistrarUbicacionVictoriaUseCase(
    private val victoryRepository: VictoryRepository
) {
    /**
     * Persiste la ubicación de la victoria validando rangos y registrando actividad.
     *
     * @param latitude Latitud en grados decimales (-90, 90).
     * @param longitude Longitud en grados decimales (-180, 180).
     * @param accuracyMetres Precisión declarada por el proveedor de ubicación.
     * @param timestampMillis Marca de tiempo en milisegundos.
     * @return `Unit` tras delegar en el repositorio.
     * @throws IllegalArgumentException Si las coordenadas no están dentro de rangos válidos.
     * @security Se omite cualquier identificador del jugador; solo se registran valores numéricos.
     */
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        accuracyMetres: Double? = null,
        timestampMillis: Long = System.currentTimeMillis()
    ) {
        require(latitude in -90.0..90.0) { "Latitud fuera de rango" }
        require(longitude in -180.0..180.0) { "Longitud fuera de rango" }

        val location = VictoryLocation(
            id = null,
            latitude = latitude,
            longitude = longitude,
            accuracyMetres = accuracyMetres,
            capturedAtMs = timestampMillis
        )

        DecentralizedLogger.d(
            tag = TAG,
            message = "Registrando ubicación de victoria (lat=${location.latitude}, lon=${location.longitude})"
        )

        victoryRepository.registrarUbicacion(location)
    }

    private companion object {
        private const val TAG = "VictoryLocation"
    }
}
