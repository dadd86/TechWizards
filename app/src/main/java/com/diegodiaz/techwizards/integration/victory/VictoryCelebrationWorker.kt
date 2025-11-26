package com.diegodiaz.techwizards.integration.victory

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Build
import android.provider.CalendarContract
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import java.util.TimeZone

/**
 * Worker que:
 * 1) Guarda una entrada en el calendario.
 * 2) Genera una imagen "captura" en la galería.
 * 3) Lanza una notificación de victoria.
 *
 * @param appContext Contexto para resolver recursos y permisos.
 * @param workerParams Parámetros serializados por WorkManager.
 * @return `Result.success` cuando todas las acciones se completan.
 * @throws IllegalStateException No se lanza directamente; errores se traducen en `Result.retry`.
 * @security Solo usa alias del jugador y datos de juego.
 */
class VictoryCelebrationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    private val workerContext: Context = applicationContext

    /**
     * Ejecuta la celebración leyendo los datos serializados en la petición.
     *
     * @return `Result.success()` si el payload mínimo es válido; `Result.failure()` en caso contrario.
     * @throws IllegalStateException No se lanza; se propagan fallas de WorkManager si las hubiera.
     * @security Solo loguea alias y métricas agregadas; no expone ubicación ni PII.
     */
    override suspend fun doWork(): Result {
        val alias = inputData.getString(KEY_ALIAS) ?: "Jugador"
        val delta = inputData.getInt(KEY_DELTA_MONEDAS, 0)
        val timestamp = inputData.getLong(KEY_TIMESTAMP, System.currentTimeMillis())
        val screenshotPath = inputData.getString(KEY_SCREENSHOT_PATH)

        return try {
            guardarEnCalendario(alias, delta, timestamp)
            guardarCapturaEnGaleria(alias, delta, screenshotPath)
            mostrarNotificacion(alias, delta)
            DecentralizedLogger.i(TAG, "Celebración de victoria completada sin PII")
            Result.success()
        } catch (t: Throwable) {
            DecentralizedLogger.e(TAG, "Error celebrando victoria", t)
            Result.retry()
        }
    }

    /**
     * Inserta un evento de victoria en el calendario principal.
     *
     * @param alias Alias del jugador ganador.
     * @param deltaMonedas Ganancia obtenida.
     * @param timestampMillis Momento de la victoria.
     * @return `Unit` al intentar la inserción.
     * @throws SecurityException Si el permiso de calendario no está concedido.
     * @security No se guardan identificadores personales.
     */
    private fun guardarEnCalendario(alias: String, deltaMonedas: Int, timestampMillis: Long) {
        if (ActivityCompat.checkSelfPermission(workerContext, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // La Activity debe pedir este permiso antes
            DecentralizedLogger.w(TAG, "Permiso de calendario ausente; se omite evento")
            return
        }

        val now = timestampMillis
        val calId = 1L // para la práctica, usamos el calendario 1

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, now)
            put(CalendarContract.Events.DTEND, now + 30 * 60 * 1000L)
            put(CalendarContract.Events.TITLE, "Victoria en Tech Wizards")
            put(
                CalendarContract.Events.DESCRIPTION,
                "$alias ha ganado $deltaMonedas monedas en Tech Wizards"
            )
            put(CalendarContract.Events.CALENDAR_ID, calId)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        workerContext.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    /**
     * Inserta la captura generada por la UI en la galería del dispositivo.
     *
     * @param alias Alias del jugador.
     * @param deltaMonedas Diferencial de monedas.
     * @param screenshotPath Ruta local a la captura generada.
     * @return `Unit` cuando se completa la escritura.
     * @throws SecurityException Si no existen permisos de almacenamiento requeridos.
     * @security No se agregan metadatos con PII.
     */
    private fun guardarCapturaEnGaleria(alias: String, deltaMonedas: Int, screenshotPath: String?) {
        if (!puedeEscribirEnGaleria(workerContext)) {
            DecentralizedLogger.w(TAG, "Sin permisos de almacenamiento; se omite captura")
            return
        }

        val fileName = "victoria_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TechWizards")
            }
        }

        val uri = workerContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return

        val bitmap = obtenerBitmapDesdeCaptura(workerContext, screenshotPath, alias, deltaMonedas)
        workerContext.contentResolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        bitmap.recycle()
    }

    // 3) Notificación
    private fun mostrarNotificacion(alias: String, deltaMonedas: Int) {
        val manager = ContextCompat.getSystemService(
            workerContext,
            NotificationManager::class.java
        ) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(workerContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            DecentralizedLogger.w(TAG, "Permiso de notificaciones ausente; no se mostrará alerta")
            return
        }


        val channelId = "victory_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                workerContext.getString(R.string.victory_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(workerContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(workerContext.getString(R.string.victory_notification_title))
            .setContentText(workerContext.getString(R.string.victory_notification_body, alias))
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val TAG = "VictoryWorker"

        const val KEY_ALIAS = "alias"
        const val KEY_DELTA_MONEDAS = "deltaMonedas"
        const val KEY_TIMESTAMP = "timestamp"
        const val KEY_SCREENSHOT_PATH = "screenshotPath"
        private const val NOTIFICATION_ID = 1001
    }
    /**
     * Verifica permisos mínimos para escribir en la galería según la versión de Android.
     *
     * @param context Contexto de aplicación.
     * @return `true` si se puede escribir una imagen.
     * @throws IllegalStateException No se lanza; se controla con comprobaciones.
     * @security No se consultan datos personales.
     */
    private fun puedeEscribirEnGaleria(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            true
        } else {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Obtiene un `Bitmap` a partir de la captura guardada o genera uno genérico como respaldo.
     *
     * @param context Contexto de aplicación.
     * @param screenshotPath Ruta local a la captura original.
     * @param alias Alias del jugador.
     * @param deltaMonedas Ganancia obtenida.
     * @return `Bitmap` listo para persistir en galería.
     * @throws IllegalArgumentException Si la ruta es inválida.
     * @security No se incorporan metadatos sensibles en la imagen resultante.
     */
    private fun obtenerBitmapDesdeCaptura(
        context: Context,
        screenshotPath: String?,
        alias: String,
        deltaMonedas: Int
    ): Bitmap {
        val bitmapFromPath = screenshotPath?.let { path ->
            BitmapFactory.decodeFile(path)
        }

        if (bitmapFromPath != null) {
            return bitmapFromPath
        }

        val fallback = Bitmap.createBitmap(1080, 600, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(fallback)
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 64f
        }

        canvas.drawARGB(255, 0, 0, 0)
        paint.setARGB(255, 255, 215, 0)
        canvas.drawText(context.getString(R.string.victory_notification_title), 80f, 250f, paint)
        canvas.drawText("+$deltaMonedas", 80f, 350f, paint)
        return fallback
    }
}
