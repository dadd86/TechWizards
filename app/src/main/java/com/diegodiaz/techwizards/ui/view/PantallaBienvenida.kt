package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.Responsive

/**
 * Pantalla de bienvenida que solicita el alias del jugador antes de iniciar.
 *
 * @param isDarkTheme Indica si el tema actual es oscuro.
 * @param nombrePredeterminado Alias previamente registrado para prellenar el diálogo.
 * @param onJugar Acción a ejecutar cuando el usuario confirma su alias.
 * @return Unit ya que únicamente emite efectos de UI.
 * @throws IllegalArgumentException No lanza excepciones; se valida el nombre en memoria.
 * @security
 * - El alias se mantiene en memoria hasta confirmarse y luego se envía al ViewModel para persistirlo.
 */
@Composable
fun PantallaBienvenida(
    isDarkTheme: Boolean,
    nombrePredeterminado: String? = null,
    onJugar: (String) -> Unit
) = Responsive { dims ->
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nombreJugador by rememberSaveable(nombrePredeterminado) { mutableStateOf(nombrePredeterminado.orEmpty()) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFF7EC8E3) else MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
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
                                Text(text = errorNombre ?: "", color = MaterialTheme.colorScheme.error)
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
                            errorNombre = stringResource(id = R.string.welcome_invalid_name)
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
