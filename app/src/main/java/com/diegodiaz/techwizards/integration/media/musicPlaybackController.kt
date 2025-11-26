package com.diegodiaz.techwizards.integration.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger

/**
 * Controlador ligero para orquestar la reproducción de música de fondo.
 *
 * @param appContext Contexto de aplicación usado para arrancar o detener el servicio.
 * @security No registra PII; solo acciones genéricas y URIs sin datos sensibles.
 */
class musicPlaybackController(private val appContext: Context) {

    /**
     * API "amigable" para la UI: aplica un estado global de música.
     *
     * @param enabled si la música debe estar activa.
     * @param selectedUri URI *en forma de String* (persistida en settings) o `null`.
     */
    fun applySettings(enabled: Boolean, selectedUri: String?) {
        val uri = selectedUri?.let { Uri.parse(it) }
        toggleMusic(enabled, uri)
    }

    /**
     * Activa o desactiva la música delegando en el servicio nativo.
     */
    fun toggleMusic(enabled: Boolean, preferredUri: Uri? = null) {
        DecentralizedLogger.i(
            TAG,
            "toggleMusic enabled=$enabled hasCustomUri=${preferredUri != null}"
        )
        if (enabled) {
            preferredUri?.let { playCustom(it) } ?: playOfficial()
        } else {
            stop()
        }
    }

    fun playOfficial() {
        DecentralizedLogger.d(TAG, "Iniciando música oficial")
        val intent = Intent(appContext, musicPlaybackService::class.java).apply {
            action = musicPlaybackService.ACTION_PLAY_OFFICIAL
        }
        appContext.startService(intent)
    }

    fun playCustom(uri: Uri) {
        DecentralizedLogger.d(TAG, "Iniciando pista personalizada para música de fondo")
        val intent = Intent(appContext, musicPlaybackService::class.java).apply {
            action = musicPlaybackService.ACTION_PLAY_CUSTOM
            data = uri
        }
        appContext.startService(intent)
    }

    fun stop() {
        DecentralizedLogger.i(TAG, "Deteniendo música de fondo")
        val intent = Intent(appContext, musicPlaybackService::class.java).apply {
            action = musicPlaybackService.ACTION_STOP
        }
        appContext.stopService(intent)
    }

    private companion object {
        private const val TAG = "MusicPlaybackController"
    }
}
