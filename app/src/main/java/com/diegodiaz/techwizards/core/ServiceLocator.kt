package com.diegodiaz.techwizards.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.diegodiaz.techwizards.BuildConfig
import com.diegodiaz.techwizards.core.usecases.CerrarSesionUseCase
import com.diegodiaz.techwizards.core.usecases.IniciarSesionConGoogleUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarUsuarioAutenticadoUseCase
import com.diegodiaz.techwizards.core.usecases.ObtenerUsuarioAutenticadoUseCase
import com.diegodiaz.techwizards.core.usecases.ActualizarPremioComunUseCase
import com.diegodiaz.techwizards.core.usecases.ResolverTiradaUseCase
import com.diegodiaz.techwizards.core.usecases.RegistrarUbicacionVictoriaUseCase
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.credenciales.EncryptedCredentialsStore
import com.diegodiaz.techwizards.data.infra.network.RetrofitProvider
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.dao.IMatchDao
import com.diegodiaz.techwizards.data.local.dao.IMatchParticipantDao
import com.diegodiaz.techwizards.data.local.dao.IMatchScoreDao
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.local.cache.MatchSnapshotLocalDataSource
import com.diegodiaz.techwizards.data.local.mapper.VictoryLocationLocalMapper
import com.diegodiaz.techwizards.data.remote.match.MatchRealtimeFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.match.MatchApi
import com.diegodiaz.techwizards.data.remote.match.MatchRemoteMapper
import com.diegodiaz.techwizards.data.remote.api.ScoresApi
import com.diegodiaz.techwizards.data.remote.mapper.ScoreRemoteMapper
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.repository.impl.*
import com.diegodiaz.techwizards.data.transaction.RoomTransactionRunner
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import com.diegodiaz.techwizards.integration.victory.WorkManagerVictoryCelebrationService
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object ServiceLocator {

    private lateinit var appContext: Context

    // --------------------------------------------------
    // Firebase
    // --------------------------------------------------
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }

    // --------------------------------------------------
    // Database
    // --------------------------------------------------
    private val db by lazy { BaseDeDatos.get(appContext) }

    private val usuarioDao by lazy { db.usuarioDao() }
    private val monederoDao by lazy { db.monederoDao() }
    private val partidaDao: IPartidaDao by lazy { db.partidaDao() }
    private val matchDao: IMatchDao by lazy { db.matchDao() }
    private val matchParticipantDao: IMatchParticipantDao by lazy { db.matchParticipantDao() }
    private val matchScoreDao: IMatchScoreDao by lazy { db.matchScoreDao() }
    private val lobbyDao by lazy { db.lobbyDao() }
    private val victoryLocationDao by lazy { db.victoryLocationDao() }

    private val victoryTransactionRunner by lazy {
        RoomTransactionRunner(db)
    }

    private val matchTransactionRunner by lazy {
        RoomTransactionRunner(db)
    }

    private val matchSnapshotLocalDataSource by lazy {
        MatchSnapshotLocalDataSource(settingsRepository.dataStore)
    }

    private val victoryMapper by lazy {
        VictoryLocationLocalMapper()
    }

    // --------------------------------------------------
    // Credentials / Tokens
    // --------------------------------------------------
    private val credentialsStore: CredentialsStore by lazy {
        EncryptedCredentialsStore()
    }

    // --------------------------------------------------
    // Network (Retrofit)
    // --------------------------------------------------
    private val retrofitSecure by lazy {
        RetrofitProvider.retrofit(
            credentialsStore = credentialsStore,
            sessionManager = sessionManager
        )
    }

    private val scoresApi by lazy {
        retrofitSecure.create(ScoresApi::class.java)
    }

    private val scoreMapper by lazy {
        ScoreRemoteMapper()
    }

    private val retrofitScore by lazy {
        RetrofitProvider.retrofit(
            credentialsStore = credentialsStore,
            baseUrl = BuildConfig.API_BASE_URL,
            serializer = BuildConfig.API_SERIALIZER,
            sessionManager = sessionManager
        )
    }

    private val scoreApi by lazy {
        retrofitScore.create(ScoreApi::class.java)
    }

    private val matchApi by lazy {
        retrofitScore.create(MatchApi::class.java)
    }

    private val matchRealtimeDataSource by lazy {
        MatchRealtimeFirebaseDataSource()
    }

    private val matchRemoteMapper by lazy {
        MatchRemoteMapper()
    }



    // --------------------------------------------------
    // Repositories
    // --------------------------------------------------
    val juegoRepository by lazy {
        JuegoRepositoryRoom(
            usuarioDao = usuarioDao,
            monederoDao = monederoDao,
            partidaDao = partidaDao
        )
    }

    val matchRepository by lazy {
        MatchRepositoryRemote(
            api = matchApi,
            realtime = matchRealtimeDataSource,
            mapper = matchRemoteMapper,
            scoreRepository = scoreRepository,
            sessionManager = sessionManager,
            mirrorRoom = MatchRepositoryRoom(
                matchDao = matchDao,
                matchParticipantDao = matchParticipantDao,
                matchScoreDao = matchScoreDao,
                partidaDao = partidaDao,
                monederoDao = monederoDao,
                transactionRunner = matchTransactionRunner,
                snapshotLocalDataSource = matchSnapshotLocalDataSource
            ),
            snapshotLocalDataSource = matchSnapshotLocalDataSource,
            appContext = appContext
        )
    }

    val lobbyRepository by lazy {
        LobbyRepositoryRoom(lobbyDao)
    }


    val settingsRepository by lazy {
        SettingsRepositoryDataStore(appContext)
    }

    val scoresRepository by lazy {
        ScoresRepositoryRemote(
            scoresApi = scoresApi,
            mapper = scoreMapper,
            credentialsStore = credentialsStore
        )
    }

    val scoreRepository by lazy {
        ScoreRepositoryRetrofit(
            scoreApi = scoreApi,
            credentialsStore = credentialsStore,
            sessionManager = sessionManager
        )
    }
    val actualizarPremioComunUseCase by lazy {
        ActualizarPremioComunUseCase(scoreRepository, sessionManager)
    }

    val victoryRepository by lazy {
        VictoryRepositoryRoom(
            dao = victoryLocationDao,
            transactionRunner = victoryTransactionRunner,
            mapper = victoryMapper
        )
    }

    // --------------------------------------------------
    // Auth
    // --------------------------------------------------
    private val authDataStore: DataStore<Preferences> by lazy {
        settingsRepository.dataStore
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryFirebase(
            firebaseAuth = firebaseAuth,
            context = appContext,
        )
    }

    val iniciarSesionConGoogleUseCase by lazy {
        IniciarSesionConGoogleUseCase(
            authRepository = authRepository,
            sessionManager = sessionManager,
            credentialsStore = credentialsStore
        )
    }

    val cerrarSesionUseCase by lazy {
        CerrarSesionUseCase(
            authRepository = authRepository,
            sessionManager = sessionManager,
            credentialsStore = credentialsStore
        )
    }

    val observarUsuarioAutenticadoUseCase by lazy {
        ObservarUsuarioAutenticadoUseCase(authRepository)
    }

    val obtenerUsuarioAutenticadoUseCase by lazy {
        ObtenerUsuarioAutenticadoUseCase(authRepository)
    }

    // --------------------------------------------------
    // Victory / Location
    // --------------------------------------------------
    val registrarUbicacionVictoriaUseCase by lazy {
        RegistrarUbicacionVictoriaUseCase(victoryRepository)
    }

    val resolverTiradaUseCase by lazy {
        ResolverTiradaUseCase(juegoRepository)
    }

    val victoryCelebrationService by lazy {
        WorkManagerVictoryCelebrationService(appContext)
    }

    // --------------------------------------------------
    // Session
    // --------------------------------------------------
    val sessionManager by lazy { SessionManager() }

    // --------------------------------------------------
    // Init
    // --------------------------------------------------
    fun init(context: Context) {
        appContext = context.applicationContext
        FirebaseApp.initializeApp(appContext)
        registrarListenerFirebaseToken()
    }


    private fun registrarListenerFirebaseToken() {
        firebaseAuth.addIdTokenListener(object : FirebaseAuth.IdTokenListener {
            override fun onIdTokenChanged(auth: FirebaseAuth) {
                val user = auth.currentUser
                if (user == null) {
                    credentialsStore.guardarFirebaseToken(null)
                    sessionManager.clearSession()
                    return
                }
                user.getIdToken(false)
                    .addOnSuccessListener { result ->
                        credentialsStore.guardarFirebaseToken(result.token)
                        val token = result.token
                        if (!token.isNullOrBlank()) {
                            val alias = user.displayName ?: user.email ?: "Jugador"
                            sessionManager.setSession(UserSession(token = token, alias = alias))
                        }
                    }
                    .addOnFailureListener {
                        credentialsStore.guardarFirebaseToken(null)
                        sessionManager.clearSession()
                    }
            }
        })
    }
}
