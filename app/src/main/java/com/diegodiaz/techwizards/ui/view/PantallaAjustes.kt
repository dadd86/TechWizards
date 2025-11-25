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

    LaunchedEffect(ajustesState.settings.musicEnabled) {
        musicController.setEnabled(ajustesState.settings.musicEnabled)
    }

    val abrirPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        onElegirPista(uri)
    }

    val fondo = if (!isDarkTheme) Color(0xFFB5E2F8) else MaterialTheme.colorScheme.background

    val compact = dims.minSide < 360.dp
    val verticalBetweenSections = if (compact) 4.dp else 8.dp
    val horizontalPadding = if (compact) dims.spaceSm else dims.spaceMd
    val bottomPaddingExtra =
        if (compact) dims.buttonHeightSm + dims.spaceSm else dims.buttonHeightSm + dims.spaceMd

    // botón “Back to menu” más pequeño
    val backButtonHeight = if (compact) dims.buttonHeightSm * 0.8f else dims.buttonHeightSm * 0.9f
    var backTextSize = dims.bodySp
    if (backTextSize > 16.sp) backTextSize = 16.sp

    Scaffold(
        containerColor = fondo,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (compact) 4.dp else dims.spaceSm),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onVolverAlMenu,
                    modifier = Modifier
                        .fillMaxWidth(if (compact) 0.85f else 0.7f)
                        .height(backButtonHeight)
                        .clip(RoundedCornerShape(18.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_back_to_menu),
                        color = Color(0xFF3B71B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = backTextSize
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
            verticalArrangement = Arrangement.spacedBy(verticalBetweenSections)
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

            // ---- Sound ----
            AjustesSeccion(
                titulo = stringResource(id = R.string.settings_sound_section),
                dims = dims,
                compact = compact
            ) {
                AjusteSwitch(
                    label = stringResource(id = R.string.settings_music_toggle),
                    checked = settings.musicEnabled,
                    onCheckedChange = { checked ->
                        musicController.setEnabled(checked)
                        onToggleMusic(checked)
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
                    modifier = Modifier.fillMaxWidth(if (compact) 1f else 0.7f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    var pickTextSize = dims.bodySp
                    if (pickTextSize > 15.sp) pickTextSize = 15.sp
                    Text(
                        text = stringResource(id = R.string.settings_pick_track),
                        fontSize = pickTextSize
                    )
                }
            }

            // ---- Display ----
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

            // ---- Notifications ----
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

            // ---- Language ----
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

            Spacer(Modifier.height(if (compact) dims.spaceSm else dims.spaceMd))
        }
    }
}

// ------------------ Secciones / componentes reutilizables ------------------

@Composable
fun AjustesSeccion(
    titulo: String,
    dims: UiDims,
    compact: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    var titleSize = if (compact) (dims.titleSp.value * 0.85f).sp else dims.titleSp
    if (titleSize > 22.sp) titleSize = 22.sp

    Text(
        text = titulo,
        fontSize = titleSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp),
        content = content
    )
}

@Composable
fun AjusteSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    dims: UiDims,
    compact: Boolean
) {
    val maxLabelSize = if (compact) 14.sp else 16.sp
    var labelSize = dims.bodySp
    if (labelSize > maxLabelSize) labelSize = maxLabelSize

    val switchScale = if (compact) 0.7f else 0.8f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (compact) dims.spaceXs else dims.spaceSm,
                vertical = if (compact) 2.dp else 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = labelSize,
            color = MaterialTheme.colorScheme.onBackground
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.scale(switchScale)
        )
    }
}

@Composable
fun IdiomaSelector(
    seleccionado: String,
    onSeleccion: (String) -> Unit,
    dims: UiDims,
    compact: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (compact) dims.spaceXs else dims.spaceSm),
        horizontalArrangement = Arrangement.spacedBy(if (compact) dims.spaceXs else dims.spaceSm)
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
fun LanguageChip(
    text: String,
    seleccionado: Boolean,
    dims: UiDims,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val maxSize = if (compact) 13.sp else 14.sp
    var chipFontSize = dims.bodySp
    if (chipFontSize > maxSize) chipFontSize = maxSize

    val chipHeight = if (compact) dims.buttonHeightSm * 0.7f else dims.buttonHeightSm * 0.8f

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado)
                MaterialTheme.colorScheme.primary
            else
                Color(0xFFD6D6D6),
            contentColor = if (seleccionado)
                MaterialTheme.colorScheme.onPrimary
            else
                Color.Black
        ),
        modifier = modifier
            .height(chipHeight)
            .clip(RoundedCornerShape(dims.cardCorner / 2)),
        contentPadding = PaddingValues(
            horizontal = if (compact) 4.dp else 6.dp,
            vertical = 0.dp
        )
    ) {
        Text(
            text = text,
            fontSize = chipFontSize,
            maxLines = 1
        )
    }
}
