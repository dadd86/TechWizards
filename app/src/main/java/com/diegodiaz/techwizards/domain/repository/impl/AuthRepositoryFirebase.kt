package com.diegodiaz.techwizards.data.repository.impl

import androidx.credentials.Credential
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.diegodiaz.techwizards.data.auth.AUTH_EMAIL
import com.diegodiaz.techwizards.data.auth.AUTH_NAME
import com.diegodiaz.techwizards.data.auth.AUTH_PHOTO
import com.diegodiaz.techwizards.data.auth.AUTH_UID
import com.diegodiaz.techwizards.data.auth.toAuthUser
import com.diegodiaz.techwizards.data.auth.toAuthUser as firebaseToAuthUser
import com.diegodiaz.techwizards.domain.model.AuthUser
import com.diegodiaz.techwizards.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryFirebase (
    private val firebaseAuth: FirebaseAuth,
    private val dataStore: DataStore<Preferences>
) : AuthRepository {
    override fun observarUsuario(): Flow<AuthUser?> =
        dataStore.data.map { prefs -> prefs.toAuthUser() }

    override suspend fun obtenerUsuario(): AuthUser? =
        dataStore.data.firstOrNull()?.toAuthUser()

    override suspend fun iniciarSesionConGoogle(idToken: String): AuthUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val firebaseUser = signInWithCredential(credential)
        val authUser = firebaseUser.firebaseToAuthUser()
        persistir(authUser)
        return authUser
    }

    override suspend fun cerrarSesion() {
        firebaseAuth.signOut()
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                remove(AUTH_UID)
                remove(AUTH_NAME)
                remove(AUTH_EMAIL)
                remove(AUTH_PHOTO)
            }
        }
    }

    private suspend fun signInWithCredential(credential: AuthCredential):
            FirebaseUser {
        firebaseAuth.signInWithCredential(credential).await()
        return firebaseAuth.currentUser
            ?: throw IllegalStateException("usuario Firebase nulo tras SignIn")
    }

    private suspend fun persistir(user: AuthUser) {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[AUTH_UID] = user.uid
                this[AUTH_NAME] = user.displayName ?: ""
                this[AUTH_EMAIL] = user.email ?: ""
                this[AUTH_PHOTO] = user.photoUrl ?: ""
            }
        }
    }
}