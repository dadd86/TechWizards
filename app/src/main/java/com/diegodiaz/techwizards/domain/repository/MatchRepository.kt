package com.diegodiaz.techwizards.domain.repository

/**
 * MatchRepository.kt
 *
 * Se encarga de las partidas jugadas.
 * Desde aquí se pedirá registrar los resultados o consultar el historial.
 */
interface MatchRepository {
    // 🔹 Registrar una nueva partida en la base de datos
    // suspend fun registrarPartida(resultado: String, monedasGanadas: Int)

    // 🔹 Consultar historial completo
    // suspend fun obtenerHistorial(): List<Partida>
}
