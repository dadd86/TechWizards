package com.diegodiaz.techwizards.ui.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.integration.media.musicPlaybackController
import com.diegodiaz.techwizards.ui.controller.AjustesState
import com.diegodiaz.techwizards.ui.responsive.UiDims

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
    dims: UiDims
) {
    val context = LocalContext.current
    val musicController = remember { musicPlaybackController(context.applicationContext) }

    // Mantengo tu lógica de sincronización con preferencias
    LaunchedEffect(ajustesState.settings.musicEnabled) {
        musicController.applySettings(
            enabled = ajustesState.settings.musicEnabled,
            selectedUri = null
        )
    }

    val abrirPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        onElegirPista(uri)
        if (uri != null && ajustesState.settings.musicEnabled) {
            musicController.applySettings(
                enabled = true,
                selectedUri = uri.toString()
            )
        }
    }

    val fondo = if (!isDarkTheme) Color(0xFFB5E2F8) else MaterialTheme.colorScheme.background

    val compact = dims.minSide < 360.dp

    // 🔧 Afinamos espaciados para que todo quede más compacto
    val sectionSpacing = if (compact) dims.spaceXs else dims.spaceSm
    val horizontalPadding = if (compact) dims.spaceSm else dims.spaceMd
    val bottomPaddingExtra =
        if (compact) dims.buttonHeightSm + dims.spaceSm
        else dims.buttonHeightSm + dims.spaceSm

    // Botón "Back to menu" más pequeño y proporcionado
    val backButtonHeight = dims.buttonHeightSm * (if (compact) 0.7f else 0.8f)
    val backButtonWidthFraction = if (compact) 0.75f else 0.6f
    val backButtonFont = (dims.bodySp.value * if (compact) 0.9f else 0.95f).sp

    Scaffold(
        containerColor = fondo,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (compact) dims.spaceXs else dims.spaceSm / 1.5f),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onVolverAlMenu,
                    modifier = Modifier
                        .fillMaxWidth(backButtonWidthFraction)
                        .height(backButtonHeight)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_back_to_menu),
                        color = Color(0xFF3B71B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = backButtonFont,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
                .padding(bottom = bottomPaddingExtra),
            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
        ) {
            if (ajustesState.cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            ajustesState.errorRes?.let { errorRes ->
                Text(
                    text = stringResource(id = errorRes),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            }

            val settings = ajustesState.settings

            // ---------- SOUND ----------
            AjustesSeccion(
                titulo = stringResource(id = R.string.settings_sound_section),
                dims = dims,
                compact = compact
            ) {
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_music_toggle),
                    checked = settings.musicEnabled,
                    onCheckedChange = { checked ->
                        onToggleMusic(checked)
                        // El LaunchedEffect sincroniza con el servicio
                    },
                    dims = dims,
                    compact = compact
                )

                AjusteSwitch(
                    label = stringResource(id = R.string.settings_sfx_toggle),
                    checked = settings.sfxEnabled,
                    onCheckedChange = onToggleSfx,
                    dims = dims,
                    compact = compact
                )

                Button(
                    onClick = { abrirPicker.launch(arrayOf("audio/*")) },
                    modifier = Modifier.fillMaxWidth(if (compact) 1f else 0.85f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(
                        horizontal = if (compact) 12.dp else 16.dp,
                        vertical = if (compact) 6.dp else 8.dp
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_pick_track),
                        fontSize = (dims.bodySp.value * if (compact) 0.9f else 1f).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ---------- DISPLAY ----------
            AjustesSeccion(
                titulo = stringResource(id = R.string.settings_visual_section),
                dims = dims,
                compact = compact
            ) {
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_dark_mode_toggle),
                    checked = settings.darkThemeEnabled,
                    onCheckedChange = onToggleTheme,
                    dims = dims,
                    compact = compact
                )

                AjusteSwitch(
                    label = stringResource(id = R.string.settings_animations_toggle),
                    checked = settings.animationsEnabled,
                    onCheckedChange = onToggleAnimations,
                    dims = dims,
                    compact = compact
                )
            }

            // ---------- NOTIFICATIONS ----------
            AjustesSeccion(
                titulo = stringResource(id = R.string.settings_notifications_section),
                dims = dims,
                compact = compact
            ) {
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_notifications_toggle),
                    checked = settings.notificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                    dims = dims,
                    compact = compact
                )
            }

            // ---------- LANGUAGE ----------
            AjustesSeccion(
                titulo = stringResource(id = R.string.settings_language_section),
                dims = dims,
                compact = compact
            ) {
                IdiomaSelector(
                    seleccionado = settings.selectedLanguageTag,
                    onSeleccion = onSeleccionIdioma,
                    dims = dims,
                    compact = compact
                )
            }

            Spacer(Modifier.height(if (compact) dims.spaceSm else dims.spaceSm))
        }
    }
}

/* ==================== VISUAL COMPONENTS ==================== */

@Composable
private fun AjustesSeccion(
    titulo: String,
    dims: UiDims,
    compact: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val titleSize = (dims.titleSp.value * if (compact) 0.9f else 0.95f).sp

    Text(
        text = titulo,
        fontSize = titleSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(
            if (compact) dims.spaceXs / 2 else dims.spaceXs
        ),
        content = content
    )
}

@Composable
private fun AjusteSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    dims: UiDims,
    compact: Boolean
) {
    val labelSize = (dims.bodySp.value * if (compact) 0.9f else 0.95f).sp
    val switchScale = if (compact) 0.7f else 0.8f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (compact) dims.spaceXs else dims.spaceSm,
                vertical = if (compact) dims.spaceXs / 2 else dims.spaceXs
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = labelSize,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(switchScale)
        )
    }
}

@Composable
private fun IdiomaSelector(
    seleccionado: String,
    onSeleccion: (String) -> Unit,
    dims: UiDims,
    compact: Boolean
) {
    val chipSpacing = if (compact) dims.spaceXs else dims.spaceSm

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (compact) dims.spaceXs else dims.spaceSm),
        horizontalArrangement = Arrangement.spacedBy(chipSpacing)
    ) {
        LanguageChip(
            text = stringResource(id = R.string.language_es_label),
            seleccionado = seleccionado == "es-ES",
            dims = dims,
            compact = compact,
            modifier = Modifier.weight(1f)
        ) { onSeleccion("es-ES") }

        LanguageChip(
            text = stringResource(id = R.string.language_en_label),
            seleccionado = seleccionado == "en-US",
            dims = dims,
            compact = compact,
            modifier = Modifier.weight(1f)
        ) { onSeleccion("en-US") }

        LanguageChip(
            text = stringResource(id = R.string.language_de_label),
            seleccionado = seleccionado == "de-DE",
            dims = dims,
            compact = compact,
            modifier = Modifier.weight(1f)
        ) { onSeleccion("de-DE") }
    }
}

@Composable
private fun LanguageChip(
    text: String,
    seleccionado: Boolean,
    dims: UiDims,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Control estricto del tamaño para que el texto SIEMPRE quepa
    val base = if (dims.bodySp > 16.sp) 16.sp else dims.bodySp
    val chipFontSize = (base.value * if (compact) 0.8f else 0.85f).sp
    val chipHeight = dims.buttonHeightSm * (if (compact) 0.65f else 0.7f)
    val radius = dims.cardCorner * 0.5f

    Button(
        onClick = onClick,
        modifier = modifier
            .height(chipHeight)
            .clip(RoundedCornerShape(radius)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado)
                MaterialTheme.colorScheme.primary
            else
                Color(0xFFD8D8D8),
            contentColor = if (seleccionado)
                MaterialTheme.colorScheme.onPrimary
            else
                Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = chipFontSize,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false
        )
    }
}
