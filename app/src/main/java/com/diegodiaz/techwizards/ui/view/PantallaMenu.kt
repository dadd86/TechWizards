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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PantallaMenu(
    isDarkTheme: Boolean,
    onJugar: () -> Unit,
    onHistorial: () -> Unit,
    onAjustes: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFFF7F7F7) else MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Menú principal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(16.dp))
            MenuBoton("Jugar", onClick = onJugar)
            MenuBoton("Historial", onClick = onHistorial)
            MenuBoton("Ajustes", onClick = onAjustes)
            MenuBoton("Salir", onClick = { showDialog = true })

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmar salida") },
                    text = { Text("¿Deseas salir de la aplicación?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            (context as? Activity)?.finish()
                        }) {
                            Text("Confirmar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MenuBoton(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F3F3)),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        Text(
            text = texto,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}
