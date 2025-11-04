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
import androidx.compose.runtime.mutableStateOf
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.ui.controller.ControladorPartida
import com.diegodiaz.techwizards.ui.controller.ControladorPartidaFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import com.diegodiaz.techwizards.ui.responsive.Responsive
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope


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
    val scope = rememberCoroutineScope()
    val usuarioActual = remember { mutableStateOf<Usuario?>(null) }
    LaunchedEffect(Unit) {
        val existente = usuarioDao.obtenerUsuarioPrincipal()
        usuarioActual.value = existente?.toDomain()
    }

    val usuarioId = (usuarioActual.value?.numero ?: 1L).toString()

    NavHost(
        navController = navController,
        startDestination = "bienvenida",
        modifier = modifier
    ) {
        composable("bienvenida") {
            PantallaBienvenida(
                isDarkTheme = isDarkTheme,
                nombrePredeterminado = usuarioActual.value?.alias,
                onJugar = { nombre ->
                    scope.launch {
                        val existente = usuarioActual.value
                        val usuario = Usuario(
                            numero = existente?.numero ?: 1L,
                            alias = nombre,
                            fechaAltaMs = existente?.fechaAltaMs ?: System.currentTimeMillis(),
                            monedas = existente?.monedas ?: 100,
                            ganoUltimaPartida = existente?.ganoUltimaPartida ?: false,
                            firebaseUid = existente?.firebaseUid
                        )
                        repo.inicializarMonedas(usuario, usuario.monedas)
                        val actualizado = usuarioDao.obtenerUsuarioPrincipal()?.toDomain() ?: usuario
                        usuarioActual.value = actualizado
                        navController.navigate("menu") {
                            popUpTo("bienvenida") { inclusive = true }
                        }
                    }
                }
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
