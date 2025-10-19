package com.diegodiaz.techwizards.util.logging

import android.content.Context
import androidx.annotation.WorkerThread
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.concurrent.Executors

/**
 * Sink que escribe logs a archivo en el almacenamiento interno de la app,
 * con rotación simple por tamaño.
 *
 * Nota: escribe en un thread de un solo hilo para evitar bloqueos del main thread.
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

    @WorkerThread
    private fun writeLine(line: String) {
        rotateIfNeeded()
        logFile.appendText(line)
    }

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
