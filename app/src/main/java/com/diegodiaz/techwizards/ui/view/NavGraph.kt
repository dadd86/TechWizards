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
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.view.PantallaAjustes
import com.diegodiaz.techwizards.ui.view.PantallaAyuda
import com.diegodiaz.techwizards.ui.view.PantallaBienvenida
import com.diegodiaz.techwizards.ui.view.PantallaHistorial
import com.diegodiaz.techwizards.ui.view.PantallaMenu
import kotlinx.coroutines.launch

/**
 * Define la jerarquía de navegación y orquesta la inicialización del jugador en Room.
 *
 * Usa el repositorio respaldado por Room para garantizar que la escritura del alias se realice
 * mediante las APIs reactivas de RxJava antes de navegar hacia el menú principal.
 *
 * @param navController Controlador de navegación de Compose.
 * @param isDarkTheme Indica si el tema actual es oscuro.
 * @param onToggleTheme Acción para alternar el tema desde ajustes.
 * @param modifier Modificador opcional de la raíz del NavHost.
 * @return Unit porque solo construye composición de navegación.
 * @throws IllegalStateException Solo propaga errores inesperados de Room o repositorio.
 * @security
 * - Obtiene y persiste únicamente el alias del jugador y su saldo local.
 */
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
    val settingsRepository = remember { ServiceLocator.settingsRepository }
    val obtenerPreferencias = remember { ObtenerPreferenciasUseCase(settingsRepository) }
    val actualizarPreferencias = remember { ActualizarPreferenciasUseCase(settingsRepository) }
    val observarPreferencias = remember { ObservarPreferenciasUseCase(settingsRepository) }
    val ajustesFactory = remember {
        ControladorAjustesFactory(obtenerPreferencias, actualizarPreferencias, observarPreferencias)
    }
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
                onAjustes = { navController.navigate("ajustes") },
                onAyuda = { navController.navigate("ayuda") }
            )
        }
        composable("partida") {
            val factory = ControladorPartidaFactory(repo, usuarioId, observarPreferencias)
            val controladorPartida: ControladorPartida = viewModel(factory = factory)
            val uiState = controladorPartida.ui.collectAsState().value
            PantallaPartida(
                isDarkTheme = isDarkTheme,
                uiState = uiState,
                eventos = controladorPartida.eventos,
                onVolverAlMenu = { navController.navigate("menu") },
                onElegirNumero = { num -> controladorPartida.elegirNumero(num) }
            )
        }
        composable("historial") {
            val viewModel: ControladorPartida = viewModel(factory = ControladorPartidaFactory(repo, usuarioId, observarPreferencias))
            val historial by viewModel.historial.collectAsState()
            PantallaHistorial(
                isDarkTheme = isDarkTheme,
                historial = historial,
                onVolverAlMenu = {  navController.navigate("menu") }
            )
        }
        composable("ajustes") {
            val ajustesVm: ControladorAjustes = viewModel(factory = ajustesFactory)
            val ajustesState by ajustesVm.ui.collectAsState()
            PantallaAjustes(
                isDarkTheme = isDarkTheme,
                state = ajustesState,
                eventos = ajustesVm.eventos,
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
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                },
                onVolverAlMenu = { navController.navigate("menu") }
            )
        }
        composable("ayuda") {
            PantallaAyuda()
        }
    }
}
