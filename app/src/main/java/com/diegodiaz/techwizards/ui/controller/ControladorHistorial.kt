package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.core.usecases.ObservarHistorialRemotoUseCase
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de historial de partidas.
 */
class ControladorHistorial(
    private val observarHistorialRemotoUseCase: ObservarHistorialRemotoUseCase,
    private val firebaseUidProvider: () -> String?,
    private val limite: Int = 50,
) : ViewModel() {

    private val _historial = MutableStateFlow<List<Partida>>(emptyList())
    val historial: StateFlow<List<Partida>> =
        _historial.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            when (val resultado = observarHistorialRemotoUseCase(firebaseUidProvider(), limite)) {
                is Result.Ok -> _historial.value = resultado.value
                is Result.Err -> {
                    _historial.value = emptyList()
                    DecentralizedLogger.e(
                        "ControladorHistorial",
                        "No se pudo cargar historial remoto: ${resultado.error}"
                    )
                }
            }
        }
    }
}