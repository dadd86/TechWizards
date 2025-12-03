package com.diegodiaz.techwizards.credenciales

interface CredentialsStore {
    /**
     * Almacena en memoria un token de Firebase para peticiones autenticadas.
     *
     * @security No persiste en claro; la implementación real debe cifrar si se guarda en disco.
     */
    fun guardarFirebaseToken(token: String?)

    /**
     * Devuelve el token actual para cabeceras `Authorization` o query `auth`.
     *
     * @return Token JWT o `null` si no está disponible.
     */
    fun obtenerFirebaseToken(): String?
}