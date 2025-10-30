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
) {
    fun observeSaldoRx(usuarioNumero: Long): Flowable<Monedero> =
        monederoDao.observeSaldo(usuarioNumero).map { it.toDomain() }

    fun cargarUsuarioRx(usuarioNumero: Long): Maybe<Usuario> =
        usuarioDao.getByNumeroRx(usuarioNumero).map { it.toDomain() }

    fun inicializarMonedasRx(usuario: Usuario, monedasIniciales: Int): Completable =
        usuarioDao.upsert(usuario.toEntity())
            .andThen(
                monederoDao.upsert(
                    MonederoEntity(
                        id = "wallet_${usuario.numero}",
                        usuarioNumero = usuario.numero,
                        saldo = monedasIniciales
                    )
                )
            )
}
