package com.diegodiaz.techwizards.util.logging

/**
 * Logger central con múltiples sinks y control de nivel.
 * Úsalo en cualquier capa: Logger.info("Repo", "cargado")
 */
object DecentralizedLogger {

    /** Niveles soportados en orden de verbosidad. */
    private val levels = listOf("VERBOSE","DEBUG","INFO","WARN","ERROR")

    /** Nivel mínimo de salida actual. Por defecto "DEBUG" en debug y "INFO" en release (ajústalo desde App.kt). */
    @Volatile private var minLevel: String = "DEBUG"

    private val sinks = mutableListOf<LogSink>()
    private val piiRegexes = mutableListOf<Regex>()

    /**
     * Registra un sink. Evita duplicados simples.
     */
    @Synchronized
    fun registerSink(sink: LogSink) {
        if (sinks.none { it::class == sink::class }) {
            sinks.add(sink)
        }
    }

    /**
     * Configura el nivel mínimo de log visible.
     * @param level Uno de: VERBOSE, DEBUG, INFO, WARN, ERROR
     */
    fun setMinLevel(level: String) {
        val upper = level.uppercase()
        if (upper in levels) minLevel = upper
    }

    /**
     * Añade mascarado básico de PII (opcional).
     * @param regex Expresión regular a enmascarar.
     */
    fun addPiiMask(regex: Regex) {
        piiRegexes.add(regex)
    }

    fun v(tag: String, message: String, throwable: Throwable? = null) = log("VERBOSE", tag, message, throwable)
    fun d(tag: String, message: String, throwable: Throwable? = null) = log("DEBUG", tag, message, throwable)
    fun i(tag: String, message: String, throwable: Throwable? = null) = log("INFO", tag, message, throwable)
    fun w(tag: String, message: String, throwable: Throwable? = null) = log("WARN", tag, message, throwable)
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
