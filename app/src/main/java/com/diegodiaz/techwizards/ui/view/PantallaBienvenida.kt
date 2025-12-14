package com.diegodiaz.techwizards.ui.view

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.controller.AuthState
import com.diegodiaz.techwizards.ui.responsive.UiDims
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

/**
 * Pantalla de bienvenida que solicita el alias del jugador antes de iniciar.
 *
 * @param isDarkTheme Indica si el tema actual es oscuro.
 * @param nombrePredeterminado Alias previamente registrado para prellenar el diálogo.
 * @param onJugar Acción a ejecutar cuando el usuario confirma su alias.
 * @param onGoogleSignIn Callback con el token de Google para autenticar.
 * @param onLogout Acción de cierre de sesión.
 * @param authState Estado actual de autenticación.
 */
@Composable
fun PantallaBienvenida(
    isDarkTheme: Boolean,
    nombrePredeterminado: String? = null,
    onJugar: (String) -> Unit,
    onGoogleSignIn: (String) -> Unit,
    onLogout: () -> Unit,
    authState: AuthState,
    dims: UiDims
) {
    val invalidNameMessage = stringResource(id = R.string.welcome_invalid_name)
    var mostrarDialogo by remember { mutableStateOf(false) }

    var nombreJugador by rememberSaveable(nombrePredeterminado, authState.usuario?.displayName) {
        mutableStateOf(nombrePredeterminado ?: authState.usuario?.displayName.orEmpty())
    }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var authError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // ---------- Google Identity Services (One Tap) ----------
    val oneTapClient: SignInClient = remember { Identity.getSignInClient(context) }

    val signInRequest = remember {
        BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false) // permitir cuentas nuevas
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }

    val oneTapLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken

                if (idToken.isNullOrEmpty()) {
                    authError = context.getString(R.string.auth_missing_token)
                } else {
                    onGoogleSignIn(idToken)
                }
            } catch (e: Exception) {
                val code = (e as? ApiException)?.statusCode
                authError = "getCredential failed: ${code ?: "null"} - ${e.message}"
            }
        } else {
            authError = context.getString(R.string.auth_google_failed, -1)
        }
    }
    // --------------------------------------------------------

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (!isDarkTheme) Color(0xFF7EC8E3)
                else MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dims.spaceLg)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dims.imageLg * 1.25f)
                    .background(Color(0xFF5597CF), shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dado),
                    contentDescription = stringResource(id = R.string.welcome_dice_content_description),
                    modifier = Modifier.size(dims.imageLg)
                )
            }

            Text(
                text = stringResource(id = R.string.welcome_title),
                fontSize = dims.titleSp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = (dims.titleSp * 1.2f)
            )

            Button(
                onClick = { mostrarDialogo = true },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(dims.buttonHeight)
                    .clip(RoundedCornerShape(dims.cardCorner)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    stringResource(id = R.string.welcome_play),
                    fontSize = dims.bodySp,
                    color = Color(0xFF3B71B8),
                    fontWeight = FontWeight.Bold
                )
            }

            // ✅ SECCIÓN LOGIN GOOGLE (AQUÍ SE MUESTRA EL BOTÓN)
            GoogleAuthSection(
                dims = dims,
                authState = authState,
                authError = authError ?: authState.error,
                onLoginClick = {
                    authError = null
                    oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener { result ->
                            val request = IntentSenderRequest.Builder(
                                result.pendingIntent.intentSender
                            ).build()
                            oneTapLauncher.launch(request)
                        }
                        .addOnFailureListener { e ->
                            val code = (e as? ApiException)?.statusCode
                            val codeText = code?.let { "${it} (${CommonStatusCodes.getStatusCodeString(it)})" } ?: "null"
                            authError = "beginSignIn failed: $codeText - ${e.message}"
                        }
                },
                onLogout = {
                    authError = null
                    onLogout()
                }
            )
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
                errorNombre = null
            },
            title = { Text(text = stringResource(id = R.string.welcome_enter_name)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(dims.spaceSm)) {
                    OutlinedTextField(
                        value = nombreJugador,
                        onValueChange = {
                            nombreJugador = it
                            if (errorNombre != null) errorNombre = null
                        },
                        label = { Text(stringResource(id = R.string.welcome_player_name_label)) },
                        singleLine = true,
                        isError = errorNombre != null,
                        supportingText = {
                            if (errorNombre != null) {
                                Text(
                                    text = errorNombre ?: "",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Text(
                        text = stringResource(id = R.string.welcome_alias_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val nombreNormalizado = nombreJugador.trim()
                        if (nombreNormalizado.isEmpty()) {
                            errorNombre = invalidNameMessage
                        } else {
                            onJugar(nombreNormalizado)
                            mostrarDialogo = false
                        }
                    }
                ) {
                    Text(stringResource(id = R.string.action_accept))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                        errorNombre = null
                    }
                ) {
                    Text(stringResource(id = R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun GoogleAuthSection(
    dims: UiDims,
    authState: AuthState,
    authError: String?,
    onLoginClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(dims.cardCorner))
            .background(MaterialTheme.colorScheme.surface)
            .padding(dims.spaceMd)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            Text(
                text = stringResource(id = R.string.auth_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (authState.cargando) {
                CircularProgressIndicator(modifier = Modifier.size(dims.spaceMd))
            }
        }

        if (authState.usuario != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.auth_signed_in_as,
                        authState.usuario.displayName
                            ?: stringResource(id = R.string.auth_unknown_user)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                authState.usuario.email?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onLogout,
                enabled = !authState.cargando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = stringResource(id = R.string.auth_logout))
            }
        } else {
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !authState.cargando
            ) {
                Text(stringResource(id = R.string.auth_with_google))
            }
        }

        if (!authError.isNullOrEmpty()) {
            Text(
                text = authError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
