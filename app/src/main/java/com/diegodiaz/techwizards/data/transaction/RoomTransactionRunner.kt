package com.diegodiaz.techwizards.data.transaction


import androidx.room.withTransaction
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación de [TransactionRunner] apoyada en Room.
 *
 * @property db Instancia de la base de datos local.
 * @property dispatcher Dispatcher utilizado para la operación.
 */
class RoomTransactionRunner(
    private val db: BaseDeDatos,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TransactionRunner {

    override suspend fun <T> invoke(block: suspend () -> T): T =
        withContext(dispatcher) { db.withTransaction { block() } }
}