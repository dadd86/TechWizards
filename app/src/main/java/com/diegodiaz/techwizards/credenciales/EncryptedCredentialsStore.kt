package com.diegodiaz.techwizards.credenciales

class EncryptedCredentialsStore {
    /**
     * Implementación mínima en memoria de [CredentialsStore].
     *
     * @security No persiste valores en disco para evitar exfiltración accidental.
     */
    class EncryptedCredentialsStore : CredentialsStore {

        @Volatile
        private var firebaseToken: String? = null

        override fun guardarFirebaseToken(token: String?) {
            firebaseToken = token
        }

        override fun obtenerFirebaseToken(): String? = firebaseToken
    }