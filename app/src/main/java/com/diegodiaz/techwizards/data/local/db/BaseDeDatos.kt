package com.diegodiaz.techwizards.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

// DAOs
import com.diegodiaz.techwizards.data.local.dao.IUsuarioDao
import com.diegodiaz.techwizards.data.local.dao.IMonederoDao
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.dao.IEventoDao
import com.diegodiaz.techwizards.data.local.dao.IIdMapDao
import com.diegodiaz.techwizards.data.local.dao.IMatchEventDao
import com.diegodiaz.techwizards.data.local.dao.IMatchDao
import com.diegodiaz.techwizards.data.local.dao.ILobbyDao
import com.diegodiaz.techwizards.data.local.dao.IMatchScoreDao
import com.diegodiaz.techwizards.data.local.dao.ITombstoneDao
import com.diegodiaz.techwizards.data.local.dao.IMessageDao
import com.diegodiaz.techwizards.data.local.dao.IMatchParticipantDao
import com.diegodiaz.techwizards.data.local.dao.IOutboxDao

// Entities
import com.diegodiaz.techwizards.data.local.entity.UsuarioEntity
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.data.local.entity.EventoEntity
import com.diegodiaz.techwizards.data.local.entity.IdMapEntity
import com.diegodiaz.techwizards.data.local.entity.MatchEventEntity
import com.diegodiaz.techwizards.data.local.entity.MatchEntity
import com.diegodiaz.techwizards.data.local.entity.LobbyEntity
import com.diegodiaz.techwizards.data.local.entity.MatchScoreEntity
import com.diegodiaz.techwizards.data.local.entity.TombstoneEntity
import com.diegodiaz.techwizards.data.local.entity.MessageEntity
import com.diegodiaz.techwizards.data.local.entity.MatchParticipantEntity
import com.diegodiaz.techwizards.data.local.entity.OutboxEntity


import com.diegodiaz.techwizards.data.local.EnumConverters

/**
 * Configuración principal de la base de datos Room.
 *
 * @security
 * - Exporta el esquema para auditoría y aplica pragmas definidos en `PrimerSQL.sql`.
 * - Ejecuta migraciones incrementales para evitar pérdidas de datos.
 */
@Database(
    version = 1, // Primera ejecución: si cambias el esquema más adelante, sube versión y añade Migration.
    exportSchema = true,
    entities = [
        // Básicas P1
        UsuarioEntity::class,
        MonederoEntity::class,
        PartidaEntity::class,

        // Evento / Lobby / Match
        EventoEntity::class,
        LobbyEntity::class,
        MatchEntity::class,
        MatchEventEntity::class,
        MatchParticipantEntity::class,
        MatchScoreEntity::class,

        // Mensajería
        MessageEntity::class,

        // Sync
        OutboxEntity::class,
        IdMapEntity::class,
        TombstoneEntity::class
    ],
    //exportSchema = true
)
@TypeConverters(EnumConverters::class)
abstract class BaseDeDatos : RoomDatabase() {

    //  DAOs básicos
    abstract fun usuarioDao(): IUsuarioDao
    abstract fun monederoDao(): IMonederoDao
    abstract fun partidaDao(): IPartidaDao

    //  Resto de DAOs
    abstract fun eventoDao(): IEventoDao
    abstract fun lobbyDao(): ILobbyDao
    abstract fun matchDao(): IMatchDao
    abstract fun matchEventDao(): IMatchEventDao
    abstract fun matchParticipantDao(): IMatchParticipantDao
    abstract fun matchScoreDao(): IMatchScoreDao

    abstract fun messageDao(): IMessageDao

    abstract fun outboxDao(): IOutboxDao
    abstract fun idMapDao(): IIdMapDao
    abstract fun tombstoneDao(): ITombstoneDao

    companion object {
        @Volatile
        private var inst: BaseDeDatos? = null

        fun get(ctx: Context): BaseDeDatos =
            inst ?: synchronized(this) {
                inst ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    BaseDeDatos::class.java,
                    "techwizards.db"
                )
                    .addCallback(RoomCallbackPragmas())
                    // .fallbackToDestructiveMigration() //Solo usar en desarrollo sino perderemos datos
                    .build()
                    .also { inst = it }
            }
    }
}


