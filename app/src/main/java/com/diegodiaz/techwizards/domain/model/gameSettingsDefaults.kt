package com.diegodiaz.techwizards.domain.model

/**
 * Proveedor de valores iniciales seguros para las preferencias del juego.
 *
 * @return [`GameSettings`] con banderas activadas por defecto y el idioma en español.
 * @throws IllegalStateException No se lanza; la constante es inmutable y segura.
 * @security No expone PII ni rutas de medios, solo valores booleanos y un tag de idioma.
 */
val gameSettingsDefault = GameSettings(
    musicEnabled = true,
    sfxEnabled = true,
    darkThemeEnabled = false,
    animationsEnabled = true,
    notificationsEnabled = true,
    selectedMusicUri = null,
    selectedLanguageTag = "es-ES"
)