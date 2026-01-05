package com.diegodiaz.techwizards.ui.view

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
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
import com.diegodiaz.techwizards.ui.controller.ControladorHistorial
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
    val appContext = context.applicationContext
    val db = remember(appContext) { BaseDeDatos.get(appContext) }
    val usuarioDao = db.usuarioDao()
    val monederoDao = db.monederoDao()
    val partidaDao = db.partidaDao()
    val repo = remember(db) { JuegoRepositoryRoom(usuarioDao, monederoDao, partidaDao) }

    val scope = rememberCoroutineScope()

    val settingsRepository = remember { ServiceLocator.settingsRepository }
    val scoreRepository = remember { ServiceLocator.scoreRepository }
    val sessionManager = remember { ServiceLocator.sessionManager }
    val observarPreferencias = remember { ObservarPreferenciasUseCase(settingsRepository) }
    val userIdLocalDataSource = remember { ServiceLocator.userIdLocalDataSource }

    val victoryService = remember { ServiceLocator.victoryCelebrationService }

    val usuarioActual = remember { mutableStateOf<Usuario?>(null) }
    val fallbackUsuarioNumero = remember { mutableStateOf<Long?>(null) }

    val isUsuarioLoaded = remember { mutableStateOf(false) }
    val authState by authVm.ui.collectAsState()

    LaunchedEffect(Unit) {
        val usuarioPersistidoId = userIdLocalDataSource.obtenerUsuarioId()
        val usuarioPersistidoNumero = usuarioPersistidoId?.toLongOrNull()
        val usuarioPersistido = usuarioPersistidoNumero?.let { usuarioDao.getByNumero(it) }

        val usuarioRoom = usuarioPersistido ?: usuarioDao.obtenerUsuarioPrincipal()
        usuarioActual.value = usuarioRoom?.toDomain()

        if (usuarioActual.value != null) {
            userIdLocalDataSource.guardarUsuarioId(usuarioActual.value!!.numero.toString())

            DecentralizedLogger.d(
                "NavGraph",
                "Usuario persistido cargado id=${redactId(usuarioActual.value?.numero.toString())}"
            )
        } else {
            fallbackUsuarioNumero.value = usuarioPersistidoNumero ?: generarNumeroUsuario()
            DecentralizedLogger.d(
                "NavGraph",
                "Fallback usuario generado id=${redactId(fallbackUsuarioNumero.value?.toString())}"
            )
        }
        isUsuarioLoaded.value = true
    }

    val usuarioNumeroActual = if (isUsuarioLoaded.value) {
        usuarioActual.value?.numero
            ?: fallbackUsuarioNumero.value
            ?: generarNumeroUsuario().also { fallbackUsuarioNumero.value = it }
    } else {
        null
    }
    val usuarioId = usuarioNumeroActual?.toString()
    val usuarioIdPersistido = usuarioActual.value?.numero?.toString()

    if (!isUsuarioLoaded.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val usuarioNumeroFinal = requireNotNull(usuarioNumeroActual) {
        "Usuario no inicializado."
    }
    val usuarioIdFinal = requireNotNull(usuarioId) { "UsuarioId no inicializado." }

    // --------- Instancia de ControladorPartida compartida ---------
    val partidaFactory = remember(usuarioIdFinal) {
        ControladorPartidaFactory(
            repo = repo,
            usuarioId = usuarioIdFinal,
            scoreRepository = scoreRepository,
            observarPreferencias = observarPreferencias,
            victoryService = victoryService,
            sessionManager = sessionManager,
            registrarHistorialRemotoUseCase = ServiceLocator.registrarHistorialRemotoUseCase,
            firebaseUidProvider = {
                authState.usuario?.uid ?: usuarioActual.value?.firebaseUid
            },
            registrarUbicacionVictoriaUseCase = ServiceLocator.registrarUbicacionVictoriaUseCase,
            resolverTiradaUseCase = ServiceLocator.resolverTiradaUseCase
        )
    }
    val controladorPartida: ControladorPartida = viewModel(
        key = "controladorPartida-$usuarioIdFinal",
        factory = partidaFactory
    )
    // ------------------------------------------------------------------------

    LaunchedEffect(usuarioIdPersistido) {
        if (usuarioIdPersistido != null) {
            DecentralizedLogger.d(
                "NavGraph",
                "ControladorPartida listo usuarioId=${redactId(usuarioIdPersistido)}"
            )
        }
    }
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
                        val existenteEnRoom = usuarioDao.obtenerUsuarioPrincipal()?.toDomain()
                        if (existenteEnRoom == null && authState.cargando) {
                            return@launch
                        }

                        val session = runCatching {
                            // ✅ ScoreRepository usa autenticarAlias(alias)
                            if (!isSesionFirebaseValida(sessionManager.session.value)) {
                                throw IllegalStateException("Firebase token no disponible")
                            }
                            scoreRepository.autenticarAlias(nombre)
                        }.getOrElse { error ->
                            DecentralizedLogger.e("NavGraph", "Fallo en login remoto", error)
                            null
                        }

                        if (session != null) {
                            sessionManager.setSession(session)
                        } else {
                            sessionManager.clearSession()
                            Toast.makeText(
                                context,
                                "Modo offline: ranking y premios no disponibles",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        val usuario = if (existenteEnRoom != null) {
                            existenteEnRoom.copy(
                                firebaseUid = authState.usuario?.uid ?: existenteEnRoom.firebaseUid
                            )
                        } else {
                            Usuario(
                                numero = usuarioNumeroFinal,
                                alias = nombre,
                                fechaAltaMs = System.currentTimeMillis(),
                                monedas = 100,
                                ganoUltimaPartida = false,
                                firebaseUid = authState.usuario?.uid
                            )
                        }

                        userIdLocalDataSource.guardarUsuarioId(usuario.numero.toString())
                        repo.inicializarMonedas(usuario, usuario.monedas)

                        val actualizado = usuarioDao.obtenerUsuarioPrincipal()?.toDomain() ?: usuario

                        usuarioActual.value = actualizado
                        fallbackUsuarioNumero.value = null
                        DecentralizedLogger.d(
                            "NavGraph",
                            "Usuario persistido actualizado id=${redactId(actualizado.numero.toString())}"
                        )

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
                onRanking = {
                    navController.navigate("ranking")
                },
                onAjustes = { navController.navigate("ajustes") },
                onAyuda = { navController.navigate("ayuda") },
                //onLobby = { navController.navigate("lobby") },
                //onChat = { navController.navigate("chat") },
                //onEventos = { navController.navigate("eventos") },
                /*onMatch = {
                    val matchId = "match-${System.currentTimeMillis()}"
                    val lobbyId = "lobby-${usuario?.numero ?: usuarioNumeroFinal}"
                    navController.navigate("match/$matchId?lobbyId=$lobbyId")
                },*/
                //onPremioAdmin = { navController.navigate("premio-admin") },
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
            val historialVm: ControladorHistorial = viewModel(
                factory = SimpleVmFactory {
                    ControladorHistorial(
                        observarHistorialRemotoUseCase = ServiceLocator.observarHistorialRemotoUseCase,
                        firebaseUidProvider = {
                            authState.usuario?.uid ?: usuarioActual.value?.firebaseUid
                        }
                    )
                }
            )
            val historial by historialVm.historial.collectAsState()

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
                val selectedTag = ajustesState.settings.selectedLanguageTag
                val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                if (currentTag != selectedTag) {
                    val localeList = LocaleListCompat.forLanguageTags(selectedTag)
                    AppCompatDelegate.setApplicationLocales(localeList)
                }
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
                    val currentTag = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                    if (currentTag != tag) {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(tag)
                        )
                    }
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
                        ServiceLocator.lobbyRepository,
                        ServiceLocator.lobbyRealtimeDataSource
                    )
                }
            )

            val matchState by matchVm.ui.collectAsState()
            val usuarioNumero = usuarioActual.value?.numero ?: usuarioNumeroFinal
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
                onBuscarRival = {
                    matchVm.buscarRival(
                        usuarioNumero,
                        lobbyState.lobbyActual?.id ?: matchState.lobbyId
                    )
                }
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
                        ServiceLocator.lobbyRepository,
                        ServiceLocator.lobbyRealtimeDataSource
                    )
                }
            )

            // ✅ Iniciar solo cuando cambie el matchId/lobbyId/usuarioId
            val usuarioId = usuarioActual.value?.numero ?: usuarioNumeroFinal
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
                onBuscarRival = { usuarioId?.let { matchVm.buscarRival(it, matchState.lobbyId) } },
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

private fun redactId(id: String?): String =
    id?.takeLast(2)?.padStart(4, '*') ?: "***"

private fun isSesionFirebaseValida(session: UserSession?): Boolean {
    val token = session?.token?.trim().orEmpty()
    if (token.isEmpty()) return false
    if (token.startsWith("local-")) return false
    val backendToken = session?.backendToken
    if (!backendToken.isNullOrBlank() && backendToken == token) return false
    return true
}