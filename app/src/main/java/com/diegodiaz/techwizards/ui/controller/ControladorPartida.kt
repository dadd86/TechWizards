package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModelProvider
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import com.diegodiaz.techwizards.domain.model.formatoResumen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de partida.
 *
 * @property nombreJugador Alias vigente del jugador.
 * @property monedas Saldo mostrado en la UI.
 * @property numeroElegido Último número seleccionado para lanzar el dado.
 * @property ultimoResultado Resumen textual del último resultado registrado.
 * @property cargando Indicador de progreso para operaciones en curso.
 * @property error Mensaje de error a mostrar en pantalla.
 * @security
 * - No exponer identificadores sensibles en `error` sin redactarlos.
 */
data class JuegoUiState(
    val nombreJugador: String = "",
    val monedas: Int = 100,
    val numeroElegido: Int? = null,
    val ultimoResultado: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)


/**
 * ViewModel que orquesta la experiencia de juego del dado.
 */
class ControladorPartida (
    private val repo: JuegoRepository,
    private val usuarioId: String
) : ViewModel() {

    val ui: StateFlow<JuegoUiState> = combine(
        repo.observarMonedero(usuarioId),
        repo.observarHistorial(usuarioId),
        repo.observarUsuario(usuarioId)
        ) { monedero, historial, usuario ->
        val alias = usuario?.alias ?: historial.firstOrNull()?.nombreJugador.orEmpty()
        JuegoUiState(
            nombreJugador = alias,
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
