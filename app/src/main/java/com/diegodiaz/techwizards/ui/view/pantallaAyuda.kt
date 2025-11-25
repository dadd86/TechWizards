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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Sección de ayuda basada en WebView con soporte para ES/EN/DE.
 *
 * @return `Unit` tras componer la pantalla.
 * @throws IllegalStateException No se lanza directamente; los fallos del WebView se propagan al sistema.
 * @security El contenido se sirve desde `assets/` sin exponer datos sensibles.
 */
@Composable
fun PantallaAyuda() = Responsive { dims ->
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(resolveDefaultLanguage()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dims.spaceMd, vertical = dims.spaceSm),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        Text(
            text = stringResource(id = R.string.help_title),
            fontSize = dims.titleSp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            HelpLanguageChip(
                label = stringResource(id = R.string.language_es_label),
                selected = selectedLanguage == "es",
                dims = dims
            ) { actualizarIdiomaAyuda("es") { selectedLanguage = it } }
            HelpLanguageChip(
                label = stringResource(id = R.string.language_en_label),
                selected = selectedLanguage == "en",
                dims = dims
            ) { actualizarIdiomaAyuda("en") { selectedLanguage = it } }
            HelpLanguageChip(
                label = stringResource(id = R.string.language_de_label),
                selected = selectedLanguage == "de",
                dims = dims
            ) { actualizarIdiomaAyuda("de") { selectedLanguage = it } }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = false
                        allowFileAccess = false
                    }
                    loadUrl(getHelpAsset(selectedLanguage))
                }
            },
            update = { webView ->
                webView.loadUrl(getHelpAsset(selectedLanguage))
            }
        )
    }
}

@Composable
private fun HelpLanguageChip(label: String, selected: Boolean, dims: UiDims, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.height(dims.buttonHeightSm),
        shape = MaterialTheme.shapes.large
    ) {
        Text(text = label, fontSize = dims.bodySp)
    }
}
private fun resolveDefaultLanguage(): String {
    val localeList = AppCompatDelegate.getApplicationLocales()
    val explicit = localeList[0]?.language?.lowercase(Locale.ROOT)
    if (!explicit.isNullOrBlank()) return explicit

    val locale = Locale.getDefault().language.lowercase(Locale.ROOT)
    return if (locale.startsWith("de")) "de" else if (locale.startsWith("en")) "en" else "es"
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
    DecentralizedLogger.i(
        tag = "Help",
        message = "Idioma de ayuda seleccionado=$language"
    )
}