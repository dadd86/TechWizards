// ui/navigation/Rutas.kt
package com.diegodiaz.techwizards.ui.navigation

sealed class Ruta(val path: String) {
    data object Bienvenida : Ruta("bienvenida")
    data object Menu       : Ruta("menu")
    data object Jugar      : Ruta("jugar")
    data object Historial  : Ruta("historial")
    data object Ajustes    : Ruta("ajustes")
}
