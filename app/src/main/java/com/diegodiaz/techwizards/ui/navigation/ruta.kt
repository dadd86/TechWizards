package com.diegodiaz.techwizards.ui.navigation

/**
 * Define rutas navegables de la app.
 */
sealed class ruta(val path: String) {
    data object Bienvenida : ruta("bienvenida")
    data object Menu : ruta("menu")
    data object Jugar : ruta("jugar")
    data object Historial : ruta("historial")
}
