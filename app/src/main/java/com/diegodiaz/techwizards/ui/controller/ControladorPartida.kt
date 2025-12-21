package com.diegodiaz.techwizards.ui.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.core.usecases.ObservarPreferenciasUseCase
import com.diegodiaz.techwizards.core.usecases.RegistrarHistorialRemotoUseCase
import com.diegodiaz.techwizards.core.usecases.ResolverTiradaUseCase
import com.diegodiaz.techwizards.core.usecases.RegistrarUbicacionVictoriaUseCase
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.GameSettings
import com.diegodiaz.techwizards.domain.model.Monedero
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.domain.model.ResolucionTiradaRemota
import com.diegodiaz.techwizards.domain.repository.JuegoRepository
import com.diegodiaz.techwizards.domain.repository.ScoreRepository
import com.diegodiaz.techwizards.core.SessionManager
import com.diegodiaz.techwizards.integration.victory.VictoryCelebrationPayload
import com.diegodiaz.techwizards.integration.victory.VictoryCelebrationService
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class JuegoUiState(
    val monedas: Int = 100,
    val numeroElegido: Int? = null,
    val ultimoResultado: String = "",
    val rollId: Long = 0L,
    val cargando: Boolean = false,
    val error: String? = null,
    val animationsEnabled: Boolean = true,
    val sfxEnabled: Boolean = true
)

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
    selectedLanguageTag = "es"
)

class ControladorPartida(
    private val repo: JuegoRepository,
    private val usuarioId: String,
    private val scoreRepository: ScoreRepository,
    private val observarPreferencias: ObservarPreferenciasUseCase,
    private val victoryService: VictoryCelebrationService,
    private val sessionManager: SessionManager,
    private val registrarHistorialRemotoUseCase: RegistrarHistorialRemotoUseCase,
    private val firebaseUidProvider: () -> String?,

    // 👇 AÑADIDO (mínimo cambio)
    private val registrarUbicacionVictoriaUseCase: RegistrarUbicacionVictoriaUseCase,
    private val resolverTiradaUseCase: ResolverTiradaUseCase,

) : ViewModel() {

    private val preferencias: StateFlow<GameSettings> = observarPreferencias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), defaultSettings)

    val saldo: StateFlow<Int> =
        repo.observarMonedero(usuarioId) // usuarioId debe ser el string correcto del jugador
            .map { it.saldo }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                100 // valor por defecto inicial
            )

    private val rollCounter = MutableStateFlow(0L)

    val ui: StateFlow<JuegoUiState> = combine(
        repo.observarMonedero(usuarioId),
        repo.observarHistorial(usuarioId),
        preferencias,
        rollCounter
    ) { monedero: Monedero, historial: List<Partida>, settings: GameSettings, rollId: Long ->
        JuegoUiState(
            monedas = monedero.saldo,
            ultimoResultado = historial.firstOrNull()?.formatoResumen() ?: "",
            rollId = rollId,
            cargando = false,
            error = null,
            animationsEnabled = settings.animationsEnabled,
            sfxEnabled = settings.sfxEnabled
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JuegoUiState())

    val historial: StateFlow<List<Partida>> =
        repo.observarHistorial(usuarioId = usuarioId, limit = 50)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _eventos = MutableSharedFlow<JuegoUiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventos: SharedFlow<JuegoUiEvent> = _eventos

    fun lanzar() {
        viewModelScope.launch {
            try {
                val partida = repo.lanzarDado(usuarioId)
                rollCounter.update { it + 1 }
                registrarHistorialRemoto(partida)
                publicarPuntuacionRemota(partida)
                handleVictoriaSiNecesario(partida, origen = "lanzar")
            } catch (t: Throwable) {
                DecentralizedLogger.e(TAG, "Error al lanzar", t)
            }
        }
    }

    fun elegirNumero(num: Int) {
        viewModelScope.launch {
            if (num in 1..6) {
                val partida = repo.lanzarDado(usuarioId)
                rollCounter.update { it + 1 }
                registrarHistorialRemoto(partida)
                publicarPuntuacionRemota(partida)
                handleVictoriaSiNecesario(partida, origen = "elegirNumero($num)")
            }
        }
    }

    fun resolverTiradaRemota(resolucion: ResolucionTiradaRemota) {
        viewModelScope.launch {
            when (val resultado = resolverTiradaUseCase(usuarioId, resolucion)) {
                is Result.Err -> {
                    DecentralizedLogger.e(TAG, "No se pudo resolver tirada remota: ${resultado.error}")
                }

                is Result.Ok -> {
                    rollCounter.update { it + 1 }
                    registrarHistorialRemoto(resultado.value.partida)
                    if (resultado.value.gano) {
                        handleVictoriaSiNecesario(resultado.value.partida, origen = "remoto")
                    }
                }
            }
        }
    }

    private suspend fun publicarPuntuacionRemota(partida: Partida) {
        val currentSession = sessionManager.session.value ?: return
        val score = partida.deltaMonedas.coerceAtLeast(0) + if (partida.resultado == Resultado.GANADO) 50 else 10
        runCatching {
            scoreRepository.publicarPuntuacion(currentSession, score)
        }.onFailure { error ->
            DecentralizedLogger.e(TAG, "No se pudo publicar la puntuación", error)
        }
    }


    private suspend fun handleVictoriaSiNecesario(partida: Partida, origen: String) {
        if (partida.resultado == Resultado.GANADO) {

            DecentralizedLogger.i(TAG, "Victoria detectada origen=$origen")
            _eventos.tryEmit(JuegoUiEvent.Victoria(partida))


            try {
                registrarUbicacionVictoriaUseCase(
                    latitude = 41.3853,
                    longitude = 2.1734,
                    accuracyMetres = null
                )
                DecentralizedLogger.i(TAG, "Ubicación registrada en SQLite")
            } catch (t: Throwable) {
                DecentralizedLogger.e(TAG, "Error al registrar ubicación", t)
            }
        }
    }

    private suspend fun registrarHistorialRemoto(partida: Partida) {
        val resultado = registrarHistorialRemotoUseCase(
            firebaseUid = firebaseUidProvider(),
            partida = partida
        )
        if (resultado is Result.Err) {
            DecentralizedLogger.e(
                TAG,
                "No se pudo registrar historial remoto: ${resultado.error}"
            )
        }
    }
    /**
     * Programa la celebración de victoria delegando en `WorkManager`.
     *
     * @param payload Datos mínimos de victoria incluyendo captura.
     * @return `Unit` tras encolar el trabajo.
     * @throws IllegalStateException No se lanza; errores se registran.
     * @security No se registran datos sensibles.
     */
    fun programarCelebracion(payload: VictoryCelebrationPayload) {
        viewModelScope.launch {
            runCatching {
                victoryService.celebrate(payload)
            }.onSuccess {
                DecentralizedLogger.i(TAG, "Celebración encolada desde UI")
            }.onFailure { error ->
                DecentralizedLogger.e(TAG, "Fallo al encolar celebración", error)
            }
        }
    }

    private companion object {
        private const val TAG = "ControladorPartida"
    }

    fun resetMonedas(usuario: Usuario, nuevoSaldo: Int = 100) {
        viewModelScope.launch {
            repo.inicializarMonedas(usuario, nuevoSaldo)
        }
    }
}

class ControladorPartidaFactory(
    private val repo: JuegoRepository,
    private val usuarioId: String,
    private val scoreRepository: ScoreRepository,
    private val observarPreferencias: ObservarPreferenciasUseCase,
    private val victoryService: VictoryCelebrationService,
    private val sessionManager: SessionManager,
    private val registrarHistorialRemotoUseCase: RegistrarHistorialRemotoUseCase,
    private val firebaseUidProvider: () -> String?,
    private val registrarUbicacionVictoriaUseCase: RegistrarUbicacionVictoriaUseCase,
    private val resolverTiradaUseCase: ResolverTiradaUseCase,

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ControladorPartida::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ControladorPartida(
                repo = repo,
                usuarioId = usuarioId,
                scoreRepository = scoreRepository,
                observarPreferencias = observarPreferencias,
                victoryService = victoryService,
                sessionManager = sessionManager,
                registrarHistorialRemotoUseCase = registrarHistorialRemotoUseCase,
                firebaseUidProvider = firebaseUidProvider,
                registrarUbicacionVictoriaUseCase = registrarUbicacionVictoriaUseCase,
                resolverTiradaUseCase = resolverTiradaUseCase,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
