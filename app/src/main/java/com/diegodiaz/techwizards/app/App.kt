package com.diegodiaz.techwizards.app

import android.app.Application
import com.diegodiaz.techwizards.core.ServiceLocator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ServiceLocator.init(this)
    }
}