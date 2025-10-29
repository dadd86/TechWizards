package com.diegodiaz.techwizards.data.local.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Configura PRAGMAs requeridos por el esquema manual.
 *
 * @security
 * - Enforce foreign_keys y WAL para garantizar durabilidad.
 */
object PragmaCallback : RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON")
        db.execSQL("PRAGMA journal_mode=WAL")
        db.execSQL("PRAGMA synchronous=FULL")
    }
}