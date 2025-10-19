package com.diegodiaz.techwizards.domain.repository

import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Partida
import kotlinx.coroutines.flow.Flow

interface JuegoRepository {
    fun observarMonedero(): Flow<Monedero>
    fun observarHistorial(limit: Int): Flow<List<Partida>>
    suspend fun lanzarDado(): Partida
}