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
import com.diegodiaz.techwizards.core.usecases.RegistrarHistorialRemotoUseCase
import com.diegodiaz.techwizards.core.usecases.ResolverTiradaUseCase
import com.diegodiaz.techwizards.core.usecases.LoginBackendUseCase
import com.diegodiaz.techwizards.core.usecases.RegistrarUbicacionVictoriaUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarHistorialRemotoUseCase
import com.diegodiaz.techwizards.credenciales.CredentialsStore
import com.diegodiaz.techwizards.credenciales.EncryptedCredentialsStore
import com.diegodiaz.techwizards.data.infra.network.RetrofitProvider
import com.diegodiaz.techwizards.data.local.dao.IPartidaDao
import com.diegodiaz.techwizards.data.local.dao.IMatchDao
import com.google.firebase.auth.FirebaseAuth
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreLeaderboardSdkDataSource
import com.diegodiaz.techwizards.data.local.dao.IMatchParticipantDao
import com.diegodiaz.techwizards.data.local.dao.IMatchScoreDao
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.data.local.cache.MatchSnapshotLocalDataSource
import com.diegodiaz.techwizards.data.local.cache.UserIdLocalDataSource
import com.diegodiaz.techwizards.data.local.mapper.VictoryLocationLocalMapper
import com.diegodiaz.techwizards.data.remote.lobby.LobbyRealtimeFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.history.PartidaHistoryFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.match.MatchRealtimeFirebaseDataSource
import com.diegodiaz.techwizards.data.remote.match.MatchApi
import com.diegodiaz.techwizards.data.remote.match.MatchRemoteMapper
import com.diegodiaz.techwizards.data.remote.api.ScoresApi
import com.diegodiaz.techwizards.data.remote.firestore.FirestoreLeaderboardApi
import com.diegodiaz.techwizards.data.remote.prize.PremioComunFirestoreDataSource
import com.diegodiaz.techwizards.data.remote.prize.PremioComunBackendDataSource

import com.diegodiaz.techwizards.data.remote.firestore.FirestorePlayersApi
import android.util.Log
import com.diegodiaz.techwizards.data.remote.score.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.diegodiaz.techwizards.data.remote.mapper.ScoreRemoteMapper
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.data.repository.impl.*
import com.diegodiaz.techwizards.data.transaction.RoomTransactionRunner
import com.diegodiaz.techwizards.domain.model.UserSession
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import com.diegodiaz.techwizards.domain.repository.PartidaHistoryRepository
import com.diegodiaz.techwizards.integration.victory.WorkManagerVictoryCelebrationService
import com.google.firebase.FirebaseApp
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
    private val firestoreBaseUrl by lazy {
        FirebaseApp.getInstance().options.projectId?.let { projectId ->
            "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents/"
        }
    }

    private val firestoreQueryBaseUrl by lazy {
        FirebaseApp.getInstance().options.projectId?.let { projectId ->
            "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/"
        }
    }


    private val firestoreRetrofit by lazy {
        firestoreBaseUrl?.let { baseUrl ->
            RetrofitProvider.retrofit(
                credentialsStore = credentialsStore,
                baseUrl = baseUrl,
                serializer = BuildConfig.API_SERIALIZER,
                sessionManager = sessionManager
            )
        }
    }

    private val firestoreQueryRetrofit by lazy {
        firestoreQueryBaseUrl?.let { baseUrl ->
            RetrofitProvider.retrofit(
                credentialsStore = credentialsStore,
                baseUrl = baseUrl,
                serializer = BuildConfig.API_SERIALIZER,
                sessionManager = sessionManager
            )
        }
    }


    private val firestorePlayersApi by lazy {
        firestoreRetrofit?.create(FirestorePlayersApi::class.java)
    }

    private val firestoreLeaderboardApi by lazy {
        firestoreQueryRetrofit?.create(FirestoreLeaderboardApi::class.java)
    }

    private val firestoreLeaderboardSdkDataSource by lazy {
        FirestoreLeaderboardSdkDataSource()
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

    val lobbyRealtimeDataSource by lazy {
        LobbyRealtimeFirebaseDataSource()
    }

    private val matchRemoteMapper by lazy {
        MatchRemoteMapper()
    }

    private val partidaHistoryDataSource by lazy {
        PartidaHistoryFirebaseDataSource()
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

    val userIdLocalDataSource by lazy {
        UserIdLocalDataSource(settingsRepository.dataStore)
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
            sessionManager = sessionManager,
            firebaseAuth = firebaseAuth,
            premioComunBackendDataSource = PremioComunBackendDataSource(scoreApi),
            firestorePlayersApi = firestorePlayersApi,
            firestoreLeaderboardApi = firestoreLeaderboardApi,
            firestoreLeaderboardSdkDataSource = firestoreLeaderboardSdkDataSource
        )
    }

    private val partidaHistoryRepository: PartidaHistoryRepository by lazy {
        PartidaHistoryRepositoryFirebase(
            dataSource = partidaHistoryDataSource,
            scoreApi = scoreApi,
            sessionManager = sessionManager,
            loginBackendUseCase = loginBackendUseCase //
        )
    }
    val actualizarPremioComunUseCase by lazy {
        ActualizarPremioComunUseCase(scoreRepository, sessionManager)
    }

    val loginBackendUseCase by lazy {
        LoginBackendUseCase(
            firebaseAuth = FirebaseAuth.getInstance(),
            scoreApi = scoreApi,
            sessionManager = sessionManager
        )
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
            credentialsStore = credentialsStore,
            scoreApi = scoreApi // ✅
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
    val registrarHistorialRemotoUseCase by lazy {
        RegistrarHistorialRemotoUseCase(partidaHistoryRepository)
    }
    val observarHistorialRemotoUseCase by lazy {
        ObservarHistorialRemotoUseCase(partidaHistoryRepository)
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


    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
                        val firebaseToken = result.token
                        credentialsStore.guardarFirebaseToken(firebaseToken)

                        if (firebaseToken.isNullOrBlank()) return@addOnSuccessListener

                        val alias = user.displayName ?: user.email ?: "Jugador"

                        // 1) Conserva backendToken si ya existía
                        val currentSession = sessionManager.session.value
                        sessionManager.setSession(
                            UserSession(
                                token = firebaseToken,
                                alias = alias,
                                backendToken = currentSession?.backendToken,
                                isAdmin = currentSession?.isAdmin ?: false
                            )
                        )

                        // 2) ✅ Si aún NO tenemos backendToken, hacemos /login al backend
                        if (currentSession?.backendToken.isNullOrBlank()) {
                            serviceScope.launch {
                                try {
                                    Log.d("BACKEND", "Haciendo POST /login...")

                                    val resp = scoreApi.login(
                                        bearerToken = "Bearer $firebaseToken",
                                        request = LoginRequest(alias = alias)
                                    )

                                    Log.d("BACKEND", "Login OK, backendToken recibido")

                                    // Guardar backendToken en SessionManager
                                    withContext(Dispatchers.Main) {
                                        val latest = sessionManager.session.value
                                        sessionManager.setSession(
                                            UserSession(
                                                token = firebaseToken,
                                                alias = resp.alias,
                                                backendToken = resp.token,
                                                isAdmin = resp.isAdmin
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e("BACKEND", "Fallo en /login", e)
                                }
                            }
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
