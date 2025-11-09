package com.diegodiaz.techwizards.util.logging

import android.content.Context
import androidx.annotation.WorkerThread
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.concurrent.Executors

/**
 * Sink que escribe logs a archivo en el almacenamiento interno de la app con rotación simple.
 * @param context Contexto de aplicación para ubicar `filesDir`.
 * @param maxBytes Tamaño máximo del archivo activo antes de rotar.
 * @param backupCount Cantidad de archivos históricos a mantener.
 * @param fileName Nombre base del archivo principal.
 * @security
 * - Solo persiste mensajes ya sanitizados por [DecentralizedLogger].
 */
class FileLogSink(
    context: Context,
    private val maxBytes: Long = 5L * 1024L * 1024L,  // 5 MB
    private val backupCount: Int = 3,
    private val fileName: String = "app.log"
) : LogSink {

    private val logDir: File = File(context.filesDir, "logs").apply { mkdirs() }
    private val logFile: File = File(logDir, fileName)
    private val ioExecutor = Executors.newSingleThreadExecutor()
    private val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    /**
     * Persiste el mensaje en el archivo de log usando un hilo dedicado para no bloquear UI.
     *
     * @param level Nivel textual (VERBOSE..ERROR).
     * @param tag Etiqueta de origen.
     * @param message Mensaje sanitizado.
     * @param throwable Excepción opcional.
     * @return `Unit` tras encolar la operación.
     * @throws IllegalStateException No se lanza; cualquier error I/O se registra en el executor.
     * @security No añade PII extra; guarda exactamente la entrada recibida.
     */
    override fun log(level: String, tag: String, message: String, throwable: Throwable?) {
        val line = buildString {
            append(stamp.format(Date()))
            append(" ")
            append(level.padEnd(5, ' '))
            append(" ")
            append(tag.take(24).padEnd(24, ' '))
            append(" | ")
            append(message)
            if (throwable != null) {
                append(" | ex=")
                append(throwable.stackTraceToString())
            }
            append('\n')
        }
        ioExecutor.execute { writeLine(line) }
    }

    /**
     * Escribe una línea en el archivo activo garantizando ejecución en hilo de I/O.
     *
     * @param line Cadena ya formateada.
     * @return `Unit` tras completar la escritura.
     * @throws IllegalStateException No se lanza; cualquier IOException se propaga como RuntimeException implícita.
     * @security La línea ya llega sanitizada; no añade datos extra.
     */
    @WorkerThread
    private fun writeLine(line: String) {
        rotateIfNeeded()
        logFile.appendText(line)
    }

    /**
     * Realiza la rotación básica cuando el archivo supera [maxBytes].
     *
     * @return `Unit` tras completar posibles renombrados.
     * @throws IllegalStateException No se lanza; fallos de I/O se propagan automáticamente.
     * @security No toca el contenido, solo administra archivos de log.
     */
    @WorkerThread
    private fun rotateIfNeeded() {
        if (logFile.length() < maxBytes) return
        // Borra el más antiguo
        val oldest = File(logDir, "$fileName.${backupCount}")
        if (oldest.exists()) oldest.delete()
        // Desplaza backups
        for (i in backupCount - 1 downTo 1) {
            val src = File(logDir, "$fileName.$i")
            val dest = File(logDir, "$fileName.${i + 1}")
            if (src.exists()) src.renameTo(dest)
        }
        // Renombra actual a .1
        val firstBackup = File(logDir, "$fileName.1")
        if (logFile.exists()) logFile.renameTo(firstBackup)
        // Crea uno nuevo
        logFile.writeText("")
    }
}
