package com.diegodiaz.techwizards.ui.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

/**
 * AppRoot.kt
 *
 * Esta es la raíz de toda la app.
 * Aquí se monta el tema visual (Theme) y el sistema de navegación entre pantallas.
 */
@Composable
fun AppRoot() {
    // Controlador de navegación: es quien maneja el moverse entre pantallas.
    val navController = rememberNavController()

    // Scaffold = estructura base que mantiene coherencia visual en toda la app.
    Scaffold { innerPadding ->
        // Aquí llamamos al NavGraph, que es quien define las rutas de cada pantalla.
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
