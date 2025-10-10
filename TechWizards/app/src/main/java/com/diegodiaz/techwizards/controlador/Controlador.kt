package com.diegodiaz.techwizards.controlador

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject


interface RepositorioJuego {
    fun iniciarMonederoSiFalta(): io.reactivex.rxjava3.core.Single<Monedero>
    fun lanzarDadoYGuardar(
        recompensa: Int,
        penalizacion: Int
    ): io.reactivex.rxjava3.core.Single<Pair<Monedero, Partida>>

    fun observarHistorial(): io.reactivex.rxjava3.core.Flowable<List<Partida>>
}

data class Monedero(val id: Int = 1, val monedas: Int)

data class Partida(
    val id: Long,
    val fechaEpochMillis: Long,
    val gano: Boolean,
    val deltaMonedas: Int
)

/** Estado UI */

data class JuegoUiState(
    val monedas: Int = 0,
    val ultimoResultado: String = "",
    val cargando: Boolean = false,
    val error: String? = null
)

/** Controlador */
/**
 * Atributos públicos: [monedas], [ultimoResultado]
 * Métodos: [cargar], [lanzar], [obtenerHistorial]
 * Publica [estado] para que la vista observe cambios
 */

class Controlador(
    private val repo: RepositorioJuego,
    private val recompensaPorDefecto: Int = 10,
    private val penalizacionPorDefecto: Int = 5
) {

    // Atributos
    var monedas: Int = 0
        private set
    var ultimoResultado: String = ""
        private set


    // Gestión de suscripciones
    private val cd = CompositeDisposable()


    // Estado
    private var ui: JuegoUiState = JuegoUiState()
    private val _estado = BehaviorSubject.createDefault(ui)
    val estado: Flowable<JuegoUiState> =
        _estado.hide().toFlowable(BackpressureStrategy.LATEST)


    /** Carga inicial de las monedas */
    fun cargar() {
        val d = repo.iniciarMonederoSiFalta()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { publicar(ui.copy(cargando = true, error = null)) }
            .subscribe({ mon ->
                monedas = mon.monedas
                publicar(ui.copy(cargando = false, monedas = monedas))
            }, { e ->
                publicar(ui.copy(cargando = false, error = e.message ?: "Error cargando"))
            })
        cd.add(d)
    }

    /** Lanzar el dado y jugar */
    fun lanzar(
        recompensa: Int = recompensaPorDefecto,
        penalizacion: Int = penalizacionPorDefecto
    ) {
        val d = repo.lanzarDadoYGuardar(recompensa, penalizacion)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (monederoActual, partida) ->
                monedas = monederoActual.monedas
                ultimoResultado = if (partida.gano)
                    "¡Has ganado! +${partida.deltaMonedas}"
                else
                    "¡Has perdido! ${-partida.deltaMonedas}"

                publicar(ui.copy(monedas = monedas, ultimoResultado = ultimoResultado, error = null))
            }, { e ->
                publicar(ui.copy(error = e.message ?: "Error al lanzar"))
            })
        cd.add(d)
    }

    /** Historial de partidas */
    fun obtenerHistorial(): Flowable<List<Partida>> =
        repo.observarHistorial()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    /** Limpa pantalla*/
    fun limpiar() = cd.clear()


    // Helpers
    private fun publicar(nuevo: JuegoUiState) {
        ui = nuevo
        _estado.onNext(nuevo)
    }
}
