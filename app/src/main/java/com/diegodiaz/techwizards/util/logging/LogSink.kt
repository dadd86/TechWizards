package com.diegodiaz.techwizards.util.logging

/**
 * Contrato para receptores de logs (*sinks*).
 *
 * Un sink decide cómo persistir o emitir el mensaje (Logcat, archivo, red, etc.).
 *
 * @security
 * - Los sinks deben evitar almacenar datos sensibles en claro.
 * - Se recomienda aplicar retención y rotación de archivos, y cifrado si se persiste.
 */
fun interface LogSink {
    /**
     * Emite un mensaje preparado hacia el backend del sink.
     *
     * @param level Nivel tipado del mensaje.
     * @param tag Etiqueta corta identificando el origen.
     * @param message Texto ya sanitizado y opcionalmente enmascarado.
     * @param throwable Excepción opcional para stacktrace.
     * @return Unit: solo efectos secundarios.
     * @security Los sinks deben respetar políticas de protección y limpieza de PII.
     */
    fun emit(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)
}
