package com.diegodiaz.techwizards.util.logging

import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

/**
 * Fachada de logging descentralizado y extensible por *sinks*.
 *
 * Un punto único para:
 * - Registrar sinks (Logcat, archivo, remoto...).
 * - Definir nivel mínimo de salida.
 * - Enmascarar PII sensible.
 * - Normalizar etiquetas y longitudes seguras.
 * - Ofrecer API cómoda (v/d/i/w/e) y puente con prioridades Android (int).
 *
 * @security
 * - Sanitiza etiqueta y mensaje (longitud máxima).
 * - Permite mascarado de PII por regex.
 * - Evita pérdidas silenciosas: si no hay sinks, escribe en Logcat e informa.
 */
object loggingDecentralizedLogger {

    // --- Parámetros de seguridad/compatibilidad ---
    private const val fallbackTag = "DecentralizedLogger"   // etiqueta segura por defecto
    private const val maxTagLength = 23                      // límite Android
    private const val maxMessageLength = 4000                // tamaño seguro para Logcat

    // --- Estado configurable en tiempo de ejecución ---
    private val minLevel = AtomicReference(LogLevel.DEBUG)   // por defecto más verboso en dev
    private val sinks = CopyOnWriteArrayList<LogSink>()
    private val piiRegexes = CopyOnWriteArrayList<Regex>()

    /**
     * Registra un sink (si no está ya). Es seguro en concurrencia.
     *
     * @param sink Instancia del receptor de logs.
     * @security Evita duplicados por clase para no emitir entradas repetidas.
     */
    fun registerSink(sink: LogSink) {
        if (sinks.none { it::class == sink::class }) {
            sinks.add(sink)
        }
    }

    /**
     * Establece el nivel mínimo de visibilidad.
     *
     * @param level MinLevel (los inferiores no se emiten).
     * @security Impide spam excesivo en producción al fijar un umbral.
     */
    fun setMinLevel(level: LogLevel) {
        minLevel.set(level)
    }

    /**
     * Añade un patrón de PII para enmascarar (***).
     *
     * @param regex Expresión regular a ocultar.
     * @security Permite ocultar tokens, emails, etc.
     */
    fun addPiiMask(regex: Regex) {
        piiRegexes.add(regex)
    }

    // --------- API cómoda estilo Android: v/d/i/w/e ---------

    fun v(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.VERBOSE, tag, message, throwable)

    fun d(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.DEBUG, tag, message, throwable)

    fun i(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.INFO, tag, message, throwable)

    fun w(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.WARN, tag, message, throwable)

    fun e(tag: String, message: String, throwable: Throwable? = null) =
        log(LogLevel.ERROR, tag, message, throwable)

    // --------- API de compatibilidad: prioridad int de Android ---------

    /**
     * Permite usar prioridades clásicas de Android (Log.DEBUG, etc.).
     *
     * @param priority Prioridad entera Android.
     * @param tag Etiqueta corta.
     * @param message Mensaje a registrar.
     * @param throwable Excepción opcional.
     */
    fun log(priority: Int, tag: String, message: String, throwable: Throwable? = null) {
        log(LogLevel.fromAndroidPriority(priority), tag, message, throwable)
    }

    // --------- Núcleo de emisión ---------

    /**
     * Emite un mensaje tras pasar por defensas (nivel, sanitizado, PII).
     *
     * @param level Nivel del mensaje.
     * @param tag Etiqueta.
     * @param rawMessage Mensaje sin procesar.
     * @param throwable Excepción opcional.
     * @security
     * - Etiqueta y mensaje sanitizados (longitud y espacios).
     * - Enmascarado PII.
     * - Si no hay sinks, escribe en Logcat + warning.
     */
    fun log(level: LogLevel, tag: String, rawMessage: String, throwable: Throwable? = null) {
        if (!isEnabled(level)) return

        val sanitizedTag = sanitizeTag(tag)
        val sanitizedMessage = sanitizeMessage(maskIfNeeded(rawMessage))

        if (sinks.isEmpty()) {
            // Fallback controlado a Logcat
            logToLogcat(level, sanitizedTag, sanitizedMessage, throwable)
            Log.w(
                fallbackTag,
                "No hay sinks registrados; se usó Logcat como backend por defecto"
            )
            return
        }

        var failures = 0
        val errors = mutableListOf<String>()
        sinks.forEach { sink ->
            runCatching { sink.emit(level, sanitizedTag, sanitizedMessage, throwable) }
                .onFailure { t ->
                    failures++
                    errors.add("${sink::class.java.simpleName}: ${t.message.orEmpty().take(maxMessageLength)}")
                }
        }

        if (failures > 0) {
            Log.w(
                fallbackTag,
                "Fallaron $failures/${sinks.size} sinks. Detalles: ${errors.joinToString()}"
            )
        }
    }

    // --------- Utilidades internas ---------

    private fun isEnabled(level: LogLevel): Boolean {
        // Mantiene orden VERBOSE < DEBUG < INFO < WARN < ERROR
        val min = minLevel.get()
        return level.ordinal >= min.ordinal
    }

    private fun sanitizeTag(tag: String): String =
        tag.trim().take(maxTagLength).ifBlank { fallbackTag }

    private fun sanitizeMessage(message: String): String =
        if (message.length <= maxMessageLength) message else message.take(maxMessageLength)

    private fun maskIfNeeded(message: String): String {
        var out = message
        piiRegexes.forEach { re -> out = out.replace(re, "***") }
        return out
    }

    private fun logToLogcat(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        when (level) {
            LogLevel.VERBOSE -> if (throwable != null) Log.v(tag, message, throwable) else Log.v(tag, message)
            LogLevel.DEBUG -> if (throwable != null) Log.d(tag, message, throwable) else Log.d(tag, message)
            LogLevel.INFO -> if (throwable != null) Log.i(tag, message, throwable) else Log.i(tag, message)
            LogLevel.WARN -> if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
            LogLevel.ERROR -> if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
        }
    }
}
