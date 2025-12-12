package com.diegodiaz.techwizards.data.auth

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.diegodiaz.techwizards.domain.model.AuthUser

val AUTH_UID = stringPreferencesKey("auth_uid")
val AUTH_NAME = stringPreferencesKey("auth_name")
val AUTH_EMAIL = stringPreferencesKey("auth_email")
val AUTH_PHOTO = stringPreferencesKey("auth_photo")

fun Preferences.toAuthUser(): AuthUser? {
    val uid = this[AUTH_UID] ?: return null
    return AuthUser(
        uid = uid,
        displayName = this[AUTH_NAME],
        email = this[AUTH_EMAIL],
        photoUrl = this[AUTH_PHOTO]
    )
}