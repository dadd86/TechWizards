package com.diegodiaz.techwizards.util.logging

import android.content.Context
import androidx.annotation.WorkerThread
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.concurrent.Executors

/**
 * Sink de archivo con rotación simple por tamaño (sin bloqueos de UI).
 *
 * @param context Contexto de app para seleccionar almacenamiento interno.
 * @param maxBytes Umbral de rotación (por defecto 5 MB).
 * @param backupCount Número de backups a conservar.
 * @param fileName Nombre base del archivo principal.
 * @security
 * - Escribe en almacenamiento interno (privado por app).
 * - Si se requiere alta seguridad, cifrar contenido antes de persistir.
 */
class FileLogSink(
    context: Context,
    private val maxBytes: Long = 5L * 1024L * 1024L,
    private val backupCount: Int = 3,
    private val fileName: String = "app.log"
) : LogSink {

    private val logDir: File = File(context.filesDir, "logs").apply { mkdirs() }
    private val logFile: File = File(logDir, fileName)
    private val ioExecutor = Executors.newSingleThreadExecutor()
    private val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    override fun emit(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val line = buildString {
            append(stamp.format(Date()))
            append(" ")
            append(level.label.padEnd(7, ' '))
            append(" ")
            append(tag.padEnd(24, ' ').take(24))
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

    @WorkerThread
    private fun writeLine(line: String) {
        rotateIfNeeded()
        logFile.appendText(line)
    }

    @WorkerThread
    private fun rotateIfNeeded() {
        if (logFile.length() < maxBytes) return
        // Elimina el más antiguo
        val oldest = File(logDir, "$fileName.${backupCount}")
        if (oldest.exists()) oldest.delete()
        // Desplaza backups
        for (i in backupCount - 1 downTo 1) {
            val src = File(logDir, "$fileName.$i")
            val dest = File(logDir, "$fileName.${i + 1}")
            if (src.exists()) src.renameTo(dest)
        }
        // Renombra actual a .1 y limpia
        val firstBackup = File(logDir, "$fileName.1")
        if (logFile.exists()) logFile.renameTo(firstBackup)
        logFile.writeText("")
    }
}
