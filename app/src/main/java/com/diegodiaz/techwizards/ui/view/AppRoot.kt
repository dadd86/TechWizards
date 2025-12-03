package com.diegodiaz.techwizards.ui.view

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.diegodiaz.techwizards.integration.media.MusicPlaybackController
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims
import androidx.lifecycle.viewmodel.compose.viewModel
import com.diegodiaz.techwizards.ui.controller.ControladorAjustes
import com.diegodiaz.techwizards.ui.controller.ControladorAjustesFactory
import com.diegodiaz.techwizards.ui.controller.ControladorAuth
import com.diegodiaz.techwizards.ui.controller.ControladorAuthFactory

import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.core.usecases.ObtenerPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ActualizarPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase

@Composable
fun AppRoot(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val musicController = remember { MusicPlaybackController(context.applicationContext) }

    val settingsRepository = remember { ServiceLocator.settingsRepository }
    val obtenerPreferencias = remember { ObtenerPreferenciasUseCase(settingsRepository) }
    val actualizarPreferencias = remember { ActualizarPreferenciasUseCase(settingsRepository) }
    val observarPreferencias = remember { ObservarPreferenciasUseCase(settingsRepository) }

    val ajustesFactory = remember {
        ControladorAjustesFactory(
            obtenerPreferencias = obtenerPreferencias,
            actualizarPreferencias = actualizarPreferencias,
            observarPreferencias = observarPreferencias
        )
    }

    val ajustesVm: ControladorAjustes = viewModel(factory = ajustesFactory)
    val ajustesState by ajustesVm.ui.collectAsState()

    val authFactory = remember {
        ControladorAuthFactory(
            iniciarSesion = ServiceLocator.iniciarSesionConGoogleUseCase,
            cerrarSesion = ServiceLocator.cerrarSesionUseCase,
            observarUsuario = ServiceLocator.observarUsuarioAutenticadoUseCase
        )
    }
    val authVm: ControladorAuth = viewModel(factory = authFactory)


    LaunchedEffect(ajustesState.settings.musicEnabled, ajustesState.settings.selectedMusicUri) {
        musicController.applySettings(
            enabled = ajustesState.settings.musicEnabled,
            selectedUri = ajustesState.settings.selectedMusicUri
        )
    }

    // Este efecto para la música al cerrar la app
    DisposableEffect(Unit) {
        onDispose { musicController.stop() }
    }

    val navController = rememberNavController()

    Responsive { dims: UiDims ->
        Scaffold { innerPadding ->
            NavGraph(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                dims = dims, // Se lo pasamos al grafo
                modifier = Modifier.padding(innerPadding),
                ajustesVm = ajustesVm,
                authVm = authVm
            )
        }
    }
}