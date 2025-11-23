package com.diegodiaz.techwizards.integration.media

import android.content.Context
import android.content.Intent

class musicPlaybackController(private val appContext: Context) {
    fun setEnabled(enabled: Boolean) {
        if (enabled) play() else stop()
    }
    fun play() {
        val intent = Intent(appContext, musicPlaybackService::class.java)
        appContext.startService(intent)
    }
    fun stop() {
        val intent = Intent(appContext, musicPlaybackService::class.java)
        appContext.stopService(intent)
    }
}