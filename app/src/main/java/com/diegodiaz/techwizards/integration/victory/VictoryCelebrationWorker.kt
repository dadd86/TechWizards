package com.diegodiaz.techwizards.integration.victory

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
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
 * @return `Result.success` cuando todas las acciones se completan.
 * @throws IllegalStateException No se lanza directamente; errores se traducen en `Result.retry`.
 * @security Solo usa alias del jugador y datos de juego.
 */
class VictoryCelebrationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val alias = inputData.getString(KEY_ALIAS) ?: "Jugador"
        val delta = inputData.getInt(KEY_DELTA_MONEDAS, 0)

        return try {
            guardarEnCalendario(alias, delta)
            guardarCapturaEnGaleria(alias, delta)
            mostrarNotificacion(alias, delta)
            DecentralizedLogger.i(TAG, "Celebración de victoria completada")
            Result.success()
        } catch (t: Throwable) {
            DecentralizedLogger.e(TAG, "Error celebrando victoria", t)
            Result.retry()
        }
    }

    // 1) Guardar evento en calendario
    private fun guardarEnCalendario(alias: String, deltaMonedas: Int) {
        val ctx = applicationContext

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_CALENDAR)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // La Activity debe pedir este permiso antes
            return
        }

        val now = System.currentTimeMillis()
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

        ctx.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    // 2) Guardar "captura" en la galería
    private fun guardarCapturaEnGaleria(alias: String, deltaMonedas: Int) {
        val ctx = applicationContext

        val fileName = "victoria_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TechWizards")
            }
        }

        val uri = ctx.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return

        // Bitmap simple que hace de "captura" de victoria
        val bitmap = Bitmap.createBitmap(1080, 600, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 64f
        }

        canvas.drawARGB(255, 0, 0, 0)
        paint.setARGB(255, 255, 215, 0)
        canvas.drawText("¡Victoria de $alias!", 80f, 250f, paint)
        canvas.drawText("+$deltaMonedas monedas", 80f, 350f, paint)

        ctx.contentResolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        bitmap.recycle()
    }

    // 3) Notificación
    private fun mostrarNotificacion(alias: String, deltaMonedas: Int) {
        val ctx = applicationContext
        val manager = ContextCompat.getSystemService(
            ctx,
            NotificationManager::class.java
        ) ?: return

        val channelId = "victory_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                ctx.getString(R.string.victory_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(ctx, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(ctx.getString(R.string.victory_notification_title))
            .setContentText(ctx.getString(R.string.victory_notification_body, alias))
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val TAG = "VictoryWorker"

        const val KEY_ALIAS = "alias"
        const val KEY_DELTA_MONEDAS = "deltaMonedas"
        private const val NOTIFICATION_ID = 1001
    }
}
