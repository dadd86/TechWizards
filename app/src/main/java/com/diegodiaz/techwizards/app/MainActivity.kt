package com.diegodiaz.techwizards.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diegodiaz.techwizards.ui.controller.ControladorJuego
import com.diegodiaz.techwizards.ui.controller.SimpleVmFactory
import com.diegodiaz.techwizards.ui.navigation.ruta
import com.diegodiaz.techwizards.ui.theme.TechWizardsTheme
import com.diegodiaz.techwizards.ui.view.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as App
        setContent {
            TechWizardsTheme {
                val nav = rememberNavController()
                val vm = viewModel<ControladorJuego>(
                    factory = SimpleVmFactory { ControladorJuego(app.repoJuego) }
                )
                NavHost(navController = nav, startDestination = ruta.Bienvenida.path) {
                    composable(ruta.Bienvenida.path) {
                        pantallaBienvenida(onJugar = { nav.navigate(ruta.Menu.path) })
                    }
                    composable(ruta.Menu.path) {
                        pantallaMenu(
                            onIrJugar = { nav.navigate(ruta.Jugar.path) },
                            onIrHistorial = { nav.navigate(ruta.Historial.path) },
                            onSalir = { finish() }
                        )
                    }
                    composable(ruta.Jugar.path) {
                        pantallaJugar(vm, onBack = { nav.popBackStack() })
                    }
                    composable(ruta.Historial.path) {
                        pantallaHistorial(vm, onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
