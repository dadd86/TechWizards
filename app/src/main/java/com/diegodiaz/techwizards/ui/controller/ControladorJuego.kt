package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    val historial: StateFlow<List<Partida>> =
        repo.observarHistorial(usuarioId = usuarioId, limit = 50)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            repo.observarMonedero(usuarioId).collect { m ->
                _ui.value = _ui.value.copy(monedas = m.saldo)
            }
        }
    }

    fun lanzar() {
        viewModelScope.launch {
            try {
                _ui.value = _ui.value.copy(cargando = true, error = null)
                val p = repo.lanzarDado(usuarioId)
                val gano = p.resultado == Resultado.GANADO
                val msg = if (gano) "¡Ganaste (+${p.deltaMonedas})!" else "Perdiste (${p.deltaMonedas})"
                _ui.value = _ui.value.copy(ultimoResultado = msg, cargando = false)
            } catch (t: Throwable) {
                _ui.value = _ui.value.copy(cargando = false, error = t.message ?: "Error")
            }
        }
    }
    companion object {
        const val DEFAULT_USUARIO_ID: String = "1"
    }
}
