package com.diegodiaz.techwizards.core

import android.content.Context
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.data.repository.impl.MatchRepositoryRoom
import com.diegodiaz.techwizards.data.repository.impl.UsuarioRepositoryRoom
import com.diegodiaz.techwizards.data.transaction.RoomTransactionRunner
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import com.diegodiaz.techwizards.domain.repository.MatchRepository
import com.diegodiaz.techwizards.domain.repository.UsuarioRepository


object ServiceLocator {

    private lateinit var appContext: Context

    // --- DB & DAOs ---
    private val db by lazy { BaseDeDatos.get(appContext) }

    private val usuarioDao by lazy { db.usuarioDao() }
    private val monederoDao by lazy { db.monederoDao() }
    private val partidaDao by lazy { db.partidaDao() }
    private val matchDao by lazy { db.matchDao() }
    private val matchEventDao by lazy { db.matchEventDao() }
    private val matchScoreDao by lazy { db.matchScoreDao() }

    private val transactionRunner by lazy { RoomTransactionRunner(db) }

    // --- Repos ---
    val juegoRepository: JuegoRepository by lazy {
        JuegoRepositoryRoom(
            usuarioDao = usuarioDao,
            monederoDao = monederoDao,
            partidaDao = partidaDao,
            transactionRunner = transactionRunner
        )
    }

    val usuarioRepository: UsuarioRepository by lazy {
        UsuarioRepositoryRoom(
            usuarioDao = usuarioDao,
            monederoDao = monederoDao
        )
    }

    val matchRepository: MatchRepository by lazy {
        MatchRepositoryRoom(
            matchDao = matchDao,
            matchEventDao = matchEventDao,
            matchScoreDao = matchScoreDao
        )
    }

    // Llamar una vez al arrancar la app
    fun init(context: Context) {
        appContext = context.applicationContext
    }
}