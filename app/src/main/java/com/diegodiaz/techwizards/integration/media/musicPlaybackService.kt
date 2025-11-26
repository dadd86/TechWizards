package com.diegodiaz.techwizards.integration.media

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger

/**
 * Servicio en primer plano que gestiona la música ambiental del juego.
 *
 * @security No se almacena PII; los logs solo incluyen el tipo de acción solicitada.
 */
class MusicPlaybackService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioManager: AudioManager
    private var focusRequest: Any? = null

    private val focusChangeListener = AudioManager.OnAudioFocusChangeListener { change ->
        when (change) {
            AudioManager.AUDIOFOCUS_LOSS,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pausePlayback()

            AudioManager.AUDIOFOCUS_GAIN -> mediaPlayer?.start()
        }
    }

    private val noisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                DecentralizedLogger.i(TAG, "Pausa por cambio de ruta de audio")
                pausePlayback()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        focusRequest = buildFocusRequest()
        registerReceiver(noisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
        createChannel()
        DecentralizedLogger.i(TAG, "Servicio de música creado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_CUSTOM -> startPlayback(intent.data)
            ACTION_STOP -> stopPlayback()
            else -> startPlayback(null)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(noisyReceiver)
        stopPlayback()
        DecentralizedLogger.i(TAG, "Servicio de música destruido")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startPlayback(customUri: Uri?) {
        val focusResult = requestAudioFocus()
        if (focusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            DecentralizedLogger.w(TAG, "AudioFocus no concedido; abortando reproducción")
            return
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = buildMediaPlayer(customUri)
            startForeground(NOTIFICATION_ID, buildNotification())
            mediaPlayer?.start()
            DecentralizedLogger.i(TAG, "Reproducción iniciada, custom=${customUri != null}")
        } catch (error: Exception) {
            DecentralizedLogger.e(
                tag = TAG,
                message = "Fallo al iniciar la música de fondo",
                throwable = error
            )
            stopSelf()
        }
    }

    private fun pausePlayback() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            DecentralizedLogger.i(TAG, "Reproducción pausada por pérdida temporal de foco")
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                DecentralizedLogger.d(TAG, "Deteniendo reproducción")
            }
            it.stop()
            it.release()
        }
        mediaPlayer = null
        abandonAudioFocus()
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    private fun buildMediaPlayer(customUri: Uri?): MediaPlayer {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        return MediaPlayer().apply {
            setAudioAttributes(attributes)
            isLooping = true
            setVolume(0.5f, 0.5f)

            if (customUri != null) {
                DecentralizedLogger.d(TAG, "Configurando pista personalizada")
                setDataSource(applicationContext, customUri)
                prepare()
            } else {
                DecentralizedLogger.d(TAG, "Configurando pista oficial")
                val afd = resources.openRawResourceFd(R.raw.musicafondo)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
            }
        }
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(getString(R.string.music_playback_active))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setOngoing(true)
        .build()

    private fun buildFocusRequest(): Any? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.media.AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener(focusChangeListener)
                .build()
        } else {
            null
        }
    }

    private fun requestAudioFocus(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = focusRequest as android.media.AudioFocusRequest
            audioManager.requestAudioFocus(request)
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = focusRequest as? android.media.AudioFocusRequest
            if (request != null) {
                audioManager.abandonAudioFocusRequest(request)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(focusChangeListener)
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.music_channel_name),
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "MusicPlaybackService"

        // ⬇⬇ AHORA SON PÚBLICAS ⬇⬇
        const val ACTION_PLAY_OFFICIAL = "com.diegodiaz.techwizards.action.PLAY_OFFICIAL"
        const val ACTION_PLAY_CUSTOM = "com.diegodiaz.techwizards.action.PLAY_CUSTOM"
        const val ACTION_STOP = "com.diegodiaz.techwizards.action.STOP"

        private const val CHANNEL_ID = "music_channel"
        private const val NOTIFICATION_ID = 2001
    }
}
