package com.diegodiaz.techwizards.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.diegodiaz.techwizards.data.local.dao.*
import com.diegodiaz.techwizards.data.local.entity.*

@Database(
    version = 1,
    exportSchema = true,
    entities = [
        UsuarioEntity::class,
        MonederoEntity::class,
        PartidaEntity::class
    ]
)
abstract class BaseDeDatos : RoomDatabase() {

    abstract fun usuarioDao(): IUsuarioDao
    abstract fun monederoDao(): IMonederoDao
    abstract fun partidaDao(): IPartidaDao

    companion object {
        @Volatile private var inst: BaseDeDatos? = null

        fun get(ctx: Context): BaseDeDatos =
            inst ?: synchronized(this) {
                inst ?: Room.databaseBuilder(
                    ctx.applicationContext,
                    BaseDeDatos::class.java,
                    "techwizards.db"
                )
                    .addCallback(RoomCallbackPragmas())
                    .build()
                    .also { inst = it }
            }
    }
}