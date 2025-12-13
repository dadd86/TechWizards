package com.diegodiaz.techwizards.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.diegodiaz.techwizards.BuildConfig
import com.diegodiaz.techwizards.core.usecases.CerrarSesionUseCase
import com.diegodiaz.techwizards.core.usecases.IniciarSesionConGoogleUseCase
import com.diegodiaz.techwizards.core.usecases.ObtenerUsuarioAutenticadoUseCase
import com.diegodiaz.techwizards.data.repository.impl.AuthRepositoryFirebase

import com.diegodiaz.techwizards.core.usecases.ObservarUsuarioAutenticadoUseCase
import com.diegodiaz.techwizards.core.usecases.RegistrarUbicacionVictoriaUseCase
import com.diegodiaz.techwizards.data.infra.network.RetrofitProvider
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.local.mapper.VictoryLocationLocalMapper
import com.diegodiaz.techwizards.data.remote.api.ScoresApi
import com.diegodiaz.techwizards.data.remote.mapper.ScoreRemoteMapper
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.repository.impl.JuegoRepositoryRoom
import com.diegodiaz.techwizards.data.repository.impl.MatchRepositoryRoom
import com.diegodiaz.techwizards.data.repository.impl.ScoreRepositoryRetrofit
import com.diegodiaz.techwizards.data.repository.impl.ScoresRepositoryRemote
import com.diegodiaz.techwizards.data.repository.impl.SettingsRepositoryDataStore
import com.diegodiaz.techwizards.data.repository.impl.VictoryRepositoryRoom
import com.diegodiaz.techwizards.data.transaction.RoomTransactionRunner
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.credenciales.EncryptedCredentialsStore
import com.diegodiaz.techwizards.integration.victory.WorkManagerVictoryCelebrationService
import com.diegodiaz.techwizards.util.location.LocationTracker
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceLocator {

    private lateinit var appContext: Context
    // --- Firebase Auth ---
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }

    // --- DB & DAOs ---
    private val db by lazy { BaseDeDatos.get(appContext) }

    private val usuarioDao by lazy { db.usuarioDao() }
    private val monederoDao by lazy { db.monederoDao() }
    private val partidaDao: IPartidaDao by lazy { db.partidaDao() }
    private val victoryLocationDao by lazy { db.victoryLocationDao() }
    private val victoryTransactionRunner by lazy { RoomTransactionRunner(db) }
    private val victoryMapper by lazy { VictoryLocationLocalMapper() }

    // --- Network ---
    private val credentialsStore: CredentialsStore by lazy { EncryptedCredentialsStore() }
    private val retrofitSecure by lazy {
        RetrofitProvider.retrofit(credentialsStore = credentialsStore)
    }
    private val scoresApi by lazy { retrofitSecure.create(ScoresApi::class.java) }
    private val scoreMapper by lazy { ScoreRemoteMapper() }

    private val scoreApi: ScoreApi by lazy {
        RetrofitProvider.retrofit(
            credentialsStore = credentialsStore,
            baseUrl = BuildConfig.API_BASE_URL,
            serializer = BuildConfig.API_SERIALIZER
        )
    }

    // --- Repos locales ---
    val juegoRepository by lazy {
        JuegoRepositoryRoom(
            usuarioDao = usuarioDao,
            monederoDao = monederoDao,
            partidaDao = partidaDao
        )
    }

    val matchRepository by lazy {
        MatchRepositoryRoom(
            partidaDao = partidaDao,
            monederoDao = monederoDao
        )
    }

    val settingsRepository by lazy {
        SettingsRepositoryDataStore(appContext)
    }

    val scoresRepository by lazy {
        ScoresRepositoryRemote(
            scoresApi = scoresApi,
            mapper = scoreMapper,
            credentialsStore = credentialsStore,
        )
    }

    val scoreRepository by lazy {
        ScoreRepositoryRetrofit(
            scoreApi = scoreApi,
            credentialsStore = credentialsStore,
        )
    }

    val victoryRepository by lazy {
        VictoryRepositoryRoom(
            victoryLocationDao,
            victoryTransactionRunner,
            victoryMapper
        )
    }


    // --- Auth + casos de uso ---
    val authRepository: AuthRepository by lazy {
        AuthRepositoryFirebase(
            firebaseAuth = firebaseAuth,
            context = appContext
        )
    }

    val iniciarSesionConGoogleUseCase by lazy {
        IniciarSesionConGoogleUseCase(authRepository)
    }
    val cerrarSesionUseCase by lazy {
        CerrarSesionUseCase(authRepository)
    }
    val observarUsuarioAutenticadoUseCase by lazy {
        ObservarUsuarioAutenticadoUseCase(authRepository)
    }
    val obtenerUsuarioAutenticadoUseCase by lazy {
        ObtenerUsuarioAutenticadoUseCase(authRepository)
    }

    // --- Otros servicios ---
    val victoryCelebrationService by lazy {
        WorkManagerVictoryCelebrationService(appContext)
    }

    val sessionManager by lazy { SessionManager() }

    // Llamar una vez al arrancar la app
    fun init(context: Context) {
        appContext = context.applicationContext
        FirebaseApp.initializeApp(appContext)
    }
}