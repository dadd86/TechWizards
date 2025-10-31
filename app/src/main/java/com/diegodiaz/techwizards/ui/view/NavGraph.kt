package com.diegodiaz.techwizards.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diegodiaz.techwizards.ui.viewmodel.JuegoViewModel

/**
 * NavGraph.kt
 *
 * Define todas las pantallas y cómo se conectan entre sí.
 * Es el “mapa” de navegación dentro del juego.
 */
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {

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

        // 🟩 Pantalla para jugar (ya conectada al ViewModel)
        composable("jugar") {
            val viewModel: JuegoViewModel = viewModel()
            PantallaJugar(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
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

// ⚠️ Corrige las firmas de las siguientes funciones
// (tienen que recibir un lambda que no devuelva nada, no un Boolean)

@Composable
fun PantallaAjustes(onBack: () -> Unit) {
    TODO("Not yet implemented")
}

@Composable
fun PantallaHistorial(onBack: () -> Unit) {
    TODO("Not yet implemented")
}
