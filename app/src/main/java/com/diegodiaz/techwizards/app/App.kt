package com.diegodiaz.techwizards.app

import android.app.Application
import com.diegodiaz.techwizards.util.logging.AndroidLogSink
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import com.diegodiaz.techwizards.util.logging.FileLogSink
import com.diegodiaz.techwizards.core.ServiceLocator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DecentralizedLogger.registerSink(AndroidLogSink())
        DecentralizedLogger.registerSink(FileLogSink(this))
        DecentralizedLogger.addPiiMask(Regex("[0-9a-fA-F-]{6,}"))
        ServiceLocator.init(this)
    }
}