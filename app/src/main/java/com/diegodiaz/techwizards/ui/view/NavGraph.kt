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
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.ui.controller.AuthState
import com.diegodiaz.techwizards.ui.controller.ControladorAjustes
import com.diegodiaz.techwizards.ui.controller.ControladorAjustesFactory
import com.diegodiaz.techwizards.ui.controller.ControladorAuth
import com.diegodiaz.techwizards.ui.controller.ControladorPartida
import com.diegodiaz.techwizards.ui.controller.ControladorPartidaFactory
import com.diegodiaz.techwizards.ui.controller.ControladorRanking
import com.diegodiaz.techwizards.ui.controller.SimpleVmFactory
import com.diegodiaz.techwizards.ui.responsive.UiDims
import kotlinx.coroutines.launch
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger

@Composable
fun NavGraph(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    dims: UiDims,
    modifier: Modifier = Modifier,
    ajustesVm: ControladorAjustes,
    authVm: ControladorAuth
) {
    val context = LocalContext.current
    val db = remember { BaseDeDatos.get(context) }
    val usuarioDao = db.usuarioDao()
    val monederoDao = db.monederoDao()
    val partidaDao = db.partidaDao()
    val repo = remember { JuegoRepositoryRoom(usuarioDao, monederoDao, partidaDao) }

    val scope = rememberCoroutineScope()

    val settingsRepository = remember { ServiceLocator.settingsRepository }
    val scoreRepository = remember { ServiceLocator.scoreRepository }
    val sessionManager = remember { ServiceLocator.sessionManager }
    val observarPreferencias = remember { ObservarPreferenciasUseCase(settingsRepository) }

    val victoryService = remember { ServiceLocator.victoryCelebrationService }

    val usuarioActual = remember { mutableStateOf<Usuario?>(null) }
    val authState by authVm.ui.collectAsState()

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
            scoreRepository = scoreRepository,
            observarPreferencias = observarPreferencias,
            victoryService = victoryService,
            sessionManager = sessionManager,
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
                nombrePredeterminado = usuarioActual.value?.alias ?: authState.usuario?.displayName,
                onJugar = { nombre ->
                    scope.launch {
                        val session = runCatching {
                            scoreRepository.iniciarSesion(nombre)
                        }.getOrElse { error ->
                            DecentralizedLogger.e("NavGraph", "Fallo en login remoto", error)
                            UserSession(token = "local-$nombre", alias = nombre)
                        }
                        sessionManager.setSession(session)

                        val existente = usuarioActual.value
                        val usuario = Usuario(
                            numero = existente?.numero ?: 1L,
                            alias = nombre,
                            fechaAltaMs = existente?.fechaAltaMs ?: System.currentTimeMillis(),
                            monedas = existente?.monedas ?: 100,
                            ganoUltimaPartida = existente?.ganoUltimaPartida ?: false,
                            firebaseUid = authState.usuario?.uid ?: existente?.firebaseUid
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
                onGoogleSignIn = { idToken ->
                    authVm.iniciarSesionConGoogle(idToken)
                },
                onLogout = {
                    authVm.cerrarSesion()
                },
                authState = AuthState(
                    usuario = authState.usuario,
                    cargando = authState.cargando,
                    error = authState.error
                ),
                dims = dims
            )
        }

        composable("menu") {
            val usuario = usuarioActual.value

            PantallaMenu(
                isDarkTheme = isDarkTheme,
                onJugar = { navController.navigate("partida") },
                onHistorial = { navController.navigate("historial") },
                onRanking = { navController.navigate("ranking") },
                onAjustes = { navController.navigate("ajustes") },
                onAyuda = { navController.navigate("ayuda") },
                dims = dims,
                controladorPartida = controladorPartida,
                usuario = usuario
            )
        }


        composable("partida") {
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
            val historial by controladorPartida.historial.collectAsState()

            PantallaHistorial(
                isDarkTheme = isDarkTheme,
                historial = historial,
                onVolverAlMenu = { navController.navigate("menu") },
                dims = dims
            )
        }
        composable("ranking") {
            val rankingVm: ControladorRanking = viewModel(
                factory = SimpleVmFactory {
                    ControladorRanking(
                        scoreRepository = scoreRepository,
                        sessionManager = sessionManager
                    )
                }
            )
            PantallaRanking(
                dims = dims,
                controlador = rankingVm,
                onVolver = { navController.navigate("menu") }
            )
        }

        composable("ajustes") {
            val ajustesState by ajustesVm.ui.collectAsState()
            LaunchedEffect(ajustesState.settings.selectedLanguageTag) {
                val localeList = LocaleListCompat.forLanguageTags(ajustesState.settings.selectedLanguageTag)
                AppCompatDelegate.setApplicationLocales(localeList)
            }


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
