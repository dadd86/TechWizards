package com.diegodiaz.techwizards.ui.view

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.runtime.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.drawToBitmap
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.integration.victory.VictoryCelebrationPayload
import com.diegodiaz.techwizards.integration.victory.WorkManagerVictoryCelebrationService
import com.diegodiaz.techwizards.ui.controller.JuegoUiEvent
import com.diegodiaz.techwizards.ui.controller.JuegoUiState
import com.diegodiaz.techwizards.ui.responsive.UiDims
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Pantalla principal de partida con gestión de animaciones, audio y celebraciones.
 *
 * @param isDarkTheme Indica si el tema oscuro está activo.
 * @param uiState Estado inmutable de la pantalla.
 * @param eventos Flujo de eventos de dominio.
 * @param onVolverAlMenu Acción para regresar al menú.
 * @param onElegirNumero Callback al seleccionar número.
 * @param onProgramarCelebracion Callback para encolar celebración con captura.
 * @param dims Dimensiones responsivas para layout.
 * @return `Unit` tras componer la UI.
 * @throws IllegalStateException No se lanza; efectos manejados por Compose.
 * @security No muestra datos sensibles en logs.
 */
@Composable
fun PantallaPartida(
    isDarkTheme: Boolean,
    uiState: JuegoUiState,
    eventos: SharedFlow<JuegoUiEvent>,
    onVolverAlMenu: () -> Unit,
    onElegirNumero: (Int) -> Unit,
    onProgramarCelebracion: (VictoryCelebrationPayload) -> Unit,
    dims: UiDims
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val view = LocalView.current
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 80) }
    var showSinMonedasDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.monedas) {
        showSinMonedasDialog = uiState.monedas <= 0
    }

    if (showSinMonedasDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(id = R.string.no_money_title)) },
            text = { Text(stringResource(id = R.string.no_money_message)) },
            confirmButton = {
                TextButton(onClick = onVolverAlMenu) {
                    Text(stringResource(id = R.string.no_money_action))
                }
            },
            dismissButton = {}
        )
    }

    DisposableEffect(Unit) {
        onDispose { toneGenerator.release() }
    }

    // ------------------ permisos ------------------
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
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions.toTypedArray())
    }

    // ------------------ estados de animación ------------------
    val fichaOffsetY = remember { Animatable(60f) }
    val fichaScale = remember { Animatable(0f) }

    val rotation = remember { Animatable(0f) }
    var showConfetti by remember { mutableStateOf(false) }

    // 1) Animación de lanzamiento (SIEMPRE que hay tirada), con su beep.
    LaunchedEffect(uiState.rollId, uiState.animationsEnabled, uiState.sfxEnabled) {
        if (uiState.rollId > 0) {
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

    // 2) Movimiento de la ficha con el número elegido
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

    // 3) Efecto de VICTORIA: confeti + sonido extra, solo en victoria.
    LaunchedEffect(eventos, uiState.sfxEnabled, uiState.animationsEnabled) {
        eventos.collectLatest { evento ->

            DecentralizedLogger.i("Partida", "Evento recibido: $evento")

            if (evento is JuegoUiEvent.Victoria && evento.partida.resultado == Resultado.GANADO) {

                DecentralizedLogger.i("Partida", "Victoria detectada en PantallaPartida")

                val bitmap = view.drawToBitmap()
                val screenshotPath = guardarCapturaTemporal(context, bitmap)
                bitmap.recycle()

                val payload = VictoryCelebrationPayload.fromPartida(
                    partida = evento.partida,
                    screenshotPath = screenshotPath
                )
                onProgramarCelebracion(payload)

                // Confeti solo si las animaciones están activadas
                if (uiState.animationsEnabled) {
                    showConfetti = true
                    kotlinx.coroutines.delay(2500)
                    showConfetti = false
                }

                // Sonido extra de victoria, respetando el switch de SFX
                if (uiState.sfxEnabled) {
                    toneGenerator.startTone(
                        ToneGenerator.TONE_CDMA_HIGH_PBX_SLS,
                        300
                    )
                }
            }
        }
    }

    val boxSize = (dims.minSide * 0.8f).coerceAtMost(360.dp)
    val diceSize = boxSize * 0.7f
    val coinSize = (dims.minSide * 0.10f).coerceIn(24.dp, 48.dp)
    val fichaSize = (diceSize * 0.55f).coerceIn(32.dp, 72.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (!isDarkTheme) Color(0xFFD6E8CD)
                else MaterialTheme.colorScheme.background
            )
            .padding(horizontal = dims.spaceSm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(dims.spaceMd))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dims.spaceSm)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.moneda),
                    contentDescription = stringResource(id = R.string.game_coin_content_description),
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
                    Image(
                        painter = painterResource(id = R.drawable.dado_negro),
                        contentDescription = stringResource(id = R.string.game_dice_content_description),
                        modifier = Modifier
                            .size(diceSize)
                            .rotate(rotation.value),
                        contentScale = ContentScale.Fit
                    )

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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF96C78F)),
                    enabled = uiState.monedas > 0
                ) {
                    Text(
                        text = stringResource(id = R.string.game_roll),
                        fontWeight = FontWeight.Bold,
                        fontSize = dims.titleSp
                    )
                }

                Spacer(Modifier.height(dims.spaceSm))

                Text(
                    text = uiState.ultimoResultado.ifEmpty {
                        stringResource(id = R.string.game_last_roll_placeholder)
                    },
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
                    text = stringResource(id = R.string.game_back_to_menu),
                    color = Color(0xFF96C78F),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.bodySp
                )
            }

            Spacer(Modifier.height(dims.spaceMd))
        }

        @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
        if (showDialog && uiState.monedas > 0) {
            val chipSize = (dims.minSide * 0.20f).coerceIn(40.dp, 64.dp)
            val spacing = dims.spaceSm

            AlertDialog(
                onDismissRequest = { showDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dims.spaceMd),
                title = {
                    Text(
                        text = stringResource(id = R.string.game_pick_number_prompt),
                        fontSize = dims.bodySp
                    )
                },
                text = {
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing),
                        verticalArrangement = Arrangement.spacedBy(spacing)
                    ) {
                        (1..6).forEach { number ->
                            Button(
                                onClick = {
                                    if (uiState.monedas > 0) {
                                        onElegirNumero(number)
                                        showDialog = false
                                    }
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
                        Text(
                            text = stringResource(id = R.string.action_cancel),
                            fontSize = dims.bodySp
                        )
                    }
                }
            )
        }

        if (showConfetti && uiState.animationsEnabled) {
            // Capa de confeti + texto de victoria
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = dims.spaceMd * 16,
                        bottom = dims.spaceMd * 3
                    ),
                contentAlignment = Alignment.Center
            ) {
                VictoryText(dims = dims)
            }
            ConfettiOverlay(dims = dims, compact = dims.minSide < 360.dp)
        }
    }
}


/**
 * Guarda la captura de victoria en caché para que el worker la publique en la galería.
 *
 * @param context Contexto de la aplicación.
 * @param bitmap Imagen de la pantalla en el momento de la victoria.
 * @return Ruta absoluta del archivo temporal o `null` si falla.
 * @throws IllegalStateException No se lanza; errores se registran.
 * @security No persiste PII, solo la imagen generada por la UI.
 */
private suspend fun guardarCapturaTemporal(context: Context, bitmap: Bitmap): String? =
    withContext(Dispatchers.IO) {
        runCatching {
            val file = File(context.cacheDir, "victory_capture_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        }.onFailure { error ->
            DecentralizedLogger.e(TAG, "No se pudo guardar la captura temporal", error)
        }.getOrNull()
    }

private const val TAG = "PantallaPartida"

@Composable
private fun ConfettiOverlay(
    dims: UiDims,
    compact: Boolean
) {
    val confettiCount = if (compact) 70 else 110
    val colors = listOf(
        Color(0xFFFFC107),
        Color(0xFFFF4081),
        Color(0xFF69F0AE),
        Color(0xFF40C4FF),
        Color(0xFFFF5722)
    )

    val randomSeeds = remember {
        List(confettiCount) { i ->
            val base = (i * 37) % 1000
            base / 1000f
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
        )
    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height
        val p = progress.value

        repeat(confettiCount) { i ->
            val seed = randomSeeds[i]
            val baseX = width * seed
            val fallSpeed = 1.0f + seed * 0.5f
            val wave = kotlin.math.sin((p * 6f + seed * 10f)) * 30f

            val y = height * (-0.2f + p * fallSpeed)
            val x = (baseX + wave).coerceIn(0f, width)

            val color = colors[i % colors.size]
            val sizePx = (dims.minSide.toPx() * 0.016f).coerceIn(6f, 16f)

            drawRoundRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(x, y),
                size = androidx.compose.ui.geometry.Size(sizePx, sizePx * 1.8f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(sizePx / 3f)
            )
        }
    }
}

@Composable
private fun VictoryText(dims: UiDims) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.snapTo(0f)
        scale.snapTo(0.5f)
        alpha.animateTo(1f, tween(durationMillis = 200))
        scale.animateTo(1.1f, tween(durationMillis = 200))
        scale.animateTo(1f, tween(durationMillis = 150))
    }

    Text(
        text = stringResource(id = R.string.game_victory_confetti_label),
        color = Color(0xFF1B5E20),
        fontSize = (dims.titleSp.value * 1.4f).sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha.value
            scaleX = scale.value
            scaleY = scale.value
        }
    )
}