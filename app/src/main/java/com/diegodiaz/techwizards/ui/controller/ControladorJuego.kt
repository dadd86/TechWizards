package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

data class JuegoUiState(
    val monedas: Int = 0,
    val ultimoResultado: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)

class ControladorJuego(
    private val repo: JuegoRepository,
    private val usuarioId: String
) : ViewModel() {

    private val _ui = MutableStateFlow(JuegoUiState())
    val ui: StateFlow<JuegoUiState> = _ui.asStateFlow()


    private val _historial = MutableStateFlow<List<Partida>>(emptyList())
    val historial: StateFlow<List<Partida>> = _historial.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observarSaldo(usuarioId).collect { m ->
                _ui.update { it.copy(monedas = m.monedas) }
            }
        }
    }

    fun lanzar() {
        viewModelScope.launch {
            try {
                _ui.update { it.copy(cargando = true, error = null) }

                //no hay metodo en el repo, he puesto una simulacion, no he querido tocar nada del repo
                val dado = (1..6).random()
                val gano = dado >= 4
                val delta = if (gano) 10 else -5
                val msg = if (gano) " Has ganado! (+$delta)!" else "Has perdido! ($delta)"


                _ui.update { it.copy(ultimoResultado = msg, cargando = false) }
            } catch (t: Throwable) {
                _ui.update { it.copy(cargando = false, error = t.message ?: "Error") }
            }
        }
    }
}
