// ui/view/PantallaJugar.kt
package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.foundation.layout.Arrangement


@Composable
fun PantallaJugar(onBack: () -> Unit) {
    var resultado by remember { mutableStateOf(0) }
    var monedas by remember { mutableStateOf(100) }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎲 Lanza el dado y prueba suerte", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(30.dp))
        Text("Resultado: $resultado")
        Spacer(Modifier.height(10.dp))
        Text("Monedas: $monedas")
        Spacer(Modifier.height(30.dp))
        Button(onClick = {
            val dado = Random.nextInt(1, 7)
            resultado = dado
            monedas += if (dado >= 4) 10 else -5
        }) { Text("🎯 Lanzar dado") }
        Spacer(Modifier.height(40.dp))
        Button(onClick = onBack) { Text("Volver al menú") }
    }
}
