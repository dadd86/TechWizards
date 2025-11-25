package com.diegodiaz.techwizards.ui.view

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.UiDims
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import java.util.Locale
import androidx.compose.foundation.layout.PaddingValues


@Composable
fun PantallaAyuda(
    dims: UiDims
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(resolveDefaultLanguage()) }

    val compact = dims.minSide < 360.dp

    // Capamos los tamaños de fuente para que no se disparen
    val titleSize = dims.titleSp.value
        .coerceIn(18f, 22f) // entre 18sp y 22sp
        .sp

    val bodySize = dims.bodySp.value
        .coerceIn(12f, 16f) // entre 12sp y 16sp
        .sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = if (compact) dims.spaceSm else dims.spaceMd,
                vertical = if (compact) dims.spaceXs else dims.spaceSm
            ),
        verticalArrangement = Arrangement.spacedBy(if (compact) dims.spaceXs else dims.spaceSm)
    ) {
        // Título
        Text(
            text = stringResource(id = R.string.help_title),
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Chips de idioma
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (compact) dims.spaceXs else dims.spaceSm),
            horizontalArrangement = Arrangement.spacedBy(if (compact) dims.spaceXs else dims.spaceSm)
        ) {
            HelpLanguageChip(
                label = stringResource(id = R.string.language_es_label),
                selected = selectedLanguage == "es",
                dims = dims,
                compact = compact,
                modifier = Modifier.weight(1f)
            ) { actualizarIdiomaAyuda("es") { selectedLanguage = it } }

            HelpLanguageChip(
                label = stringResource(id = R.string.language_en_label),
                selected = selectedLanguage == "en",
                dims = dims,
                compact = compact,
                modifier = Modifier.weight(1f)
            ) { actualizarIdiomaAyuda("en") { selectedLanguage = it } }

            HelpLanguageChip(
                label = stringResource(id = R.string.language_de_label),
                selected = selectedLanguage == "de",
                dims = dims,
                compact = compact,
                modifier = Modifier.weight(1f)
            ) { actualizarIdiomaAyuda("de") { selectedLanguage = it } }
        }

        // WebView ocupando el resto
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = {
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = false
                        allowFileAccess = false
                        // Reducimos el zoom de texto según tamaño de pantalla
                        textZoom = if (compact) 80 else 90
                    }
                    loadUrl(getHelpAsset(selectedLanguage))
                }
            },
            update = { webView ->
                webView.settings.textZoom = if (compact) 80 else 90
                webView.loadUrl(getHelpAsset(selectedLanguage))
            }
        )
    }
}

@Composable
private fun HelpLanguageChip(
    label: String,
    selected: Boolean,
    dims: UiDims,
    compact: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // Limitar aún más el tamaño de letra de los chips
    val base = dims.bodySp.value.coerceIn(11f, 14f).sp
    val fontSize = if (compact) (base.value * 0.95f).sp else base
    val chipHeight = if (compact) (dims.buttonHeightSm * 0.7f) else (dims.buttonHeightSm * 0.8f)

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
            .height(chipHeight),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(
            horizontal = if (compact) dims.spaceXs else dims.spaceSm,
            vertical = dims.spaceXs
        )
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ------------------------ auxiliares ------------------------

private fun resolveDefaultLanguage(): String {
    val localeList = AppCompatDelegate.getApplicationLocales()
    val explicit = localeList[0]?.language?.lowercase(Locale.ROOT)
    if (!explicit.isNullOrBlank()) return explicit

    val locale = Locale.getDefault().language.lowercase(Locale.ROOT)
    return when {
        locale.startsWith("de") -> "de"
        locale.startsWith("en") -> "en"
        else -> "es"
    }
}

private fun getHelpAsset(language: String): String =
    when (language.lowercase(Locale.ROOT)) {
        "de" -> "file:///android_asset/help/index_de.html"
        "es" -> "file:///android_asset/help/index.html"
        else -> "file:///android_asset/help/index_en.html"
    }

private fun actualizarIdiomaAyuda(language: String, onSelected: (String) -> Unit) {
    onSelected(language)
    val tag = when (language.lowercase(Locale.ROOT)) {
        "de" -> "de-DE"
        "en" -> "en-US"
        else -> "es-ES"
    }
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    DecentralizedLogger.i("Help", "Idioma de ayuda seleccionado=$language")
}
