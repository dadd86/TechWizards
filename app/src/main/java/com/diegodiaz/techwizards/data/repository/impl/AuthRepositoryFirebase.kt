package com.diegodiaz.techwizards.data.repository.impl

import android.content.Context
import com.diegodiaz.techwizards.core.common.AgentError
import com.diegodiaz.techwizards.core.common.Result
import com.diegodiaz.techwizards.data.local.db.BaseDeDatos
import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Implementación de [AuthRepository] usando FirebaseAuth.
 */
class AuthRepositoryFirebase(
    private val firebaseAuth: FirebaseAuth,
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clearLocalData: suspend (Context) -> Unit = { ctx ->
        BaseDeDatos.get(ctx).clearAllTables()
    }
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUser, AgentError> =
        withContext(ioDispatcher) {
            if (idToken.isBlank()) {
                return@withContext Result.Err(AgentError.Validation("idToken vacío"))
            }

            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = firebaseAuth.signInWithCredential(credential).await()
                val user = result.user
                    ?: return@withContext Result.Err(AgentError.Unknown(null))

                Result.Ok(user.toDomain())
            } catch (e: Exception) {
                Result.Err(AgentError.Unknown(e))
            }
        }

    override suspend fun signOut(): Result<Unit, AgentError> =
        withContext(ioDispatcher) {
            try {
                firebaseAuth.signOut()

                // Limpia datos locales relacionados con el jugador
                clearLocalData(context)

                Result.Ok(Unit)
            } catch (e: Exception) {
                Result.Err(AgentError.Unknown(e))
            }
        }

    override suspend fun getCachedUser(): Result<AuthUser?, AgentError> =
        withContext(ioDispatcher) {
            try {
                val user = firebaseAuth.currentUser
                Result.Ok(user?.toDomain())
            } catch (e: Exception) {
                Result.Err(AgentError.Unknown(e))
            }
        }

    override fun observeUser(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomain()).isSuccess
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    private fun com.google.firebase.auth.FirebaseUser.toDomain(): AuthUser =
        AuthUser(
            uid = uid,
            displayName = displayName,
            email = email,
            photoUrl = photoUrl?.toString()
        )
}

