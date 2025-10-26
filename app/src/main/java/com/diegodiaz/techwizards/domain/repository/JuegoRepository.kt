package com.diegodiaz.techwizards.domain.repository

/**
 * JuegoRepository.kt
 *
 * Aquí se define qué puede hacer el repositorio del juego.
 * De momento solo lo dejamos como interfaz, sin implementación concreta.
 */
interface JuegoRepository {
    // 🔹 Recuperar información general del juego (opcional)
    // suspend fun obtenerInfoJuego(): Juego?

    // 🔹 Registrar o actualizar progreso del jugador
    // suspend fun actualizarProgreso(usuarioId: Int, monedas: Int)
}
