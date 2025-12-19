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
        val mensaje: PremioAdminMensaje? = null,
    ) : PremioAdminUiState

    data class Error(val mensaje: String) : PremioAdminUiState
}
data class PremioAdminMensaje(val texto: String, val esError: Boolean)

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
    private var ultimoPremio: CommonPrize? = null

    init {
        cargarPremio()
    }

    fun cargarPremio() {
        _ui.value = PremioAdminUiState.Cargando
        viewModelScope.launch {
            runCatching { scoreRepository.obtenerPremioComun() }
                .onSuccess { premio ->
                    ultimoPremio = premio
                    _ui.value = PremioAdminUiState.Exito(premio)
                }
                .onFailure { _ui.value = PremioAdminUiState.Error("No se pudo cargar el premio común") }
        }
    }

    fun actualizarPremio(descripcion: String, valor: Int) {
        viewModelScope.launch {
            when (val resultado = actualizarPremioComunUseCase(descripcion, valor)) {
                is Result.Err -> {
                    val premioActual = ultimoPremio
                    if (premioActual != null) {
                        _ui.value = PremioAdminUiState.Exito(
                            premio = premioActual,
                            mensaje = PremioAdminMensaje(resultado.error.toUserMessage(), esError = true)
                        )
                    } else {
                        _ui.value = PremioAdminUiState.Error(resultado.error.toUserMessage())
                    }
                }

                is Result.Ok -> {
                    ultimoPremio = resultado.value
                    _ui.value = PremioAdminUiState.Exito(
                        resultado.value,
                        PremioAdminMensaje("Premio actualizado correctamente", esError = false)
                    )
                }
            }
        }
    }
}

private fun Any.toUserMessage(): String = when (this) {
    is com.diegodiaz.techwizards.core.common.AgentError.Validation -> this.reason
    else -> "Ocurrió un error inesperado"
}