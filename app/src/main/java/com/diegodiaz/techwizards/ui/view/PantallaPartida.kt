package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.controller.JuegoUiState

@Composable
fun PantallaPartida(
    isDarkTheme: Boolean,
    uiState: JuegoUiState,
    onVolverAlMenu: () -> Unit,
    onElegirNumero: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFFD6E8CD) else MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(0.93f)
                    .padding(start = 8.dp, bottom = 12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.moneda),
                    contentDescription = "Moneda",
                    modifier = Modifier.size(38.dp)
                )
                Text(
                    text = " ${uiState.monedas}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .background(Color(0xFFF9F9EB), shape = RoundedCornerShape(36.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dado_negro),
                    contentDescription = "Dado_negro",
                    modifier = Modifier.size(180.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(72.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF96C78F)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF96C78F))
            ) {
                Text(
                    text = "Lanzar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = uiState.ultimoResultado.ifEmpty { "Último lanzamiento: ..." },
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onVolverAlMenu,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Volver al menú",
                    color = Color(0xFF96C78F),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Elige un número del 1 al 6") },
            text = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    (1..6).forEach { number ->
                        Button(
                            onClick = {
                                onElegirNumero(number)
                                showDialog = false
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF5C6BC0),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(40.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = number.toString(),
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
