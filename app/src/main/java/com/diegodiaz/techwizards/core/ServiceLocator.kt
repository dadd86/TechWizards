package com.diegodiaz.techwizards.core

import android.content.Context
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.data.repository.impl.MatchRepositoryRoom

object ServiceLocator {

    private lateinit var appContext: Context

    // --- DB & DAOs ---
    private val db by lazy { BaseDeDatos.get(appContext) }

    private val usuarioDao by lazy { db.usuarioDao() }
    private val monederoDao by lazy { db.monederoDao() }
    private val partidaDao by lazy { db.partidaDao() }

    // --- Repos ---
    val juegoRepository by lazy {
        JuegoRepositoryRoom(
            usuarioDao = usuarioDao,
            monederoDao = monederoDao,
            partidaDao = partidaDao
        )
    }

    val matchRepository by lazy {
        MatchRepositoryRoom(
            partidaDao = partidaDao,
            monederoDao = monederoDao
        )
    }

    // Llamar una vez al arrancar la app
    fun init(context: Context) {
        appContext = context.applicationContext
    }
}