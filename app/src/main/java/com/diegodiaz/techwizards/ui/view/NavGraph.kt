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
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.core.usecases.ActualizarPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObtenerPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.ui.controller.ControladorAjustes
import com.diegodiaz.techwizards.ui.controller.ControladorAjustesFactory
import com.diegodiaz.techwizards.ui.controller.ControladorPartida
import com.diegodiaz.techwizards.ui.controller.ControladorPartidaFactory
import com.diegodiaz.techwizards.ui.responsive.UiDims
import kotlinx.coroutines.launch
import android.app.Activity

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    dims: UiDims,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = remember { BaseDeDatos.get(context) }
    val usuarioDao = db.usuarioDao()
    val monederoDao = db.monederoDao()
    val partidaDao = db.partidaDao()
    val repo = remember { JuegoRepositoryRoom(usuarioDao, monederoDao, partidaDao) }
    val scope = rememberCoroutineScope()

    // ---- Settings / preferencias ----
    val settingsRepository = remember { ServiceLocator.settingsRepository }
    val obtenerPreferencias = remember { ObtenerPreferenciasUseCase(settingsRepository) }
    val actualizarPreferencias = remember { ActualizarPreferenciasUseCase(settingsRepository) }
    val observarPreferencias = remember { ObservarPreferenciasUseCase(settingsRepository) }

    val victoryService = remember { ServiceLocator.victoryCelebrationService }

    val ajustesFactory = remember {
        ControladorAjustesFactory(
            obtenerPreferencias = obtenerPreferencias,
            actualizarPreferencias = actualizarPreferencias,
            observarPreferencias = observarPreferencias
        )
    }

    // ViewModel de ajustes a nivel de NavGraph
    val ajustesVmGlobal: ControladorAjustes = viewModel(factory = ajustesFactory)
    val ajustesStateGlobal by ajustesVmGlobal.ui.collectAsState()

    // Cada vez que cambie el idioma guardado => aplica locales de la app
    LaunchedEffect(ajustesStateGlobal.settings.selectedLanguageTag) {
        val tag = ajustesStateGlobal.settings.selectedLanguageTag
        if (tag.isNotBlank()) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(tag)
            )
        }
    }

    // ---- Usuario actual ----
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
                        val actualizado =
                            usuarioDao.obtenerUsuarioPrincipal()?.toDomain() ?: usuario
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
            PantallaMenu(
                isDarkTheme = isDarkTheme,
                onJugar = { navController.navigate("partida") },
                onHistorial = { navController.navigate("historial") },
                onAjustes = { navController.navigate("ajustes") },
                onAyuda = { navController.navigate("ayuda") },
                onLobby = { navController.navigate("lobby") },
                onChat = { navController.navigate("chat") },
                onEventos = { navController.navigate("eventos") },
                onMatch = { navController.navigate("match") },
                dims = dims
            )
        }

        composable("partida") {
            val factory = ControladorPartidaFactory(
                repo = repo,
                usuarioId = usuarioId,
                observarPreferencias = observarPreferencias,
                victoryService = victoryService,
                registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase
            )
            val controladorPartida: ControladorPartida = viewModel(factory = factory)
            val uiState = controladorPartida.ui.collectAsState().value

            PantallaPartida(
                isDarkTheme = isDarkTheme,
                uiState = uiState,
                eventos = controladorPartida.eventos,
                onVolverAlMenu = { navController.navigate("menu") },
                onElegirNumero = { num -> controladorPartida.elegirNumero(num) },
                dims = dims
            )
        }

        composable("historial") {
            val viewModel: ControladorPartida = viewModel(
                factory = ControladorPartidaFactory(
                    repo = repo,
                    usuarioId = usuarioId,
                    observarPreferencias = observarPreferencias,
                    victoryService = victoryService,
                    registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase
                )
            )
            val historial by viewModel.historial.collectAsState()

            PantallaHistorial(
                isDarkTheme = isDarkTheme,
                historial = historial,
                onVolverAlMenu = { navController.navigate("menu") },
                dims = dims
            )
        }

        composable("ajustes") {
            val ajustesVm: ControladorAjustes = viewModel(factory = ajustesFactory)
            val ajustesState by ajustesVm.ui.collectAsState()
            val activity = LocalContext.current as? Activity

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
                    // 1) guardar preferencia
                    ajustesVm.actualizarIdioma(tag)
                    // 2) aplicar locales
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(tag)
                    )
                    // 3) recrear activity para que cambien todos los stringResource
                    activity?.recreate()
                },
                onVolverAlMenu = { navController.navigate("menu") },
                dims = dims
            )
        }


                composable("ayuda") {
                    PantallaAyuda(dims = dims)
                }
                /*
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
                }*/
}
}
