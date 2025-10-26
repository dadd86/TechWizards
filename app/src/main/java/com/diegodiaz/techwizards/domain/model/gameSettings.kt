package com.diegodiaz.techwizards.domain.model

/**
 * Preferencias locales del jugador para audio, visualización y notificaciones.
 *
 * @property musicEnabled Indica si la música de fondo está habilitada.
 * @property sfxEnabled Indica si los efectos sonoros están habilitados.
 * @property darkThemeEnabled Indica si se usa tema oscuro.
 * @property animationsEnabled Indica si las animaciones están activas.
 * @property notificationsEnabled Indica si las notificaciones in-app están permitidas.
 * @security
 * - No almacena información personal; solo banderas de configuración.
 */
data class GameSettings(
    val musicEnabled: Boolean,
    val sfxEnabled: Boolean,
    val darkThemeEnabled: Boolean,
    val animationsEnabled: Boolean,
    val notificationsEnabled: Boolean,
)