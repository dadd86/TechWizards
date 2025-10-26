package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

/**
 * PantallaJugar.kt
 *
 * Aquí está la lógica básica del juego de azar.
 * Simula un lanzamiento de dado y actualiza las monedas según el resultado.
 */
@Composable
fun PantallaJugar(onBack: () -> Unit) {
    // Guarda el resultado del dado
    var resultado by remember { mutableStateOf(0) }

    // Monedas del jugador (por ahora simuladas)
    var monedas by remember { mutableStateOf(100) }

    // Lógica del juego: tirar el dado y actualizar monedas
    fun lanzarDado() {
        val dado = Random.nextInt(1, 7) // número entre 1 y 6
        resultado = dado
        monedas += if (dado >= 4) 10 else -5
    }

    // Interfaz visual
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎲 Lanza el dado y prueba suerte", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(30.dp))

        Text("Resultado: $resultado", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(10.dp))

        Text("Monedas: $monedas", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = { lanzarDado() }) { Text("🎯 Lanzar dado") }

        Spacer(modifier = Modifier.height(40.dp))
        Button(onClick = onBack) { Text("Volver al menú") }
    }
}
