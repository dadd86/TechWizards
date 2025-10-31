package com.diegodiaz.techwizards.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.ui.theme.TechWizardsTheme
import com.diegodiaz.techwizards.ui.view.PantallaBienvenida
import com.diegodiaz.techwizards.ui.view.PantallaMenu
import com.diegodiaz.techwizards.ui.view.PantallaJugar
import com.diegodiaz.techwizards.ui.view.PantallaHistorial
import com.diegodiaz.techwizards.ui.view.PantallaAjustes
import com.diegodiaz.techwizards.ui.viewmodel.JuegoViewModel
import com.diegodiaz.techwizards.ui.viewmodel.JuegoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar repositorios y base de datos
        ServiceLocator.init(applicationContext)

        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            val navController = rememberNavController()

            // ⚙️ Instanciamos el ViewModel con Factory
            val ctx = LocalContext.current.applicationContext
            val juegoViewModel: JuegoViewModel = viewModel(factory = JuegoViewModelFactory(ctx))

            TechWizardsTheme(darkTheme = isDarkTheme) {
                NavHost(
                    navController = navController,
                    startDestination = "bienvenida"
                ) {

                    // --- Pantalla de bienvenida ---
                    composable("bienvenida") {
                        PantallaBienvenida(
                            isDarkTheme = isDarkTheme,
                            onJugar = { navController.navigate("menu") }
                        )
                    }

                    // --- Menú principal ---
                    composable("menu") {
                        PantallaMenu(
                            isDarkTheme = isDarkTheme,
                            onJugar = { navController.navigate("jugar") },
                            onHistorial = { navController.navigate("historial") },
                            onAjustes = { navController.navigate("ajustes") }
                        )
                    }

                    // --- Pantalla del juego ---
                    composable("jugar") {
                        PantallaJugar(
                            isDarkTheme = isDarkTheme,
                            viewModel = juegoViewModel,
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }

                    // --- Historial ---
                    composable("historial") {
                        PantallaHistorial(
                            isDarkTheme = isDarkTheme,
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }

                    // --- Ajustes ---
                    composable("ajustes") {
                        PantallaAjustes(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = it },
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }
                }
            }
        }
    }
}
