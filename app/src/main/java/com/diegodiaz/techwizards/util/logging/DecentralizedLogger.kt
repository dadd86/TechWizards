package com.diegodiaz.techwizards.util.logging

/**
 * Permite enrutar mensajes a Logcat, archivos u otros destinos respetando políticas anti-PII.
 *
 * @security
 * - Enmascara cadenas configuradas antes de enviar a los sinks.
 * - Limita el tamaño del tag a 24 caracteres para mitigar leaks accidentales.
 */
object DecentralizedLogger {

    /** Niveles soportados en orden de verbosidad. */
    private val levels = listOf("VERBOSE", "DEBUG", "INFO", "WARN", "ERROR")
    /** Nivel mínimo de salida actual. Por defecto "DEBUG" en debug y "INFO" en release (ajústalo desde App.kt). */
    @Volatile private var minLevel: String = "DEBUG"

    private val sinks = mutableListOf<LogSink>()
    private val piiRegexes = mutableListOf<Regex>()

    /**
     * Registra un nuevo sink si aún no existe otro del mismo tipo.
     *
     * @param sink Implementación de [LogSink] a enlazar.
     * @return `Unit` tras completar el registro o reemplazo.
     * @throws IllegalStateException No se lanza; la sincronización evita estados inconsistentes.
     * @security No persiste ni expone datos sensibles, solo referencias de sinks.
     */
    @Synchronized
    fun registerSink(sink: LogSink) {
        val existingIndex = sinks.indexOfFirst { it::class == sink::class }
        if (existingIndex >= 0) {
            sinks[existingIndex] = sink
        } else {
            sinks.add(sink)
        }
    }

    /**
     * Configura el nivel mínimo de log visible.
     *
     * @param level Uno de: VERBOSE, DEBUG, INFO, WARN o ERROR.
     * @return `Unit` al finalizar.
     * @throws IllegalArgumentException Se encapsula; valores no válidos se ignoran silenciosamente.
     * @security No registra entradas inválidas, solo ajusta una bandera interna.
     */
    fun setMinLevel(level: String) {
        val upper = level.uppercase()
        if (upper in levels) minLevel = upper
    }

    /**
     * Variante tipada de [setMinLevel] para consumidores Kotlin.
     *
     * @param level Nivel deseado.
     * @return `Unit` al finalizar.
     * @throws IllegalArgumentException No se lanza; delega en [setMinLevel].
     * @security Sin exposición adicional.
     */
    fun setMinLevel(level: LogLevel) {
        setMinLevel(level.label)
    }

    /**
     * Añade mascarado básico de PII (opcional).
     * @param regex Expresión regular a enmascarar.
     */
    fun addPiiMask(regex: Regex) {
        piiRegexes.add(regex)
    }

    /**
     * Emite un log con nivel VERBOSE.
     *
     * @param tag Etiqueta corta asociada al módulo.
     * @param message Mensaje ya sanitizado por el llamador.
     * @param throwable Excepción opcional con traza.
     * @return `Unit` tras distribuir a los sinks.
     * @throws IllegalStateException No se lanza; los sinks manejan sus propias excepciones.
     * @security Enmascara coincidencias configuradas antes de propagar.
     */
    fun v(tag: String, message: String, throwable: Throwable? = null) = log("VERBOSE", tag, message, throwable)

    /**
     * Emite un log con nivel DEBUG.
     *
     * @param tag Etiqueta corta asociada al módulo.
     * @param message Mensaje ya sanitizado por el llamador.
     * @param throwable Excepción opcional con traza.
     * @return `Unit` tras distribuir a los sinks.
     * @throws IllegalStateException No se lanza; los sinks manejan sus propias excepciones.
     * @security Enmascara coincidencias configuradas antes de propagar.
     */
    fun d(tag: String, message: String, throwable: Throwable? = null) = log("DEBUG", tag, message, throwable)

    /**
     * Emite un log con nivel INFO.
     *
     * @param tag Etiqueta corta asociada al módulo.
     * @param message Mensaje ya sanitizado por el llamador.
     * @param throwable Excepción opcional con traza.
     * @return `Unit` tras distribuir a los sinks.
     * @throws IllegalStateException No se lanza; los sinks manejan sus propias excepciones.
     * @security Enmascara coincidencias configuradas antes de propagar.
     */
    fun i(tag: String, message: String, throwable: Throwable? = null) = log("INFO", tag, message, throwable)

    /**
     * Emite un log con nivel WARN.
     *
     * @param tag Etiqueta corta asociada al módulo.
     * @param message Mensaje ya sanitizado por el llamador.
     * @param throwable Excepción opcional con traza.
     * @return `Unit` tras distribuir a los sinks.
     * @throws IllegalStateException No se lanza; los sinks manejan sus propias excepciones.
     * @security Enmascara coincidencias configuradas antes de propagar.
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) = log("WARN", tag, message, throwable)

    /**
     * Emite un log con nivel ERROR.
     *
     * @param tag Etiqueta corta asociada al módulo.
     * @param message Mensaje ya sanitizado por el llamador.
     * @param throwable Excepción opcional con traza.
     * @return `Unit` tras distribuir a los sinks.
     * @throws IllegalStateException No se lanza; los sinks manejan sus propias excepciones.
     * @security Enmascara coincidencias configuradas antes de propagar.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) = log("ERROR", tag, message, throwable)

    private fun log(level: String, tag: String, rawMessage: String, throwable: Throwable?) {
        if (!isEnabled(level)) return
        val sanitized = maskIfNeeded(rawMessage)
        sinks.forEach { it.log(level, tag.take(24), sanitized, throwable) }
    }

    private fun isEnabled(level: String): Boolean {
        val idx = levels.indexOf(level)
        val minIdx = levels.indexOf(minLevel)
        return idx >= 0 && minIdx >= 0 && idx >= minIdx
    }

    private fun maskIfNeeded(message: String): String {
        var m = message
        piiRegexes.forEach { re -> m = m.replace(re, "***") }
        return m
    }
}
