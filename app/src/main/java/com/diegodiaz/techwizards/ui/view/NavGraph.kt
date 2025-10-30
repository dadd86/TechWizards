package com.diegodiaz.techwizards.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diegodiaz.techwizards.ui.view.*


/**
 * NavGraph.kt
 *
 * Aquí definimos todas las pantallas y cómo se conectan entre sí.
 * Básicamente es el “mapa” de la navegación dentro del juego.
 */
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

    // NavHost define el gráfico de navegación.
    // startDestination = pantalla inicial que se abre al ejecutar la app.
    NavHost(
        navController = navController,
        startDestination = "bienvenida",
        modifier = modifier
    ) {
        // Pantalla inicial (bienvenida)
        composable("bienvenida") {
            PantallaBienvenida(onNavigateToMenu = { navController.navigate("menu") })
        }

        // Menú principal
        composable("menu") {
            PantallaMenu(
                onNavigateToJugar = { navController.navigate("jugar") },
                onNavigateToHistorial = { navController.navigate("historial") },
                onNavigateToAjustes = { navController.navigate("ajustes") }
            )
        }

        // Pantalla para jugar
        composable("jugar") {
            PantallaJugar(onBack = { navController.popBackStack() })
        }

        // Pantalla del historial de partidas
        composable("historial") {
            PantallaHistorial(onBack = { navController.popBackStack() })
        }

        // Pantalla de ajustes
        composable("ajustes") {
            PantallaAjustes(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun PantallaAjustes(onBack: () -> Boolean) {
    TODO("Not yet implemented")
}

@Composable
fun PantallaHistorial(onBack: () -> Boolean) {
    TODO("Not yet implemented")
}
