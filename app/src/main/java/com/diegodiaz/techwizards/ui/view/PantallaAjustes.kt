/*package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PantallaAjustes.kt
 *
 * Pantalla sencilla para activar o desactivar sonido y vibración.
 * Por ahora solo guarda los valores en memoria (no persiste aún).
 */
@Composable
fun PantallaAjustes(onBack: () -> Unit) {
    var sonidoActivo by remember { mutableStateOf(true) }
    var vibracionActiva by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("⬅️") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Preferencias del jugador", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Sonido activado")
                Switch(checked = sonidoActivo, onCheckedChange = { sonidoActivo = it })
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Vibración activada")
                Switch(checked = vibracionActiva, onCheckedChange = { vibracionActiva = it })
            }

            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Guardar y volver")
            }
        }
    }
}
*/