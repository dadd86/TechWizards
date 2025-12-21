package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.domain.model.CommonPrize
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface RankingUiState {
    data object Cargando : RankingUiState
    data class Exito(
        val topTen: List<LeaderboardEntry>,
        val premio: CommonPrize?,
        val actualizadoEnMs: Long,
        val puedeActualizarPremio: Boolean,
        val mensajeError: String? = null
    ) : RankingUiState

    data class Error(val mensaje: String) : RankingUiState
}

/**
 * Gestiona la pantalla de ranking y premio común.
 *
 * @property scoreRepository Fuente remota via Retrofit.
 * @property sessionManager Control de sesión para operaciones con token.
 * @security
 * No registra tokens ni datos sensibles en logs; solo alias públicos.
 */
class ControladorRanking(
    private val scoreRepository: ScoreRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<RankingUiState>(RankingUiState.Cargando)
    val uiState: StateFlow<RankingUiState> = _uiState

    init {
        refrescarTodo()
    }

    fun refrescarTodo() {
        val estadoPrevio = _uiState.value as? RankingUiState.Exito
        _uiState.value = RankingUiState.Cargando
        viewModelScope.launch {
            runCatching {
                val topTen = scoreRepository.obtenerTopTen()
                DecentralizedLogger.d(
                    "ControladorRanking",
                    "TopTen recibido con ${topTen.size} entradas"
                )

                val premio = runCatching { scoreRepository.obtenerPremioComun() }
                    .onFailure { error ->
                        DecentralizedLogger.e(
                            "ControladorRanking",
                            "No se pudo refrescar premio",
                            error
                        )
                    }
                    .getOrElse { estadoPrevio?.premio }

                _uiState.value = RankingUiState.Exito(
                    topTen = topTen,
                    premio = premio,
                    actualizadoEnMs = System.currentTimeMillis(),
                    puedeActualizarPremio = sessionManager.session.value?.isAdmin == true,
                    mensajeError = null
                )
            }.onFailure { error ->
                DecentralizedLogger.e("ControladorRanking", "Error al cargar ranking", error)

                // Fallback para demo
                val dummy = List(5) { i ->
                    LeaderboardEntry(
                        id = "dummy-$i",
                        alias = "Jugador ${i + 1}",
                        score = (10 - i) * 5,
                        position = i + 1
                    )
                }

                _uiState.value = RankingUiState.Exito(
                    topTen = dummy,
                    premio = null,
                    actualizadoEnMs = System.currentTimeMillis(),
                    puedeActualizarPremio = sessionManager.session.value?.isAdmin == true,
                    mensajeError = "No se pudo contactar con el servidor; mostrando datos de ejemplo"
                )
            }
        }
    }

    fun refrescarPremio() {
        val estadoActual = _uiState.value
        if (estadoActual !is RankingUiState.Exito) return
        viewModelScope.launch {
            runCatching { scoreRepository.obtenerPremioComun() }
                .onSuccess { premioActualizado ->
                    _uiState.value = estadoActual.copy(
                        premio = premioActualizado,
                        actualizadoEnMs = System.currentTimeMillis(),
                        mensajeError = null
                    )
                }
                .onFailure { error ->
                    DecentralizedLogger.e("ControladorRanking", "No se pudo refrescar premio", error)
                    _uiState.value = estadoActual.copy(
                        mensajeError = "No pudimos refrescar el premio común"
                    )
                }
        }
    }

    fun actualizarPremio(descripcion: String, valor: Int) {
        val session = sessionManager.session.value
        if (session == null) {
            _uiState.value = RankingUiState.Error("Inicia sesión para actualizar el premio")
            return
        }
        if (!session.isAdmin) {
            val estadoActual = _uiState.value as? RankingUiState.Exito
            val mensaje = "Solo admin puede actualizar el premio común"
            _uiState.value = estadoActual?.copy(mensajeError = mensaje)
                ?: RankingUiState.Error(mensaje)
            return
        }
        viewModelScope.launch {
            runCatching {
                scoreRepository.actualizarPremioComun(
                    session = session,
                    nuevoPremio = CommonPrize(descripcion = descripcion, valor = valor)
                )
            }.onSuccess { premio ->
                val top = (_uiState.value as? RankingUiState.Exito)?.topTen.orEmpty()
                _uiState.value = RankingUiState.Exito(
                    topTen = top,
                    premio = premio,
                    actualizadoEnMs = System.currentTimeMillis(),
                    puedeActualizarPremio = session.isAdmin,
                    mensajeError = null
                )
            }.onFailure { error ->
                DecentralizedLogger.e("ControladorRanking", "No se pudo actualizar premio", error)
                val estadoActual = _uiState.value as? RankingUiState.Exito
                val mensaje = when (error) {
                    is retrofit2.HttpException -> {
                        if (error.code() == 403) {
                            "Solo admin puede actualizar el premio común"
                        } else {
                            "Error actualizando premio común"
                        }
                    }
                    else -> "Error actualizando premio común"
                }
                _uiState.value = estadoActual?.copy(
                    mensajeError = mensaje
                ) ?: RankingUiState.Error(mensaje)
            }
        }
    }
}
