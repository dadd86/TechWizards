package com.diegodiaz.techwizards.data.repository.impl

import android.content.Context
import com.diegodiaz.techwizards.core.common.Result
import com.google.firebase.auth.FirebaseAuth
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryFirebaseTest {

    @Test
    fun `signOut clears local data after firebase signOut`() = runBlocking {
        val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
        val context: Context = mockk(relaxed = true)
        var clearedLocalData = false

        val repository = AuthRepositoryFirebase(
            firebaseAuth = firebaseAuth,
            context = context,
            ioDispatcher = Dispatchers.Unconfined,
            clearLocalData = {
                clearedLocalData = true
            }
        )

        val result = repository.signOut()

        assertTrue(result is Result.Ok)
        assertTrue(clearedLocalData)
        verify { firebaseAuth.signOut() }
    }
}