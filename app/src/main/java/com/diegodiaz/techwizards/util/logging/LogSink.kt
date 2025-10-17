package com.diegodiaz.techwizards.util.logging

/**
 * Interfaz para destinos de log (sinks). Permite enrutar los mensajes
 * a múltiples salidas (Logcat, archivo, crash reporter, etc.).
 */
interface LogSink {
    /**
     * Registra un mensaje con nivel y etiqueta.
     *
     * @param level Nivel del mensaje ("VERBOSE","DEBUG","INFO","WARN","ERROR").
     * @param tag Etiqueta corta (módulo/clase).
     * @param message Texto ya sanitizado.
     * @param throwable Excepción opcional para stacktrace.
     */
    fun log(level: String, tag: String, message: String, throwable: Throwable? = null)
}
