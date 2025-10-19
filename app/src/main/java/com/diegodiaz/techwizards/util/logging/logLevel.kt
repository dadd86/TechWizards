package com.diegodiaz.techwizards.util.logging

import android.util.Log

/**
 * Representa el nivel de log de forma tipada y portable.
 *
 * @property androidPriority Mapeo directo al nivel entero de android.util.Log.
 * @property label Etiqueta canónica usada para formateo y sinks que aceptan texto.
 * @security No contiene datos sensibles; solo metadatos de nivel.
 */
enum class LogLevel(val androidPriority: Int, val label: String) {
    VERBOSE(Log.VERBOSE, "VERBOSE"),
    DEBUG(Log.DEBUG, "DEBUG"),
    INFO(Log.INFO, "INFO"),
    WARN(Log.WARN, "WARN"),
    ERROR(Log.ERROR, "ERROR");

    companion object {
        /**
         * Convierte un entero de prioridad Android en un LogLevel conocido.
         *
         * @param priority Prioridad entera recibida (p.ej. Log.DEBUG).
         * @return Nivel equivalente; si no coincide, devuelve INFO.
         * @security Evita valores fuera de rango normalizando a INFO.
         */
        fun fromAndroidPriority(priority: Int): LogLevel = when (priority) {
            Log.VERBOSE -> VERBOSE
            Log.DEBUG -> DEBUG
            Log.INFO -> INFO
            Log.WARN -> WARN
            Log.ERROR, Log.ASSERT -> ERROR
            else -> INFO
        }

        /**
         * Convierte una etiqueta textual a LogLevel.
         *
         * @param label Cadena como "debug", "INFO", etc.
         * @return Nivel equivalente o INFO si no coincide.
         * @security Entrada robusta (case-insensitive y valor por defecto).
         */
        fun fromLabel(label: String): LogLevel = when (label.uppercase()) {
            "VERBOSE" -> VERBOSE
            "DEBUG" -> DEBUG
            "INFO" -> INFO
            "WARN" -> WARN
            "ERROR" -> ERROR
            else -> INFO
        }
    }
}
