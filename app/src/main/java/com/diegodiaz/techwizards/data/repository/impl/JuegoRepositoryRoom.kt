package com.diegodiaz.techwizards.data.repository.impl

import com.diegodiaz.techwizards.data.local.dao.*
import com.diegodiaz.techwizards.data.local.entity.MonederoEntity
import com.diegodiaz.techwizards.data.local.entity.PartidaEntity
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.data.local.mapper.toDomain
import com.diegodiaz.techwizards.data.local.mapper.toEntity
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.domain.model.Partida
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow

class JuegoRepositoryRoom(
    private val usuarioDao: IUsuarioDao,
    private val monederoDao: IMonederoDao,
    private val partidaDao: IPartidaDao

) : JuegoRepository {
    override fun observeSaldoRx(usuarioId: String): Flowable<Monedero> =
        monederoDao.observeSaldo(usuarioId.toLong()).map { it.toDomain() }

    override fun cargarUsuarioRx(usuarioId: String): Maybe<Usuario> =
        usuarioDao.getByNumeroRx(usuarioId.toLong()).map { it.toDomain() }

    override fun inicializarMonedasRx(usuario: Usuario, monedasIniciales: Int): Completable =
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

    // ------- Coroutines ---------
    override fun observarSaldo(usuarioId: String): Flow<Monedero> =
        monederoDao.observeSaldo(usuarioId.toLong()).asFlow().map { it.toDomain() }

    override suspend fun inicializarMonedas(usuario: Usuario, monedasIniciales: Int) {
        throw NotImplementedError("Implementar inicializarMonedas")
    }

    override fun observarHistorial(usuarioId: String, limit: Int): Flow<List<Partida>> {
        return partidaDao.observarHistorial(usuarioId.toLong(), limit)
            .map { lista: List<PartidaEntity> -> lista.map { it.toDomain() } }
    }

    override fun observarMonedero(usuarioId: String): Flow<Monedero> {
        return monederoDao.observeSaldo(usuarioId.toLong()).asFlow().map { it.toDomain() }
    }

    override suspend fun lanzarDado(usuarioId: String): Partida {
        val usuarioNumero = usuarioId.toLong()
        val monedero = monederoDao.getMonederoSimple(usuarioNumero)
        var saldo = monedero?.saldo ?: 0

        val dado = (1..6).random()
        val gano = dado == 6
        val cambioMonedas = if (gano) 30 else -10
        // Limita el saldo a mínimo 0
        saldo = maxOf(saldo + cambioMonedas, 0)

        val partidaEntity = PartidaEntity(
            usuarioNumero = usuarioNumero,
            fecha = System.currentTimeMillis(),
            resultado = if (gano) Resultado.GANADO else Resultado.PERDIDO,
            cambioMonedas = cambioMonedas
        )
        val partidaId = partidaDao.insert(partidaEntity)

        monederoDao.actualizarSaldo(usuarioNumero, saldo)

        return partidaEntity.copy(id = partidaId).toDomain()
    }

}
