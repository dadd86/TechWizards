package com.diegodiaz.techwizards.data.local.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class RoomCallbackPragmas : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Activa integridad referencial en SQLite (foreign keys)
        db.execSQL("PRAGMA foreign_keys=ON;")
    }
}