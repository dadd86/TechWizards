package com.diegodiaz.techwizards.ui.view

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import java.util.Locale

/**
 * Sección de ayuda basada en WebView con soporte para EN/DE.
 *
 * @return `Unit` tras componer la pantalla.
 * @throws IllegalStateException No se lanza directamente; los fallos del WebView se propagan al sistema.
 * @security El contenido se sirve desde `assets/` sin exponer datos sensibles.
 */
@Composable
fun PantallaAyuda() {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(resolveDefaultLanguage()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.help_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { selectedLanguage = "en"; logLanguage("en") }) {
                Text(text = "EN")
            }
            Button(onClick = { selectedLanguage = "de"; logLanguage("de") }) {
                Text(text = "DE")
            }
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

private fun resolveDefaultLanguage(): String {
    val locale = Locale.getDefault().language.lowercase(Locale.ROOT)
    return if (locale == "de") "de" else "en"
}

private fun getHelpAsset(language: String): String =
    when (language.lowercase(Locale.ROOT)) {
        "de" -> "file:///android_asset/help/index_de.html"
        else -> "file:///android_asset/help/index_en.html"
    }

private fun logLanguage(language: String) {
    DecentralizedLogger.i(
        tag = "Help",
        message = "Idioma de ayuda seleccionado=$language"
    )
}