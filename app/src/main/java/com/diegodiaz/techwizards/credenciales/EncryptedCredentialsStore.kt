package com.diegodiaz.techwizards.credenciales

/**
 * Implementación mínima en memoria de [CredentialsStore].
 *
 * @security No persiste valores en disco para evitar exfiltración accidental.
 */
class EncryptedCredentialsStore : CredentialsStore {

    @Volatile
    private var firebaseToken: String? = null

    @Volatile
    private var aliasAutenticado: String? = null

    override fun guardarFirebaseToken(token: String?) {
        firebaseToken = token
    }

    override fun obtenerFirebaseToken(): String? = firebaseToken

    override fun guardarSesionAlias(token: String?, alias: String?) {
        firebaseToken = token
        aliasAutenticado = alias
    }

    override fun obtenerAliasAutenticado(): String? = aliasAutenticado
}
