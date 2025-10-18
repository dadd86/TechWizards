package com.diegodiaz.techwizards.app

import android.app.Application
import com.diegodiaz.techwizards.util.logging.AndroidLogSink
import com.diegodiaz.techwizards.util.logging.FileLogSink
import com.diegodiaz.techwizards.util.logging.LogLevel
import com.diegodiaz.techwizards.util.logging.loggingDecentralizedLogger
import com.diegodiaz.techwizards.BuildConfig


/**
 * Punto de arranque de la app: registra sinks y define nivel mínimo.
 *
 * @security Evita registrar sinks duplicados y define máscaras PII comunes.
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Nivel mínimo: DEBUG en dev; INFO/ WARN en release (puedes condicionar por BuildConfig.DEBUG)
        loggingDecentralizedLogger.setMinLevel(
            if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.INFO
        )

        // Sinks
        loggingDecentralizedLogger.registerSink(AndroidLogSink())
        loggingDecentralizedLogger.registerSink(FileLogSink(this))

        // Máscaras PII de ejemplo (emails y UUID)
        loggingDecentralizedLogger.addPiiMask(Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"))
        loggingDecentralizedLogger.addPiiMask(Regex("[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}"))
    }
}
