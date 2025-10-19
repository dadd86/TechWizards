package com.diegodiaz.techwizards.app

import android.app.Application
import androidx.room.Room
import com.diegodiaz.techwizards.BuildConfig
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import com.diegodiaz.techwizards.util.logging.AndroidLogSink
import com.diegodiaz.techwizards.util.logging.FileLogSink
import com.diegodiaz.techwizards.util.logging.loggingDecentralizedLogger

class App : Application() {

    lateinit var db: BaseDeDatos
        private set
    lateinit var repoJuego: JuegoRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Logger centralizado
        loggingDecentralizedLogger.registerSink(AndroidLogSink())
        loggingDecentralizedLogger.registerSink(FileLogSink(this))
        loggingDecentralizedLogger.setMinLevel(if (BuildConfig.DEBUG) "DEBUG" else "INFO")
        // (Opcional) Enmascaradores PII
        // loggingDecentralizedLogger.addPiiMask(Regex("""token=[A-Za-z0-9\-_.]+"""))

        // Room
        db = Room.databaseBuilder(this, BaseDeDatos::class.java, "techwizards.db")
            .fallbackToDestructiveMigration() // TODO: reemplazar por migraciones
            .build()

        // Repo
        repoJuego = JuegoRepositoryRoom(db.iPartidaDao(), db.iMonederoDao())
    }
}
