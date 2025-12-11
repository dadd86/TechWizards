package com.diegodiaz.techwizards.data.auth

import com.diegodiaz.techwizards.domain.model.AuthUser
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toAuthUser(): AuthUser =
    AuthUser(
        uid = uid,
        displayName = displayName,
        email =  email,
        photoUrl = photoUrl?.toString()
    )