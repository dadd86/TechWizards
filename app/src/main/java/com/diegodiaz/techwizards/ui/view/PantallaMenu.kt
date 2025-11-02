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
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims

// ui/view/PantallaMenu.kt
@Composable
fun PantallaMenu(
    isDarkTheme: Boolean,
    onJugar: () -> Unit,
    onHistorial: () -> Unit,
    onAjustes: () -> Unit
) = Responsive { dims ->
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
            verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            Text("Menú principal", fontSize = dims.titleSp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(dims.spaceSm))
            MenuBoton("Jugar", dims, onJugar)
            MenuBoton("Historial", dims, onHistorial)
            MenuBoton("Ajustes", dims, onAjustes)
            MenuBoton("Salir", dims) { showDialog = true }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmar salida", fontSize = dims.bodySp) },
                    text  = { Text("¿Deseas salir de la aplicación?", fontSize = dims.bodySp) },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            (context as? Activity)?.finish()
                        }) { Text("Confirmar", fontSize = dims.bodySp) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) { Text("Cancelar", fontSize = dims.bodySp) }
                    }
                )
            }
        }
    }
}

@Composable
private fun MenuBoton(texto: String, dims: UiDims, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F3F3)),
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(dims.buttonHeightSm)
            .clip(RoundedCornerShape(16.dp))
    ) { Text(texto, fontSize = dims.bodySp, fontWeight = FontWeight.Bold, color = Color.Black) }
}
