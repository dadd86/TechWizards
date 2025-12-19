package com.diegodiaz.techwizards.ui.controller

import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.core.usecases.CerrarSesionUseCase
import com.diegodiaz.techwizards.core.usecases.IniciarSesionConGoogleUseCase
import com.diegodiaz.techwizards.core.usecases.ObservarUsuarioAutenticadoUseCase
import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ControladorAuthTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `iniciarSesion con token valido actualiza usuario y limpia error`() = runTest {
        val repo = FakeAuthRepository()
        val vm = ControladorAuth(
            iniciarSesion = IniciarSesionConGoogleUseCase(repo, dispatcher),
            cerrarSesion = CerrarSesionUseCase(repo, dispatcher),
            observarUsuario = ObservarUsuarioAutenticadoUseCase(repo)
        )

        vm.iniciarSesion("ok")
        advanceUntilIdle()

        val state = vm.ui.value
        assertNotNull(state.usuario)
        assertEquals("uid_1", state.usuario!!.uid)
        assertNull(state.error)
        assertFalse(state.cargando)
    }

    @Test
    fun `iniciarSesion con token invalido deja error y no autentica`() = runTest {
        val repo = FakeAuthRepository()
        val vm = ControladorAuth(
            iniciarSesion = IniciarSesionConGoogleUseCase(repo, dispatcher),
            cerrarSesion = CerrarSesionUseCase(repo, dispatcher),
            observarUsuario = ObservarUsuarioAutenticadoUseCase(repo)
        )

        vm.iniciarSesion("bad")
        advanceUntilIdle()

        val state = vm.ui.value
        assertNull(state.usuario)
        assertNotNull(state.error)
        assertFalse(state.cargando)
    }

    @Test
    fun `cerrarSesion limpia el estado`() = runTest {
        val repo = FakeAuthRepository()
        val vm = ControladorAuth(
            iniciarSesion = IniciarSesionConGoogleUseCase(repo, dispatcher),
            cerrarSesion = CerrarSesionUseCase(repo, dispatcher),
            observarUsuario = ObservarUsuarioAutenticadoUseCase(repo)
        )

        vm.iniciarSesion("ok")
        advanceUntilIdle()
        assertNotNull(vm.ui.value.usuario)

        vm.cerrarSesion()
        advanceUntilIdle()

        val state = vm.ui.value
        assertNull(state.usuario)
        assertNull(state.error)
        assertFalse(state.cargando)
    }

    private class FakeAuthRepository : AuthRepository {

        private val _user = MutableStateFlow<AuthUser?>(null)
        private var cached: AuthUser? = null

        override fun observeUser(): Flow<AuthUser?> = _user.asStateFlow()


        override suspend fun signInWithGoogle(idToken: String): Result<AuthUser, AgentError> {
            return if (idToken == "ok") {
                val u = AuthUser(
                    uid = "uid_1",
                    email = "roger@test.com",
                    displayName = "Roger",
                    photoUrl = null
                )
                cached = u
                _user.value = u
                Result.Ok(u)
            } else {
                Result.Err(AgentError.Validation("token inválido"))
            }
        }
        override suspend fun fetchIdToken(forceRefresh: Boolean): Result<String, AgentError> {
            return if (cached != null) {
                Result.Ok("token_${cached!!.uid}")
            } else {
                Result.Err(AgentError.Validation("no user"))
            }
        }

        override suspend fun signOut(): Result<Unit, AgentError> {
            cached = null
            _user.value = null
            return Result.Ok(Unit)
        }
        override suspend fun getCachedUser(): Result<AuthUser?, AgentError> = Result.Ok(cached)
    }
}

