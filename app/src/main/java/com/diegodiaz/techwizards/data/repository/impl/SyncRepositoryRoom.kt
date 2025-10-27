package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.*
import com.diegodiaz.techwizards.data.local.entity.*
import com.diegodiaz.techwizards.domain.model.*
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import io.reactivex.rxjava3.core.Completable
import kotlinx.coroutines.rx3.await

/**
 * Repositorio encargado de sincronizar datos locales (Room)
 * con un servidor remoto o nube (futuro desarrollo).
 *
 * Por ahora, su función es preparar el entorno local para
 * futuras sincronizaciones.
 */
class SyncRepositoryRoom(
    private val usuarioDao: IUsuarioDao,
    private val partidaDao: IPartidaDao,
    private val monederoDao: IMonederoDao,
    private val outboxDao: IOutboxDao
) {

    /**
     * Simula una sincronización inicial de datos.
     * En una versión futura, esto enviaría/recibiría datos del servidor.
     */
    fun sincronizarDatosRx(): Completable = Completable.fromAction {
        // Aquí iría la lógica de sincronización (ejemplo: limpiar y recargar datos)
        // Por ahora, se deja como placeholder.
    }

    /**
     * Sincronización en entorno coroutine.
     */
    suspend fun sincronizarDatos() {
        sincronizarDatosRx().await()
    }

    /**
     * Limpia la base de datos local (opcional).
     */
    fun limpiarDatosLocalesRx(): Completable =
        Completable.fromAction {
            usuarioDao.borrarTodo()
            partidaDao.borrarTodo()
            monederoDao.borrarTodo()
            outboxDao.borrarTodo()
        }

    suspend fun limpiarDatosLocales() {
        limpiarDatosLocalesRx().await()
    }
}
