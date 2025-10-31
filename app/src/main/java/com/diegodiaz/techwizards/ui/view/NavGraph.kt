// ui/view/NavGraph.kt
package com.diegodiaz.techwizards.ui.view

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diegodiaz.techwizards.ui.navigation.Ruta

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(navController, startDestination = "bienvenida", modifier) {
        composable("bienvenida") {
            PantallaBienvenida(
                isDarkTheme = isDarkTheme,
                onJugar = { navController.navigate("menu") }
            )
        }
        composable("menu") {
            PantallaMenu(
                isDarkTheme = isDarkTheme,
                onJugar = { navController.navigate("jugar") },
                onHistorial = { navController.navigate("historial") },
                onAjustes = { navController.navigate("ajustes") }
            )
        }
        composable("jugar") {
            PantallaJugar(onBack = { navController.popBackStack() })
        }
        composable("historial") {
            PantallaHistorial(
                isDarkTheme = isDarkTheme,
                onVolverAlMenu = { navController.navigate("menu") }
            )
        }
        composable("ajustes") {
            PantallaAjustes(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onVolverAlMenu = { navController.navigate("menu") }
            )
        }
    }
}
