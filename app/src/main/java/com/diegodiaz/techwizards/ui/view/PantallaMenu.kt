package com.diegodiaz.techwizards.ui.view

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.UiDims
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.diegodiaz.techwizards.domain.model.Usuario
import com.diegodiaz.techwizards.ui.controller.ControladorPartida


@Composable
fun PantallaMenu(
    isDarkTheme: Boolean,
    onJugar: () -> Unit,
    onHistorial: () -> Unit,
    onRanking: () -> Unit,
    onAjustes: () -> Unit,
    onAyuda: () -> Unit,
    //onLobby: () -> Unit,
    //onChat: () -> Unit,
    //onEventos: () -> Unit,
    //onMatch: () -> Unit,
    dims: UiDims,
    controladorPartida: ControladorPartida,
    usuario: Usuario?
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val saldo by controladorPartida.saldo.collectAsState()

    Text(
        text = "Monedas actuales: $saldo",
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (!isDarkTheme) Color(0xFFF7F7F7)
                else MaterialTheme.colorScheme.background
            ),
        contentAlignment = Alignment.Center
    ) {

        // ⬇️ Aquí añadimos SCROLL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = dims.spaceMd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {

            Text(
                text = stringResource(id = R.string.menu_title),
                fontSize = dims.titleSp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(dims.spaceSm))

            MenuBoton(stringResource(id = R.string.menu_play), dims, onJugar)
            MenuBoton(stringResource(id = R.string.menu_history), dims, onHistorial)
            MenuBoton(texto = "Top Ten", dims = dims, onClick = onRanking)
            MenuBoton(stringResource(id = R.string.menu_settings), dims, onAjustes)
            MenuBoton(stringResource(id = R.string.menu_help), dims, onAyuda)
            //MenuBoton(stringResource(id = R.string.lobby_title), dims, onLobby)
            //MenuBoton(stringResource(id = R.string.chat_title), dims, onChat)
            //MenuBoton(stringResource(id = R.string.events_title), dims, onEventos)
            //MenuBoton(stringResource(id = R.string.match_title), dims, onMatch)

            // 🚪 Salir
            MenuBoton(texto = stringResource(id = R.string.menu_exit), dims = dims) {
                showDialog = true
            }

            if (usuario != null) {
                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(0.7f)
                        .clip(RoundedCornerShape(16.dp)),
                    onClick = { controladorPartida.resetMonedas(usuario, 100) }
                ) {
                    Text("Reiniciar monedas")
                }
            }

            Spacer(Modifier.height(dims.spaceMd)) // Margen final extra

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = {
                        Text(
                            text = stringResource(id = R.string.menu_exit_confirm_title),
                            fontSize = dims.bodySp
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.menu_exit_confirm_message),
                            fontSize = dims.bodySp
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                (context as? Activity)?.finish()
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.menu_exit_confirm_accept),
                                fontSize = dims.bodySp
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text(
                                text = stringResource(id = R.string.menu_exit_confirm_cancel),
                                fontSize = dims.bodySp
                            )
                        }
                    }
                )
            }
        }
    }
}


@Composable
private fun MenuBoton(
    texto: String,
    dims: UiDims,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF3F3F3),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(dims.buttonHeightSm)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Text(
            text = texto,
            fontSize = dims.bodySp,
            fontWeight = FontWeight.Bold
        )
    }
}
