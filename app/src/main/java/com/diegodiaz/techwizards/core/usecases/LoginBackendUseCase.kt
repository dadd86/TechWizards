package com.diegodiaz.techwizards.core.usecases

import android.util.Log
import com.diegodiaz.techwizards.data.remote.score.LoginRequest
import com.diegodiaz.techwizards.data.remote.score.ScoreApi
import com.diegodiaz.techwizards.core.SessionManager


import com.diegodiaz.techwizards.domain.model.UserSession
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class LoginBackendUseCase(
    private val firebaseAuth: FirebaseAuth,
    private val scoreApi: ScoreApi,
    private val sessionManager: SessionManager
) {

    suspend fun execute(alias: String): kotlin.Result<UserSession> {
        Log.d("LOGIN", "execute() START alias='$alias'")

        return try {
            val user = firebaseAuth.currentUser
                ?: return kotlin.Result.failure(IllegalStateException("Firebase currentUser == null"))

            val idToken = user.getIdToken(true).await().token
                ?: return kotlin.Result.failure(IllegalStateException("Firebase ID token null"))

            Log.d("LOGIN", "Firebase token OK len=${idToken.length}")

            // Llama a /login (requiere Bearer Firebase ID Token)
            val resp = scoreApi.login(
                bearerToken = "Bearer $idToken",
                request = LoginRequest(alias = alias)
            )

            val session = UserSession(
                token = idToken,
                alias = resp.alias,
                isAdmin = resp.isAdmin,
                backendToken = resp.token
            )

            sessionManager.setSession(session)
            Log.d("LOGIN", "Session guardada ✅ alias='${session.alias}' tokenLen=${session.token.length}")

            kotlin.Result.success(session)
        } catch (e: Exception) {
            Log.e("LOGIN", "execute() FAILED ❌", e)
            kotlin.Result.failure(e)
        }
    }
}



