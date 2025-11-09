package com.diegodiaz.techwizards.util.logging

import android.util.Log

/**
 * Sink que escribe en Logcat apoyándose en `android.util.Log`.
 *
 * @security
 * - No enmascara por sí mismo; asume que [DecentralizedLogger] aplicó mascarado previo.
 */
class AndroidLogSink : LogSink {
    /**
     * Envía el mensaje a Logcat con la prioridad correspondiente.
     *
     * @param level Nivel textual (VERBOSE..ERROR).
     * @param tag Etiqueta truncada a 23-24 caracteres.
     * @param message Mensaje a imprimir.
     * @param throwable Excepción opcional.
     * @return `Unit` tras delegar en Logcat.
     * @throws IllegalArgumentException No se lanza; Logcat ignora niveles desconocidos.
     * @security No registra PII adicional, delega en el logger superior.
     */
    override fun log(level: String, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            "VERBOSE" -> if (throwable != null) Log.v(tag, message, throwable) else Log.v(tag, message)
            "DEBUG"   -> if (throwable != null) Log.d(tag, message, throwable) else Log.d(tag, message)
            "INFO"    -> if (throwable != null) Log.i(tag, message, throwable) else Log.i(tag, message)
            "WARN"    -> if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
            "ERROR"   -> if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
            else      -> if (throwable != null) Log.d(tag, message, throwable) else Log.d(tag, message)
        }
    }
}
