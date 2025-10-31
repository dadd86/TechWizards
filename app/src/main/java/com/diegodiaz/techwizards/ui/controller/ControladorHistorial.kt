package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para la pantalla de historial de partidas.
 */
class ControladorHistorial(
    private val repository: JuegoRepository,
    private val usuarioId: String,
    private val limite: Int = 100,
) : ViewModel() {

    val historial: StateFlow<List<Partida>> =
        repository.observarHistorial(usuarioId, limite)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}