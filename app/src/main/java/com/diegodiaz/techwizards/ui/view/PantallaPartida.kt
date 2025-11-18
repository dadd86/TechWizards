package com.diegodiaz.techwizards.ui.view

import android.Manifest
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.drawToBitmap
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.integration.victory.VictoryCelebrationPayload
import com.diegodiaz.techwizards.integration.victory.VictoryCelebrationService
import com.diegodiaz.techwizards.ui.controller.JuegoUiEvent
import com.diegodiaz.techwizards.ui.controller.JuegoUiState
import com.diegodiaz.techwizards.ui.responsive.Responsive
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

/**
 * Pantalla principal del juego donde el usuario elige un número y lanza el dado.
 */
@Composable
fun PantallaPartida(
    isDarkTheme: Boolean,
    uiState: JuegoUiState,
    eventos: SharedFlow<JuegoUiEvent>,
    onVolverAlMenu: () -> Unit,
    onElegirNumero: (Int) -> Unit
) = Responsive { dims ->
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val view = LocalView.current
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 80) }
    DisposableEffect(Unit) {
        onDispose { toneGenerator.release() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    LaunchedEffect(eventos, uiState.sfxEnabled) {
        eventos.collectLatest { evento ->
            if (evento is JuegoUiEvent.Victoria && evento.partida.resultado == Resultado.GANADO) {
                val bitmap = view.drawToBitmap()
                val stream = ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
                val payload = VictoryCelebrationPayload(
                    partidaId = evento.partida.id,
                    aliasJugador = evento.partida.aliasJugador,
                    deltaMonedas = evento.partida.deltaMonedas,
                    timestampMs = System.currentTimeMillis(),
                    screenshotBytes = stream.toByteArray()
                )
                VictoryCelebrationService.enqueue(context.applicationContext, payload)
                if (uiState.sfxEnabled) {
                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, 250)
                }
            }
        }
    }

    val rotation = remember { Animatable(0f) }
    var lastResultado by remember { mutableStateOf("") }

    // ANIMACIÓN: ficha (número elegido) moviéndose en el tablero
    val fichaOffsetY = remember { Animatable(60f) } // "dp virtuales"
    val fichaScale = remember { Animatable(0f) }

    LaunchedEffect(uiState.ultimoResultado, uiState.animationsEnabled, uiState.sfxEnabled) {
        if (uiState.ultimoResultado.isNotBlank() && uiState.ultimoResultado != lastResultado) {
            lastResultado = uiState.ultimoResultado
            if (uiState.animationsEnabled) {
                rotation.snapTo(0f)
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 600)
                )
            }
            if (uiState.sfxEnabled) {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 120)
            }
        }
    }

    // Cuando cambia el número elegido, animamos su "entrada" como ficha
    LaunchedEffect(uiState.numeroElegido, uiState.animationsEnabled) {
        val numero = uiState.numeroElegido ?: return@LaunchedEffect
        if (uiState.animationsEnabled) {
            fichaOffsetY.snapTo(60f)
            fichaScale.snapTo(0f)
            fichaOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 400)
            )
            fichaScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400)
            )
        } else {
            fichaOffsetY.snapTo(0f)
            fichaScale.snapTo(1f)
        }
    }

    // Tamaños relativos a la pantalla
    val boxSize = (dims.minSide * 0.8f).coerceAtMost(360.dp)     // contenedor del dado
    val diceSize = boxSize * 0.7f                                // imagen del dado
    val coinSize = (dims.minSide * 0.10f).coerceIn(24.dp, 48.dp) // icono moneda
    val fichaSize = (diceSize * 0.55f).coerceIn(32.dp, 72.dp)    // tamaño ficha animada

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFFD6E8CD) else MaterialTheme.colorScheme.background)
            .padding(horizontal = dims.spaceSm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(dims.spaceMd))

            // Barra de monedas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dims.spaceSm)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.moneda),
                    contentDescription = "Moneda",
                    modifier = Modifier.size(coinSize)
                )
                Spacer(Modifier.width(dims.spaceXs))
                Text(
                    text = " ${uiState.monedas}",
                    fontSize = dims.titleSp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Centro: dado + ficha + botón lanzar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(boxSize)
                        .background(Color(0xFFF9F9EB), shape = RoundedCornerShape(dims.cardCorner)),
                    contentAlignment = Alignment.Center
                ) {
                    // Dado
                    Image(
                        painter = painterResource(id = R.drawable.dado_negro),
                        contentDescription = "Dado",
                        modifier = Modifier
                            .size(diceSize)
                            .rotate(rotation.value),
                        contentScale = ContentScale.Fit
                    )

                    // FICHA ANIMADA: número elegido que "sube" y aparece abajo del dado
                    uiState.numeroElegido?.let { numero ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = fichaOffsetY.value.dp)
                                .graphicsLayer {
                                    scaleX = fichaScale.value
                                    scaleY = fichaScale.value
                                }
                                .size(fichaSize)
                                .background(Color(0xFF5C6BC0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = numero.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = (dims.bodySp.value * 1.1f).sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(dims.spaceMd))

                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(dims.buttonHeight)
                        .clip(RoundedCornerShape(dims.cardCorner)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF96C78F))
                ) {
                    Text(
                        text = "Lanzar",
                        fontWeight = FontWeight.Bold,
                        fontSize = dims.titleSp
                    )
                }

                Spacer(Modifier.height(dims.spaceSm))

                Text(
                    text = uiState.ultimoResultado.ifEmpty { "Último lanzamiento: ..." },
                    fontWeight = FontWeight.Medium,
                    fontSize = dims.bodySp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(Modifier.height(dims.spaceSm))
            Button(
                onClick = onVolverAlMenu,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(dims.buttonHeightSm)
                    .clip(RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Volver al menú",
                    color = Color(0xFF96C78F),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.bodySp
                )
            }

            Spacer(Modifier.height(dims.spaceMd))
        }

        @OptIn(ExperimentalLayoutApi::class)
        if (showDialog) {
            val chipSize = (dims.minSide * 0.20f).coerceIn(40.dp, 64.dp)
            val spacing  = dims.spaceSm

            AlertDialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dims.spaceMd),
                title = {
                    Text(
                        "Elige un número del 1 al 6",
                        fontSize = dims.bodySp
                    )
                },
                text = {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalArrangement   = Arrangement.spacedBy(spacing)
                    ) {
                        (1..6).forEach { number ->
                            Button(
                                onClick = {
                                    // Aquí disparamos la lógica del VM (que actualiza numeroElegido)
                                    onElegirNumero(number)
                                    showDialog = false
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF5C6BC0),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.size(chipSize)
                            ) {
                                Text(
                                    text = number.toString(),
                                    fontSize = (dims.bodySp.value * 0.9f).sp
                                )
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar", fontSize = dims.bodySp)
                    }
                }
            )
        }
    }
}
