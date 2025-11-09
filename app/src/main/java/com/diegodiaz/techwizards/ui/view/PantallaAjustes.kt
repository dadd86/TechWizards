package com.diegodiaz.techwizards.ui.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.integration.media.MusicPlaybackController
import com.diegodiaz.techwizards.ui.controller.AjustesUiEvent
import com.diegodiaz.techwizards.ui.controller.AjustesUiState
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


/**
 * Pantalla de ajustes que controla las preferencias de audio, animaciones y notificaciones.
 *
 * @param isDarkTheme Indicador global del tema actual.
 * @param state Estado observable proveniente del ViewModel.
 * @param eventos Flujo de eventos de un solo uso para controlar multimedia.
 * @param onToggleTheme Callback para alternar el tema.
 * @param onToggleMusic Callback para activar/desactivar música.
 * @param onToggleSfx Callback para efectos de sonido.
 * @param onToggleAnimations Callback para animaciones de fichas.
 * @param onToggleNotifications Callback para notificaciones.
 * @param onElegirPista Callback para seleccionar pista personalizada.
 * @param onSeleccionIdioma Callback para cambiar el locale.
 * @param onVolverAlMenu Callback de navegación al menú.
 * @return `Unit` al componer.
 * @throws IllegalStateException No se lanza; cualquier excepción Compose se propaga automáticamente.
 * @security No muestra datos sensibles; sólo flags y acciones de configuración.
 */
@Composable
fun PantallaAjustes(
    isDarkTheme: Boolean,
    state: AjustesUiState,
    eventos: SharedFlow<AjustesUiEvent>,
    onToggleTheme: (Boolean) -> Unit,
    onToggleMusic: (Boolean) -> Unit,
    onToggleSfx: (Boolean) -> Unit,
    onToggleAnimations: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onElegirPista: (Uri?) -> Unit,
    onSeleccionIdioma: (String) -> Unit,
    onVolverAlMenu: () -> Unit,
) = Responsive { dims ->
    val context = LocalContext.current
    val musicController = remember { MusicPlaybackController(context.applicationContext) }
    DisposableEffect(Unit) {
        musicController.bind()
        onDispose { musicController.unbind() }
    }

    LaunchedEffect(eventos) {
        eventos.collectLatest { evento ->
            when (evento) {
                AjustesUiEvent.DetenerMusica -> musicController.stop()
                is AjustesUiEvent.ReproducirMusica -> {
                    val uri = evento.uri?.let(Uri::parse)
                    if (uri != null) {
                        musicController.playCustom(uri)
                    } else {
                        musicController.playOfficial()
                    }
                }
            }
        }
    }

    val abrirPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        onElegirPista(uri)
    }

    val fondo = if (!isDarkTheme) Color(0xFFB5E2F8) else MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = fondo,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dims.spaceSm),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onVolverAlMenu,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(dims.buttonHeightSm)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_back_to_menu),
                        color = Color(0xFF3B71B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = dims.bodySp
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = dims.spaceMd)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            if (state.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
                    AjustesSeccion(stringResource(id = R.string.settings_sound_section), dims) {
                        val settings = state.settings
                        AjusteSwitch(
                            label = stringResource(id = R.string.settings_music_toggle),
                            checked = settings?.musicEnabled ?: false,
                            onCheckedChange = onToggleMusic,
                        )
                        AjusteSwitch(
                            label = stringResource(id = R.string.settings_sfx_toggle),
                            checked = settings?.sfxEnabled ?: false,
                            onCheckedChange = onToggleSfx,
                        )
                        Button(
                            onClick = { abrirPicker.launch(arrayOf("audio/*")) },
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ) {
                            Text(text = stringResource(id = R.string.settings_pick_track))
                        }
                    }


                    AjustesSeccion(stringResource(id = R.string.settings_visual_section), dims) {
                        AjusteSwitch(
                            label = stringResource(id = R.string.settings_dark_mode_toggle),
                            checked = state.settings?.darkThemeEnabled ?: isDarkTheme,
                            onCheckedChange = onToggleTheme,
                        )
                        AjusteSwitch(
                            label = stringResource(id = R.string.settings_animations_toggle),
                            checked = state.settings?.animationsEnabled ?: true,
                            onCheckedChange = onToggleAnimations,
                        )
                    }


                    AjustesSeccion(stringResource(id = R.string.settings_notifications_section), dims) {
                        AjusteSwitch(
                            label = stringResource(id = R.string.settings_notifications_toggle),
                            checked = state.settings?.notificationsEnabled ?: true,
                            onCheckedChange = onToggleNotifications,
                        )
                    }

                    AjustesSeccion(stringResource(id = R.string.settings_language_section), dims) {
                        IdiomaSelector(
                            seleccionado = state.settings?.selectedLanguageTag ?: "es-ES",
                            onSeleccion = onSeleccionIdioma,
                        )
                    }

                    Spacer(Modifier.height(dims.spaceLg))
                }
            }
        }

        @Composable
        private fun AjustesSeccion(
            titulo: String,
            dims: UiDims,
            content: @Composable Column.() -> Unit
        ) {
            Text(
                text = titulo,
                fontSize = dims.titleSp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs), content = content)
        }

        @Composable
        private fun AjusteSwitch(
            label: String,
            checked: Boolean,
            onCheckedChange: (Boolean) -> Unit = {},
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }

        @Composable
        private fun IdiomaSelector(seleccionado: String, onSeleccion: (String) -> Unit) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LanguageChip(text = "ES", seleccionado = seleccionado == "es-ES") { onSeleccion("es-ES") }
                LanguageChip(text = "EN", seleccionado = seleccionado == "en-US") { onSeleccion("en-US") }
            }
        }

        @Composable
        private fun LanguageChip(text: String, seleccionado: Boolean, onClick: () -> Unit) {
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (seleccionado) MaterialTheme.colorScheme.primary else Color.LightGray,
                    contentColor = if (seleccionado) MaterialTheme.colorScheme.onPrimary else Color.Black
                ),
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Text(text = text)
            }
        }