package com.diegodiaz.techwizards.ui.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.integration.media.*
import com.diegodiaz.techwizards.ui.controller.AjustesState
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims
import com.diegodiaz.techwizards.R


/**
 * Pantalla de ajustes que controla las preferencias de audio, animaciones y notificaciones.
 *
 * @param isDarkTheme Indicador global del tema actual.
 * @param ajustesState Estado observable proveniente del ViewModel.
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
    ajustesState: AjustesState,
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
    val musicController = remember { musicPlaybackController(context.applicationContext) }

    LaunchedEffect(ajustesState.settings?.musicEnabled) {
        ajustesState.settings?.musicEnabled?.let { musicController.setEnabled(it) }
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
            if (ajustesState.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            ajustesState.errorRes?.let { errorRes ->
                Text(
                    text = stringResource(id = errorRes),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }


            val settings = ajustesState.settings

            AjustesSeccion(stringResource(id = R.string.settings_sound_section), dims) {
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_music_toggle),
                    checked = ajustesState.settings?.musicEnabled ?: false,
                ) { checked ->
                    musicController.setEnabled(checked)
                    onToggleMusic(checked)
                }
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_sfx_toggle),
                    checked = settings?.sfxEnabled ?: false,
                    onCheckedChange = onToggleSfx
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
                    checked = settings?.darkThemeEnabled ?: isDarkTheme,
                    onCheckedChange = onToggleTheme
                )
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_animations_toggle),
                    checked = settings?.animationsEnabled ?: true,
                    onCheckedChange = onToggleAnimations
                )
            }

            AjustesSeccion(stringResource(id = R.string.settings_notifications_section), dims) {
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_notifications_toggle),
                    checked = settings?.notificationsEnabled ?: true,
                    onCheckedChange = onToggleNotifications
                )
            }

            AjustesSeccion(stringResource(id = R.string.settings_language_section), dims) {
                IdiomaSelector(
                    seleccionado = settings?.selectedLanguageTag ?: "es-ES",
                    onSeleccion = onSeleccionIdioma
                )
            }
            Spacer(Modifier.height(dims.spaceLg))
        }
    }
}

/**
 * Encabezado y contenedor de una sección de ajustes.
 *
 * @param titulo Texto de la sección.
 * @param dims Dimensiones responsivas.
 * @param content Contenido composable interno.
 */
@Composable
fun AjustesSeccion(
    titulo: String,
    dims: UiDims,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = titulo,
        fontSize = dims.titleSp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs), content = content)
}

/**
 * Conmutador reutilizable para los ajustes booleanos.
 *
 * @param label Texto descriptivo.
 * @param checked Estado actual.
 * @param onCheckedChange Acción al cambiar el valor.
 */
@Composable
fun AjusteSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
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

/**
 * Selector de idioma utilizando chips simples.
 *
 * @param seleccionado Tag actualmente seleccionado.
 * @param onSeleccion Callback de selección.
 */
@Composable
fun IdiomaSelector(seleccionado: String, onSeleccion: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LanguageChip(text = "ES", seleccionado = seleccionado == "es-ES") { onSeleccion("es-ES") }
        LanguageChip(text = "EN", seleccionado = seleccionado == "en-US") { onSeleccion("en-US") }
        LanguageChip(text = "DE", seleccionado = seleccionado == "de-DE") { onSeleccion("de-DE") }
    }
}


/**
 * Botón estilo chip para seleccionar idioma.
 *
 * @param text Etiqueta del chip.
 * @param seleccionado Indica si está activo.
 * @param onClick Acción al pulsarlo.
 */
@Composable
fun LanguageChip(text: String, seleccionado: Boolean, onClick: () -> Unit) {
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
        Text(text = text, fontSize = 14.sp)
    }
}