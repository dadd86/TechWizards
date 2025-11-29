package com.diegodiaz.techwizards.ui.view

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.core.os.LocaleListCompat
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.core.usecases.ActualizarPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObtenerPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase
import com.diegodiaz.techwizards.ui.controller.ControladorAjustes
import com.diegodiaz.techwizards.ui.controller.ControladorAjustesFactory
import com.diegodiaz.techwizards.ui.controller.ControladorPartida
import com.diegodiaz.techwizards.ui.controller.ControladorPartidaFactory
import com.diegodiaz.techwizards.ui.responsive.UiDims
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    dims: UiDims,
    modifier: Modifier = Modifier,
    ajustesVm: ControladorAjustes
) {
    val context = LocalContext.current
    val db = remember { BaseDeDatos.get(context) }
    val usuarioDao = db.usuarioDao()
    val monederoDao = db.monederoDao()
    val partidaDao = db.partidaDao()
    val repo = remember { JuegoRepositoryRoom(usuarioDao, monederoDao, partidaDao) }

    val scope = rememberCoroutineScope()

    val settingsRepository = remember { ServiceLocator.settingsRepository }
    val observarPreferencias = remember { ObservarPreferenciasUseCase(settingsRepository) }

    val victoryService = remember { ServiceLocator.victoryCelebrationService }

    val usuarioActual = remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(Unit) {
        val existente = usuarioDao.obtenerUsuarioPrincipal()
        usuarioActual.value = existente?.toDomain()
    }

    val usuarioId = (usuarioActual.value?.numero ?: 1L).toString()

    // --------- Instancia de ControladorPartida compartida ---------
    val partidaFactory = remember {
        ControladorPartidaFactory(
            repo = repo,
            usuarioId = usuarioId,
            observarPreferencias = observarPreferencias,
            victoryService = victoryService,
            registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase
        )
    }
    val controladorPartida: ControladorPartida = viewModel(factory = partidaFactory)
    // ------------------------------------------------------------------------

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
                        val actualizado = usuarioDao
                            .obtenerUsuarioPrincipal()
                            ?.toDomain() ?: usuario
                        usuarioActual.value = actualizado
                        navController.navigate("menu") {
                            popUpTo("bienvenida") { inclusive = true }
                        }
                    }
                },
                dims = dims
            )
        }

        composable("menu") {
            val usuario = usuarioActual.value

            // Crea el ViewModel de partida aquí para acceder desde el menú
            /*val factory = ControladorPartidaFactory(
                repo = repo,
                usuarioId = usuarioId,
                observarPreferencias = observarPreferencias,
                victoryService = victoryService,
                registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase
            )
            val controladorPartida: ControladorPartida = viewModel(factory = factory)*/

            PantallaMenu(
                isDarkTheme = isDarkTheme,
                onJugar = { navController.navigate("partida") },
                onHistorial = { navController.navigate("historial") },
                onAjustes = { navController.navigate("ajustes") },
                onAyuda = { navController.navigate("ayuda") },
                dims = dims,
                controladorPartida = controladorPartida,
                usuario = usuario
            )
        }


        composable("partida") {
            /*val factory = ControladorPartidaFactory(
                repo = repo,
                usuarioId = usuarioId,
                observarPreferencias = observarPreferencias,
                victoryService = victoryService,
                registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase
            )
            val controladorPartida: ControladorPartida = viewModel(factory = factory)*/
            val uiState = controladorPartida.ui.collectAsState().value

            PantallaPartida(
                isDarkTheme = isDarkTheme,
                uiState = uiState,
                eventos = controladorPartida.eventos,
                onVolverAlMenu = { navController.navigate("menu") },
                onElegirNumero = { num -> controladorPartida.elegirNumero(num) },
                onProgramarCelebracion = { payload ->
                    controladorPartida.programarCelebracion(payload)
                },
                dims = dims
            )
        }

        composable("historial") {
            /*val viewModel: ControladorPartida = viewModel(
                factory = ControladorPartidaFactory(
                    repo = repo,
                    usuarioId = usuarioId,
                    observarPreferencias = observarPreferencias,
                    victoryService = victoryService,
                    registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase
                )
            )*/
            val historial by controladorPartida.historial.collectAsState()

            PantallaHistorial(
                isDarkTheme = isDarkTheme,
                historial = historial,
                onVolverAlMenu = { navController.navigate("menu") },
                dims = dims
            )
        }

        composable("ajustes") {
            val ajustesState by ajustesVm.ui.collectAsState()

            PantallaAjustes(
                isDarkTheme = isDarkTheme,
                ajustesState = ajustesState,
                onToggleTheme = { enabled ->
                    ajustesVm.actualizarTemaOscuro(enabled)
                    onToggleTheme(enabled)
                },
                onToggleMusic = ajustesVm::actualizarMusica,
                onToggleSfx = ajustesVm::actualizarSfx,
                onToggleAnimations = ajustesVm::actualizarAnimaciones,
                onToggleNotifications = ajustesVm::actualizarNotificaciones,
                onElegirPista = ajustesVm::seleccionarPista,
                onSeleccionIdioma = { tag ->
                    ajustesVm.actualizarIdioma(tag)
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(tag)
                    )
                },
                onVolverAlMenu = { navController.navigate("menu") },
                dims = dims
            )
        }

        composable("ayuda") {
            PantallaAyuda(
                dims = dims,
                onVolverAlMenu = { navController.navigate("menu") }
            )
        }

        composable("chat") {
            PantallaChat(dims = dims)
        }

        composable("eventos") {
            PantallaEventos(dims = dims)
        }

        composable("lobby") {
            PantallaLobby(dims = dims)
        }

        composable("match") {
            PantallaMatch(dims = dims)
        }
    }
}
