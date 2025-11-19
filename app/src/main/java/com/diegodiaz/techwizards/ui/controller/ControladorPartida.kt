package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import com.diegodiaz.techwizards.integration.victory.VictoryCelebrationPayload
import com.diegodiaz.techwizards.integration.victory.VictoryCelebrationService
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Estado observable que alimenta la UI de la partida.
 *
 * @property monedas Saldo actual del monedero.
 * @property numeroElegido Último número escogido por el jugador.
 * @property ultimoResultado Resumen textual del último lanzamiento.
 * @property cargando Indicador de operaciones en curso.
 * @property error Mensaje de error para mostrar en UI.
 * @property animationsEnabled Controla si se ejecutan animaciones.
 * @property sfxEnabled Controla si se reproducen sonidos.
 * @security
 * - Solo incluye alias y datos de juego, nunca credenciales.
 */
data class JuegoUiState(
    val monedas: Int = 100,
    val numeroElegido: Int? = null,
    val ultimoResultado: String = "",
    val cargando: Boolean = false,
    val error: String? = null,
    val animationsEnabled: Boolean = true,
    val sfxEnabled: Boolean = true
)

/**
 * Eventos de un solo uso que la UI debe manejar (p.ej. victoria).
 *
 * @security
 * - Transporta únicamente datos de juego ya visibles para el usuario.
 */
sealed interface JuegoUiEvent {
    data class Victoria(val partida: Partida) : JuegoUiEvent
}

private fun Partida.formatoResumen(): String = when (resultado) {
    Resultado.GANADO -> "${aliasJugador} ganó (+$deltaMonedas)"
    Resultado.PERDIDO -> "${aliasJugador} perdió ($deltaMonedas)"
}

private val defaultSettings = GameSettings(
    musicEnabled = true,
    sfxEnabled = true,
    darkThemeEnabled = false,
    animationsEnabled = true,
    notificationsEnabled = true,
    selectedMusicUri = null,
    selectedLanguageTag = "es-ES"
)

/**
 * ViewModel que coordina acciones de partida y expone estado reactivo para la UI.
 *
 * @param repo Repositorio con acceso a Room.
 * @param usuarioId Identificador del jugador en formato String.
 * @param observarPreferencias Use case para obtener las preferencias multimedia.
 * @param victoryService Servicio de integración que dispara galería/calendario/notificación.
 * @security
 * - No almacena secretos, solo id locales y alias.
 */
class ControladorPartida(
    private val repo: JuegoRepository,
    private val usuarioId: String,
    observarPreferencias: ObservarPreferenciasUseCase,
    private val victoryService: VictoryCelebrationService // NUEVO (Victory)
) : ViewModel() {

    private val preferencias: StateFlow<GameSettings> = observarPreferencias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaultSettings)

    /** Estado compuesto que sincroniza saldo, historial y preferencias. */
    val ui: StateFlow<JuegoUiState> = combine(
        repo.observarMonedero(usuarioId),
        repo.observarHistorial(usuarioId),
        preferencias
    ) { monedero: Monedero, historial: List<Partida>, settings: GameSettings ->
        JuegoUiState(
            monedas = monedero.saldo,
            ultimoResultado = historial.firstOrNull()?.formatoResumen() ?: "",
            cargando = false,
            error = null,
            animationsEnabled = settings.animationsEnabled,
            sfxEnabled = settings.sfxEnabled
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JuegoUiState())

    /** Historial reactivo de partidas recientes. */
    val historial: StateFlow<List<Partida>> =
        repo.observarHistorial(usuarioId = usuarioId, limit = 50)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _eventos = MutableSharedFlow<JuegoUiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventos: SharedFlow<JuegoUiEvent> = _eventos

    /**
     * Lanza un dado virtual actualizando el historial y el saldo.
     *
     * @return Unit porque la respuesta llega vía Flows observados por la UI.
     * @throws IllegalStateException Propaga errores al obtener el usuario inexistente.
     * @security
     * - No registra PII; delega en el repositorio la persistencia del alias.
     */
    fun lanzar() {
        viewModelScope.launch {
            try {
                val partida = repo.lanzarDado(usuarioId)
                handleVictoriaSiNecesario(partida, origen = "lanzar")
            } catch (t: Throwable) {
                DecentralizedLogger.e(TAG, "Error al lanzar", t)
            }
        }
    }

    /**
     * Permite al jugador elegir un número y dispara el lanzamiento cuando es válido.
     *
     * @param num Número seleccionado por el usuario.
     * @return Unit porque delega la actualización al repositorio.
     * @throws IllegalArgumentException No lanza excepciones; números inválidos se ignoran.
     * @security
     * - Solo procesa identificadores y saldos locales.
     */
    fun elegirNumero(num: Int) {
        viewModelScope.launch {
            if (num in 1..6) {
                val partida = repo.lanzarDado(usuarioId)
                handleVictoriaSiNecesario(partida, origen = "elegirNumero($num)")
            }
        }
    }

    // Lógica común para manejar victoria
    private suspend fun handleVictoriaSiNecesario(partida: Partida, origen: String) {
        if (partida.resultado == Resultado.GANADO) {
            DecentralizedLogger.i(TAG, "Victoria detectada origen=$origen")

            // 1) Evento para la UI (ya lo tenías)
            _eventos.tryEmit(JuegoUiEvent.Victoria(partida))

            // 2) Disparar integración: galería + calendario + notificación
            //    (usa solo datos de juego, nada sensible)
            val payload = VictoryCelebrationPayload(
                playerName = partida.aliasJugador,
                coinsWon = partida.deltaMonedas,
                timestampMillis = System.currentTimeMillis()
            )
            victoryService.celebrate(payload)
        }
    }

    private companion object {
        private const val TAG = "ControladorPartida"
    }
}

/**
 * Factory de ViewModel que inyecta el repositorio y el identificador del jugador.
 *
 * @param repo Repositorio respaldado por Room.
 * @param usuarioId Identificador en texto del jugador.
 * @param observarPreferencias Use case reactivo de preferencias.
 * @param victoryService Servicio de celebración de victorias. //
 * @return Instancia de [ControladorPartida].
 * @throws IllegalArgumentException Si la clase solicitada no coincide con el ViewModel esperado.
 * @security
 * - No persiste ni expone datos sensibles; solo pasa referencias de infraestructura.
 */
class ControladorPartidaFactory(
    private val repo: JuegoRepository,
    private val usuarioId: String,
    private val observarPreferencias: ObservarPreferenciasUseCase,
    private val victoryService: VictoryCelebrationService // NUEVO (Victory)
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ControladorPartida::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ControladorPartida(
                repo = repo,
                usuarioId = usuarioId,
                observarPreferencias = observarPreferencias,
                victoryService = victoryService
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
