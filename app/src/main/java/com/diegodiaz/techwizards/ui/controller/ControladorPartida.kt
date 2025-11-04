package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class JuegoUiState(
    val monedas: Int = 100,
    val numeroElegido: Int? = null,
    val ultimoResultado: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)

/**
 * Devuelve un texto descriptivo del resultado más reciente incorporando el alias del jugador.
 */
fun Partida.formatoResumen(): String = when (resultado) {
    Resultado.GANADO -> "${aliasJugador} ganó (+$deltaMonedas)"
    Resultado.PERDIDO -> "${aliasJugador} perdió ($deltaMonedas)"
}

class ControladorPartida (
    private val repo: JuegoRepository,
    private val usuarioId: String
) : ViewModel() {

    val ui: StateFlow<JuegoUiState> = combine(
        repo.observarMonedero(usuarioId),
        repo.observarHistorial(usuarioId)
    ) { monedero: Monedero, historial: List<Partida> ->
        JuegoUiState(
            monedas = monedero.saldo,
            ultimoResultado = historial.firstOrNull()?.formatoResumen() ?: "",
            cargando = false,
            error = null
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), JuegoUiState())

    val historial: StateFlow<List<Partida>> =
        repo.observarHistorial(usuarioId = usuarioId, limit = 50)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun lanzar() {
        viewModelScope.launch {
            try {
                val partida = repo.lanzarDado(usuarioId)
            } catch (t: Throwable) {
            }
        }
    }

    fun elegirNumero(num: Int) {
        viewModelScope.launch {
            if (num in 1..6) {
                // Simulación de lanzar dado, usar lógica real de tu dominio
                repo.lanzarDado(usuarioId) // actualiza BD, los Flows cambian el UI
            }
        }
    }
}

class ControladorPartidaFactory(
    private val repo: JuegoRepository,
    private val usuarioId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ControladorPartida::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ControladorPartida(repo, usuarioId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
