package com.diegodiaz.techwizards.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration

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
import com.diegodiaz.techwizards.data.local.dao.IVictoryLocationDao

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
import com.diegodiaz.techwizards.data.local.entity.VictoryLocationEntity


import com.diegodiaz.techwizards.data.local.EnumConverters

/**
 * Configuración principal de la base de datos Room.
 *
 * @security
 * - Exporta el esquema para auditoría y aplica pragmas definidos en `PrimerSQL.sql`.
 * - Ejecuta migraciones incrementales para evitar pérdidas de datos.
 */
@Database(
    version = 4, // Incrementa para ajustar PK manual en Usuario.
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
        TombstoneEntity::class,
        VictoryLocationEntity::class
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
    abstract fun victoryLocationDao(): IVictoryLocationDao

    companion object {
        @Volatile
        private var inst: BaseDeDatos? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    ALTER TABLE Partida
                    ADD COLUMN nombreJugador TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    UPDATE Partida
                    SET nombreJugador = COALESCE(
                        (SELECT usuario FROM Usuario WHERE Usuario.numero = Partida.usuarioNumero),
                        ''
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `evento` (
                        `id` TEXT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `descripcion` TEXT NOT NULL,
                        `fechaInicio` INTEGER NOT NULL,
                        `fechaFin` INTEGER NOT NULL,
                        `completado` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `Lobby` (
                        `nombre` TEXT NOT NULL,
                        `id` TEXT NOT NULL,
                        `codigo` TEXT,
                        `modo` TEXT NOT NULL,
                        `estado` TEXT NOT NULL,
                        `creadorNum` INTEGER NOT NULL,
                        `createdAtMs` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`creadorNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Lobby_estado` ON `Lobby` (`estado`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_Lobby_codigo` ON `Lobby` (`codigo`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Lobby_creadorNum` ON `Lobby` (`creadorNum`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Lobby_estado_createdAtMs` ON `Lobby` (`estado`, `createdAtMs`)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `Match` (
                        `id` TEXT NOT NULL,
                        `lobbyId` TEXT,
                        `modo` TEXT NOT NULL,
                        `estado` TEXT NOT NULL,
                        `createdByNum` INTEGER NOT NULL,
                        `createdAtMs` INTEGER NOT NULL,
                        `startedAtMs` INTEGER,
                        `finishedAtMs` INTEGER,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`lobbyId`) REFERENCES `Lobby`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL,
                        FOREIGN KEY(`createdByNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Match_estado_createdAtMs` ON `Match` (`estado`, `createdAtMs`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Match_lobbyId` ON `Match` (`lobbyId`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Match_createdByNum` ON `Match` (`createdByNum`)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `MatchEvent` (
                        `id` TEXT NOT NULL,
                        `matchId` TEXT NOT NULL,
                        `seq` INTEGER NOT NULL,
                        `type` TEXT NOT NULL,
                        `actorNum` INTEGER NOT NULL,
                        `payloadJson` TEXT,
                        `createdAtMs` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`actorNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_MatchEvent_matchId_seq` ON `MatchEvent` (`matchId`, `seq`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_MatchEvent_actorNum` ON `MatchEvent` (`actorNum`)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `MatchParticipant` (
                        `matchId` TEXT NOT NULL,
                        `usuarioNum` INTEGER NOT NULL,
                        `rol` TEXT,
                        `teamId` TEXT,
                        `joinedAtMs` INTEGER NOT NULL,
                        `leftAtMs` INTEGER,
                        `score` INTEGER NOT NULL,
                        PRIMARY KEY(`matchId`, `usuarioNum`),
                        FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`usuarioNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_MatchParticipant_matchId` ON `MatchParticipant` (`matchId`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_MatchParticipant_usuarioNum` ON `MatchParticipant` (`usuarioNum`)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `MatchScore` (
                        `matchId` TEXT NOT NULL,
                        `usuarioNum` INTEGER NOT NULL,
                        `score` INTEGER NOT NULL,
                        PRIMARY KEY(`matchId`, `usuarioNum`),
                        FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`usuarioNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_MatchScore_usuarioNum` ON `MatchScore` (`usuarioNum`)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `Message` (
                        `id` TEXT NOT NULL,
                        `matchId` TEXT NOT NULL,
                        `senderNum` INTEGER NOT NULL,
                        `text` TEXT NOT NULL,
                        `createdAtMs` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`matchId`) REFERENCES `Match`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`senderNum`) REFERENCES `Usuario`(`numero`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Message_matchId_createdAtMs` ON `Message` (`matchId`, `createdAtMs`)
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_Message_senderNum` ON `Message` (`senderNum`)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `Outbox` (
                        `operationId` TEXT NOT NULL,
                        `entityType` TEXT NOT NULL,
                        `entityId` TEXT NOT NULL,
                        `op` TEXT NOT NULL,
                        `payloadJson` TEXT NOT NULL,
                        `attempt` INTEGER NOT NULL,
                        `lastError` TEXT,
                        `createdAtMs` INTEGER NOT NULL,
                        `updatedAtMs` INTEGER NOT NULL,
                        PRIMARY KEY(`operationId`)
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `IdMap` (
                        `localTable` TEXT NOT NULL,
                        `localId` TEXT NOT NULL,
                        `remoteCollection` TEXT NOT NULL,
                        `remoteId` TEXT NOT NULL,
                        PRIMARY KEY(`localTable`, `localId`)
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_IdMap_remoteCollection_remoteId` ON `IdMap` (`remoteCollection`, `remoteId`)
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `Tombstone` (
                        `tableName` TEXT NOT NULL,
                        `entityId` TEXT NOT NULL,
                        `deletedAtMs` INTEGER NOT NULL,
                        PRIMARY KEY(`tableName`, `entityId`)
                    )
                    """.trimIndent()
                )
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""DROP TABLE IF EXISTS `VictoryLocation`""")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `victory_location` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `latitude` REAL NOT NULL,
                        `longitude` REAL NOT NULL,
                        `accuracyMetres` REAL,
                        `capturedAtMs` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("PRAGMA foreign_keys=OFF")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `Usuario_new` (
                        `numero` INTEGER NOT NULL,
                        `usuario` TEXT NOT NULL,
                        `fechaAlta` INTEGER NOT NULL,
                        `monedas` INTEGER NOT NULL,
                        `gano` INTEGER NOT NULL,
                        `firebaseUid` TEXT,
                        PRIMARY KEY(`numero`)
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    INSERT INTO `Usuario_new` (`numero`, `usuario`, `fechaAlta`, `monedas`, `gano`, `firebaseUid`)
                    SELECT `numero`, `usuario`, `fechaAlta`, `monedas`, `gano`, `firebaseUid`
                    FROM `Usuario`
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE `Usuario`")
                database.execSQL("ALTER TABLE `Usuario_new` RENAME TO `Usuario`")
                database.execSQL("PRAGMA foreign_keys=ON")
            }
        }

        fun get(ctx: Context): BaseDeDatos =
            inst ?: synchronized(this) {
                inst ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    BaseDeDatos::class.java,
                    "techwizards.db"
                )
                    .addCallback(RoomCallbackPragmas())
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { inst = it }
            }
    }
}