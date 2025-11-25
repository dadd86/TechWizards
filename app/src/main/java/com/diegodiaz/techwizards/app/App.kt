package com.diegodiaz.techwizards.app

import android.app.Application
import com.diegodiaz.techwizards.core.ServiceLocator
import com.diegodiaz.techwizards.util.logging.AndroidLogSink
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import com.diegodiaz.techwizards.util.logging.FileLogSink
import com.diegodiaz.techwizards.util.logging.LogLevel
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.domain.model.gameSettingsDefault
import kotlinx.coroutines.runBlocking

/**
 * Punto de entrada de la aplicación responsable de inicializar el *Service Locator*
 * y preparar el logger descentralizado.
 *
 * @security
 * - Registra enmascaramiento para identificadores extensos y evita PII en los sinks.
 */
class App : Application() {
    /**
     * Inicializa repositorios y sinks de logging al arrancar la app.
     *
     * @return `Unit` tras completar la configuración.
     * @throws IllegalStateException No se lanza; los métodos invocados manejan sus errores.
     * @security Aplica mascarado de identificadores antes de registrar sinks persistentes.
     */
    override fun onCreate() {
        super.onCreate()
        DecentralizedLogger.registerSink(AndroidLogSink())
        DecentralizedLogger.registerSink(FileLogSink(this))
        DecentralizedLogger.setMinLevel(LogLevel.INFO)
        DecentralizedLogger.addPiiMask(Regex("[0-9a-fA-F-]{6,}"))
        ServiceLocator.init(this)
        aplicarIdiomaPreferido()
    }

    /**
     * Configura el locale de la app tomando como base el valor guardado
     * en preferencias. Si no existe, fuerza español como idioma principal.
     */
    private fun aplicarIdiomaPreferido() {
        val languageTag = when (val prefs = runBlocking { ServiceLocator.settingsRepository.obtenerPreferencias() }) {
            is Result.Ok -> prefs.value.selectedLanguageTag
            is Result.Err -> gameSettingsDefault.selectedLanguageTag
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
    }
}