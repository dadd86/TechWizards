package com.diegodiaz.techwizards.ui.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.ui.controller.ControladorPartida
import com.diegodiaz.techwizards.ui.controller.ControladorPartidaFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import com.diegodiaz.techwizards.ui.responsive.Responsive


@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    modifier: Modifier = Modifier
)= Responsive { dims ->
    val context = LocalContext.current
    val db = remember { BaseDeDatos.get(context) }
    val usuarioDao = db.usuarioDao()
    val monederoDao = db.monederoDao()
    val partidaDao = db.partidaDao()
    val repo = remember { JuegoRepositoryRoom(usuarioDao, monederoDao, partidaDao) }
    val usuarioNumero = 1L
    val usuarioId = usuarioNumero.toString()
    LaunchedEffect(Unit) {
        val usuario = Usuario(
            numero = usuarioNumero,
            alias = "Tester",
            fechaAltaMs = System.currentTimeMillis(),
            monedas = 100,
            ganoUltimaPartida = false,
            firebaseUid = null
        )
        // Esto crea  un usuario de prueba
        repo.inicializarMonedasRx(usuario, 100)
            .subscribeOn(Schedulers.io())
            .subscribe({}, { throwable -> throwable.printStackTrace() })
    }

    NavHost(
        navController = navController,
        startDestination = "bienvenida",
        modifier = modifier
    ) {
        composable("bienvenida") {
            PantallaBienvenida(
                isDarkTheme = isDarkTheme,
                onJugar = { navController.navigate("menu") }
            )
        }
        composable("menu") {
            PantallaMenu(
                isDarkTheme = isDarkTheme,
                onJugar = { navController.navigate("partida") },
                onHistorial = { navController.navigate("historial") },
                onAjustes = { navController.navigate("ajustes") }
            )
        }
        composable("partida") {
            val factory = ControladorPartidaFactory(repo, usuarioId)
            val controladorPartida: ControladorPartida = viewModel(factory = factory)
            val uiState = controladorPartida.ui.collectAsState().value
            PantallaPartida(
                isDarkTheme = isDarkTheme,
                uiState = uiState,
                onVolverAlMenu = { navController.navigate("menu") },
                onElegirNumero = { num -> controladorPartida.elegirNumero(num) }
            )
        }
        composable("historial") {
            val viewModel: ControladorPartida = viewModel(factory = ControladorPartidaFactory(repo, usuarioId))
            val historial by viewModel.historial.collectAsState()
            PantallaHistorial(
                isDarkTheme = isDarkTheme,
                historial = historial,
                onVolverAlMenu = {  navController.navigate("menu") }
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
