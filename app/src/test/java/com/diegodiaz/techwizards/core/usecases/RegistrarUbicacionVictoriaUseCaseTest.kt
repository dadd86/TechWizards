package com.diegodiaz.techwizards.core.usecases

import com.diegodiaz.techwizards.domain.model.VictoryLocation
import com.diegodiaz.techwizards.domain.repository.VictoryRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class RegistrarUbicacionVictoriaUseCaseTest {

    @Test
    fun `cuando las coordenadas son validas se persisten sin alterar`() = runBlocking {
        val fakeRepository = FakeVictoryRepository()
        val useCase = RegistrarUbicacionVictoriaUseCase(fakeRepository)

        useCase(latitude = 10.0, longitude = -20.0, accuracyMetres = 5.0, timestampMillis = 1234L)

        val stored = fakeRepository.ubicaciones.single()
        assertEquals(10.0, stored.latitude, 0.0)
        assertEquals(-20.0, stored.longitude, 0.0)
        assertEquals(5.0, stored.accuracyMetres)
        assertEquals(1234L, stored.capturedAtMs)
    }

    @Test
    fun `cuando la coordenada es invalida lanza IllegalArgumentException`() {
        val fakeRepository = FakeVictoryRepository()
        val useCase = RegistrarUbicacionVictoriaUseCase(fakeRepository)

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                useCase(latitude = 120.0, longitude = 0.0)
            }
        }
    }

    private class FakeVictoryRepository : VictoryRepository {
        val ubicaciones = mutableListOf<VictoryLocation>()

        override suspend fun registrarUbicacion(location: VictoryLocation) {
            ubicaciones.add(location)
        }

        override suspend fun obtenerUbicaciones(): List<VictoryLocation> = ubicaciones
    }
}