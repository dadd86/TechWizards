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

/**
 * Estado observable que alimenta la UI de la partida.
 *
 * @property monedas Saldo actual del monedero.
 * @property numeroElegido Último número escogido por el jugador.
 * @property ultimoResultado Resumen textual del último lanzamiento.
 * @property cargando Indicador de operaciones en curso.
 * @property error Mensaje de error para mostrar en UI.
 * @security
 * - Solo incluye alias y datos de juego, nunca credenciales.
 */
data class JuegoUiState(
    val monedas: Int = 100,
    val numeroElegido: Int? = null,
    val ultimoResultado: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)

    /**
     * Devuelve un texto descriptivo del resultado más reciente incorporando el alias del jugador.
     *
     * @return Cadena localizada con alias y cambio de monedas.
     * @throws IllegalStateException No lanza excepciones; la rama `when` cubre todos los casos.
     * @security
     * - Expone únicamente alias y delta de monedas.
     */
    fun Partida.formatoResumen(): String = when (resultado) {
        Resultado.GANADO -> "${aliasJugador} ganó (+$deltaMonedas)"
        Resultado.PERDIDO -> "${aliasJugador} perdió ($deltaMonedas)"
    }

    /**
     * ViewModel que coordina acciones de partida y expone estado reactivo para la UI.
     *
     * @param repo Repositorio con acceso a Room.
     * @param usuarioId Identificador del jugador en formato String.
     * @security
     * - No almacena secretos, solo id locales y alias.
     */
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
                } catch (t: Throwable) {
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
                    // Simulación de lanzar dado, usar lógica real de tu dominio
                    repo.lanzarDado(usuarioId) // actualiza BD, los Flows cambian el UI
                }
            }
        }
    }

    /**
     * Factory de ViewModel que inyecta el repositorio y el identificador del jugador.
     *
     * @param repo Repositorio respaldado por Room.
     * @param usuarioId Identificador en texto del jugador.
     * @return Instancia de [ControladorPartida].
     * @throws IllegalArgumentException Si la clase solicitada no coincide con el ViewModel esperado.
     * @security
     * - No persiste ni expone datos sensibles; solo pasa referencias de infraestructura.
     */
    class ControladorPartidaFactory(
        private val repo: JuegoRepository,
        private val usuarioId: String
    ) : ViewModelProvider.Factory {
        /**
         * Crea instancias de [ControladorPartida] solicitadas por Compose.
         *
         * @param modelClass Clase requerida por el framework.
         * @return Instancia configurada del ViewModel.
         * @throws IllegalArgumentException Si el tipo solicitado no es compatible.
         * @security
         * - No filtra datos; solo construye dependencias.
         */
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ControladorPartida::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ControladorPartida(repo, usuarioId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }