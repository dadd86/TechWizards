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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.core.os.LocaleListCompat
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.ui.controller.AuthState
import com.diegodiaz.techwizards.ui.controller.ControladorAjustes
import com.diegodiaz.techwizards.ui.controller.ControladorAuth
import com.diegodiaz.techwizards.ui.controller.ControladorMatch
import com.diegodiaz.techwizards.ui.controller.ControladorMatchOnline
import com.diegodiaz.techwizards.ui.controller.ControladorLobby
import com.diegodiaz.techwizards.ui.controller.ControladorPartida
import com.diegodiaz.techwizards.ui.controller.ControladorPartidaFactory
import com.diegodiaz.techwizards.ui.controller.ControladorPremioAdmin
import com.diegodiaz.techwizards.ui.controller.ControladorRanking
import com.diegodiaz.techwizards.ui.controller.SimpleVmFactory
import com.diegodiaz.techwizards.ui.responsive.UiDims
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.launch
import kotlin.random.Random

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
    val fallbackUsuarioNumero = remember { mutableStateOf<Long?>(null) }
    val authState by authVm.ui.collectAsState()

    LaunchedEffect(Unit) {
        val existente = usuarioDao.obtenerUsuarioPrincipal()
        usuarioActual.value = existente?.toDomain()
        if (existente == null) {
            fallbackUsuarioNumero.value = generarNumeroUsuario()
        }
    }

    val usuarioNumeroActual = usuarioActual.value?.numero
        ?: fallbackUsuarioNumero.value
        ?: generarNumeroUsuario().also { fallbackUsuarioNumero.value = it }
    val usuarioId = usuarioNumeroActual.toString()

    // --------- Instancia de ControladorPartida compartida ---------
    val partidaFactory = remember {
        ControladorPartidaFactory(
            repo = repo,
            usuarioId = usuarioId,
            scoreRepository = scoreRepository,
            observarPreferencias = observarPreferencias,
            victoryService = victoryService,
            sessionManager = sessionManager,
            registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase,
            resolverTiradaUseCase = ServiceLocator.resolverTiradaUseCase
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
                            // ✅ ScoreRepository usa autenticarAlias(alias)
                            scoreRepository.autenticarAlias(nombre)
                        }.getOrElse { error ->
                            DecentralizedLogger.e("NavGraph", "Fallo en login remoto", error)
                            UserSession(token = "local-$nombre", alias = nombre)
                        }

                        sessionManager.setSession(session)

                        val existente = usuarioActual.value
                        val usuario = Usuario(
                            numero = existente?.numero ?: usuarioNumeroActual,
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
                    // ✅ Ajusta al nombre real del método en ControladorAuth
                    authVm.iniciarSesion(idToken)

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
                onLobby = { navController.navigate("lobby") },
                onChat = { navController.navigate("chat") },
                onEventos = { navController.navigate("eventos") },
                onMatch = {
                    val matchId = "match-${System.currentTimeMillis()}"
                    val lobbyId = "lobby-${usuario?.numero ?: usuarioNumeroActual}"
                    navController.navigate("match/$matchId?lobbyId=$lobbyId")
                },
                onPremioAdmin = { navController.navigate("premio-admin") },
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



        composable("premio-admin") {
            val premioVm: ControladorPremioAdmin = viewModel(
                factory = SimpleVmFactory {
                    ControladorPremioAdmin(
                        scoreRepository = scoreRepository,
                        actualizarPremioComunUseCase = ServiceLocator.actualizarPremioComunUseCase
                    )
                }
            )

            PantallaPremioAdmin(
                dims = dims,
                controlador = premioVm,
                onVolver = { navController.navigate("menu") }
            )
        }


        composable("ayuda") {
            PantallaAyuda(
                dims = dims,
                onVolverAlMenu = { navController.navigate("menu") }
            )
        }

        composable("chat") {
            PantallaChat(
                dims = dims,
                onVolver = { navController.navigate("menu") }
            )
        }

        composable("eventos") {
            PantallaEventos(
                dims = dims,
                onVolver = { navController.navigate("menu") }
            )
        }

        composable("lobby") {
            val lobbyVm: ControladorLobby = viewModel(
                factory = SimpleVmFactory {
                    ControladorLobby(
                        ServiceLocator.lobbyRealtimeDataSource
                    )
                }
            )
            val lobbyState by lobbyVm.ui.collectAsState()

            val matchVm: ControladorMatchOnline = viewModel(
                key = "matchOnline-lobby",
                factory = SimpleVmFactory {
                    ControladorMatchOnline(
                        ServiceLocator.matchRepository,
                        ServiceLocator.scoreRepository,
                        ServiceLocator.lobbyRepository
                    )
                }
            )

            val matchState by matchVm.ui.collectAsState()
            val usuarioNumero = usuarioActual.value?.numero ?: usuarioNumeroActual
            val normalizarMatchId: (String) -> String = { codigo ->
                if (codigo.startsWith("match-")) codigo else "match-$codigo"
            }

            PantallaLobby(
                dims = dims,
                lobbyState = lobbyState,
                matchState = matchState,
                onVolver = { navController.navigate("menu") },
                onCrearLobby = {
                    val codigo = lobbyState.codigoIngreso.trim().ifBlank { null }
                    val lobby = lobbyVm.crearLobby(
                        nombre = "Lobby $usuarioNumero",
                        modo = "duelo",
                        creadorNumero = usuarioNumero,
                        codigo = codigo
                    )
                    matchVm.crearMatchDesdeLobby(lobby = lobby, creadorNumero = usuarioNumero)
                },
                onActualizarCodigo = lobbyVm::actualizarCodigoIngreso,
                onUnirsePorCodigo = {
                    val codigo = lobbyVm.normalizarCodigoIngreso(lobbyState.codigoIngreso)
                    if (!codigo.isNullOrEmpty()) {
                        lobbyVm.seleccionar(codigo)
                        lobbyVm.unirseLobbyRemoto(codigo, usuarioNumero)
                        val matchId = normalizarMatchId(codigo)
                        matchVm.unirseAMatchExistente(matchId = matchId, lobbyId = codigo, usuarioId = usuarioNumero)
                    }
                },
                onEntrarLobby = { lobbyId ->
                    lobbyVm.seleccionar(lobbyId)
                    lobbyVm.unirseLobbyRemoto(lobbyId, usuarioNumero)
                    matchVm.unirseAMatchExistente(
                        matchId = normalizarMatchId(lobbyId),
                        lobbyId = lobbyId,
                        usuarioId = usuarioNumero
                    )
                },
                onSeleccionCara = matchVm::seleccionarCara,
                onConfirmarApuesta = { matchVm.confirmarApuesta(usuarioNumero) },
                onLanzarDado = { matchVm.lanzarDado(usuarioNumero) },
                onBuscarRival = { matchVm.buscarRival(usuarioNumero) }
            )
        }

        composable(
            route = "match/{matchId}?lobbyId={lobbyId}",
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType },
                navArgument("lobbyId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->

            val matchId = entry.arguments?.getString("matchId")?.trim().orEmpty()
            val lobbyId = entry.arguments?.getString("lobbyId")?.trim()?.takeIf { it.isNotEmpty() }

            // Si matchId no viene, evita crashear y vuelve al menú
            if (matchId.isEmpty()) {
                LaunchedEffect(Unit) {
                    navController.navigate("menu") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
                return@composable
            }

            // ✅ Key por matchId: si cambia el match, instancia nueva limpia
            val matchVm: ControladorMatchOnline = viewModel(
                key = "matchOnline-$matchId",
                factory = SimpleVmFactory {
                    ControladorMatchOnline(
                        ServiceLocator.matchRepository,
                        ServiceLocator.scoreRepository,
                        ServiceLocator.lobbyRepository
                    )
                }
            )

            // ✅ Iniciar solo cuando cambie el matchId/lobbyId/usuarioId
            val usuarioId = usuarioActual.value?.numero ?: usuarioNumeroActual
            LaunchedEffect(matchId, lobbyId, usuarioId) {
                matchVm.iniciar(matchId = matchId, lobbyId = lobbyId, usuarioId = usuarioId)
            }

            val matchState by matchVm.ui.collectAsState()

            PantallaMatch(
                dims = dims,
                uiState = matchState,
                onSeleccionCara = matchVm::seleccionarCara,
                onConfirmarApuesta = { usuarioId?.let(matchVm::confirmarApuesta) },
                onLanzarDado = { usuarioId?.let(matchVm::lanzarDado) },
                onBuscarRival = { usuarioId?.let(matchVm::buscarRival) },
                onVolver = {
                    navController.navigate("menu") {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo("menu") { inclusive = false }
                    }
                }
            )
        }
    }
}
private fun generarNumeroUsuario(): Long {
    val base = System.currentTimeMillis() % 1_000_000_000L
    val random = Random.nextLong(1_000L, 1_000_000L)
    return (base * 1_000_000L) + random
}