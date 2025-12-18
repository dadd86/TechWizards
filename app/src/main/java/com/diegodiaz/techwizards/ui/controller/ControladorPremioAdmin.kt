package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.core.usecases.ActualizarPremioComunUseCase
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface PremioAdminUiState {
    data object Cargando : PremioAdminUiState
    data class Exito(
        val premio: CommonPrize,
        val mensaje: String? = null,
    ) : PremioAdminUiState

    data class Error(val mensaje: String) : PremioAdminUiState
}

/**
 * Controlador dedicado al flujo administrativo del premio común.
 *
 * @property scoreRepository Fuente de lectura del premio.
 * @property actualizarPremioComunUseCase Caso de uso que valida y persiste actualizaciones.
 */
class ControladorPremioAdmin(
    private val scoreRepository: ScoreRepository,
    private val actualizarPremioComunUseCase: ActualizarPremioComunUseCase,
) : ViewModel() {

    private val _ui = MutableStateFlow<PremioAdminUiState>(PremioAdminUiState.Cargando)
    val ui: StateFlow<PremioAdminUiState> = _ui

    init {
        cargarPremio()
    }

    fun cargarPremio() {
        _ui.value = PremioAdminUiState.Cargando
        viewModelScope.launch {
            runCatching { scoreRepository.obtenerPremioComun() }
                .onSuccess { premio -> _ui.value = PremioAdminUiState.Exito(premio) }
                .onFailure { _ui.value = PremioAdminUiState.Error("No se pudo cargar el premio común") }
        }
    }

    fun actualizarPremio(descripcion: String, valor: Int) {
        viewModelScope.launch {
            when (val resultado = actualizarPremioComunUseCase(descripcion, valor)) {
                is Result.Err -> _ui.value = PremioAdminUiState.Error(resultado.error.toUserMessage())
                is Result.Ok -> _ui.value = PremioAdminUiState.Exito(resultado.value, "Premio actualizado correctamente")
            }
        }
    }
}

private fun Any.toUserMessage(): String = when (this) {
    is com.diegodiaz.techwizards.core.common.AgentError.Validation -> this.reason
    else -> "Ocurrió un error inesperado"
}