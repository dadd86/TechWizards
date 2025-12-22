package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.ui.controller.ControladorRanking
import com.diegodiaz.techwizards.ui.controller.RankingUiState
import com.diegodiaz.techwizards.ui.responsive.UiDims
import androidx.compose.ui.res.stringResource
import com.diegodiaz.techwizards.R

/**
 * Pantalla para visualizar el top ten y el premio común con estados de carga.
 *
 * @security
 * Solo se muestran alias y puntuaciones públicas.
 */
@Composable
fun PantallaRanking(
    dims: UiDims,
    controlador: ControladorRanking,
    onVolver: () -> Unit
) {
    val estado by controlador.uiState.collectAsState()
    val descripcionPremio = remember { mutableStateOf("Nuevo premio para todos") }
    val valorPremio = remember { mutableStateOf("100") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dims.spaceMd),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Top Ten",
                style = MaterialTheme.typography.headlineSmall
            )
            TextButton(onClick = onVolver) { Text("Volver") }
        }

        when (val ui = estado) {
            RankingUiState.Cargando -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(dims.spaceSm))
                    Text(text = "Cargando ranking...")
                }
            }

            is RankingUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = ui.mensaje)
                    Spacer(modifier = Modifier.height(dims.spaceSm))
                    Button(onClick = controlador::refrescarTodo) { Text("Reintentar") }
                }
            }

            is RankingUiState.Exito -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(dims.spaceXs),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (ui.topTen.isEmpty()) {
                        item { Text(text = "Sin datos de ranking todavía") }
                    } else {
                        itemsIndexed(ui.topTen) { index, entry ->
                            EntradaRanking(
                                posicion = entry.position ?: (index + 1),
                                entry = entry,
                                dims = dims
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(dims.spaceSm))
                    }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(dims.spaceSm),
                                verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
                            ) {
                                Text(
                                    text = "Premio común",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = ui.premio?.descripcion ?: "Sin premio publicado")
                                Text(text = "Valor: ${ui.premio?.valor ?: 0}")
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(dims.spaceXs)
                                ) {
                                    Button(onClick = controlador::refrescarPremio) {
                                        Text("Refrescar premio")
                                    }
                                    Button(
                                        onClick = controlador::refrescarTodo
                                    ) { Text("Actualizar ranking") }
                                }

                                if (ui.puedeActualizarPremio) {
                                    OutlinedTextField(
                                        value = descripcionPremio.value,
                                        onValueChange = { descripcionPremio.value = it },
                                        label = { Text("Descripción") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = valorPremio.value,
                                        onValueChange = { valorPremio.value = it },
                                        label = { Text("Valor") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(
                                        onClick = {
                                            val valor = valorPremio.value.toIntOrNull() ?: 0
                                            controlador.actualizarPremio(descripcionPremio.value, valor)
                                        }
                                    ) {
                                        Text("Guardar premio")
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EntradaRanking(posicion: Int, entry: LeaderboardEntry, dims: UiDims) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dims.spaceSm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs)) {
                Text(text = "#$posicion ${entry.alias}")
                entry.wins?.let { wins ->
                    Text(text = stringResource(id = R.string.ranking_wins, wins))
                }
            }
            Text(text = "${entry.score} pts", fontWeight = FontWeight.SemiBold)
        }
    }
}