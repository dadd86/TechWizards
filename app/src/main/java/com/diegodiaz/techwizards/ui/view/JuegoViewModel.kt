package com.diegodiaz.techwizards.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.domain.model.Monedero
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.rx3.asFlow

class JuegoViewModel(
    private val repo: JuegoRepositoryRoom
) : ViewModel() {

    fun saldo(usuarioNumero: Long) =
        repo.observeSaldoRx(usuarioNumero)
            .asFlow() // convierte Observable en Flow
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                Monedero("wallet_$usuarioNumero", usuarioNumero, 0)
            )
}
