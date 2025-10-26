package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.IMonederoDao
import com.diegodiaz.techwizards.data.local.dao.IUsuarioDao
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Usuario
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.await

class JuegoRepositoryRoom(
    private val usuarioDao: IUsuarioDao,
    private val monederoDao: IMonederoDao
    // Si usas transacciones, inyecta tu TransactionRunner
) {
    // -------- Rx nativo (Producto 1) --------
    fun observeSaldoRx(usuarioId: String): Flowable<Monedero> =
        monederoDao.observeSaldo(usuarioId).map { it.toDomain() }

    fun cargarUsuarioRx(usuarioId: String): Maybe<Usuario> =
        usuarioDao.getById(usuarioId).map { it.toDomain() }

    fun inicializarMonedasRx(usuario: Usuario, monedasIniciales: Int): Completable =
        usuarioDao.upsert(usuario.toEntity())
            .andThen(
                monederoDao.upsert(
                    MonederoEntity(
                        id = "wallet_${usuario.id}",
                        usuarioId = usuario.id,
                        saldo = monedasIniciales
                    )
                )
            )

    // -------- Wrappers coroutines (opcional) --------
    fun observarSaldo(usuarioId: String): Flow<Monedero> =
        observeSaldoRx(usuarioId).asFlow<Monedero>()

    suspend fun inicializarMonedas(usuario: Usuario, monedasIniciales: Int) {
        inicializarMonedasRx(usuario, monedasIniciales).await()
    }
}