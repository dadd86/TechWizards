package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.domain.model.LeaderboardEntry
import com.diegodiaz.techwizards.ui.controller.MatchOnlineUiState
import com.diegodiaz.techwizards.ui.responsive.UiDims
@Composable
fun PantallaMatch(
    dims: UiDims,
    uiState: MatchOnlineUiState,
    onSeleccionCara: (Int) -> Unit,
    onConfirmarApuesta: () -> Unit,
    onLanzarDado: () -> Unit,
    onBuscarRival: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progreso = uiState.progresoPremio
    val ambosListos = uiState.localListo && uiState.remotoListo && uiState.carasSeleccionadas.size >= 2
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dims.spaceMd, vertical = dims.spaceSm)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        TextButton(onClick = onVolver) {
            Text(text = stringResource(id = R.string.game_back_to_menu))
        }

        Text(
            text = stringResource(id = R.string.match_title),
            fontSize = dims.titleSp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(id = R.string.match_subtitle_online, uiState.matchId ?: "—"),
            fontSize = dims.bodySp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        uiState.error?.let { mensaje ->
            Text(
                text = mensaje,
                fontSize = dims.bodySp,
                color = MaterialTheme.colorScheme.error
            )
        }

        PrizeProgressCard(
            progreso = progreso,
            premioDescripcion = uiState.premioComun?.descripcion,
            premioValor = uiState.premioComun?.valor,
            dims = dims
        )

        LeaderboardCard(topTen = uiState.topTen, dims = dims)

        MatchmakingRow(
            remotoListo = uiState.remotoListo,
            buscandoRival = uiState.buscandoRival,
            onBuscarRival = onBuscarRival,
            dims = dims
        )

        SeleccionCara(
            seleccionada = uiState.seleccionCara,
            dims = dims,
            onSeleccionCara = onSeleccionCara
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            Button(
                onClick = onConfirmarApuesta,
                modifier = Modifier
                    .weight(1f)
                    .height(dims.buttonHeightSm),
                enabled = !uiState.localListo,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.match_cta_listo),
                    fontSize = dims.bodySp
                )
            }

            Button(
                onClick = onLanzarDado,
                modifier = Modifier
                    .weight(1f)
                    .height(dims.buttonHeightSm),
                enabled = ambosListos,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = stringResource(id = R.string.match_roll),
                    fontSize = dims.bodySp
                )
            }
        }

        ResultadoDado(uiState, dims)

        Divider()

        uiState.participantes.forEach { participante ->
            val score = uiState.puntuaciones.firstOrNull { it.usuarioNumero == participante.usuarioNumero }?.score ?: 0
            MatchCard(
                nombre = participante.rol ?: "Player ${participante.usuarioNumero}",
                puntos = score,
                caraElegida = uiState.carasSeleccionadas[participante.usuarioNumero],
                lanzamiento = uiState.lanzamientos[participante.usuarioNumero],
                dims = dims
            )
        }


        Spacer(modifier = Modifier.height(dims.spaceSm))

        Button(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(dims.buttonHeightSm)
        ) {
            Text(text = stringResource(id = R.string.common_back), fontSize = dims.bodySp)
        }
    }
}

@Composable
private fun MatchmakingRow(
    remotoListo: Boolean,
    buscandoRival: Boolean,
    onBuscarRival: () -> Unit,
    dims: UiDims
) {
    val color = if (remotoListo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val texto = if (remotoListo) R.string.match_remote_ready else R.string.match_remote_waiting
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, color, RoundedCornerShape(12.dp))
                .padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dims.spaceXs)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = stringResource(id = texto),
                fontSize = dims.bodySp,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (!remotoListo) {
            Button(
                onClick = onBuscarRival,
                enabled = !buscandoRival,
                modifier = Modifier.height(dims.buttonHeightSm),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(
                        id = if (buscandoRival) R.string.match_searching_rival else R.string.match_search_rival
                    ),
                    fontSize = dims.bodySp
                )
            }
        }
    }
}

@Composable
private fun SeleccionCara(
    seleccionada: Int,
    dims: UiDims,
    onSeleccionCara: (Int) -> Unit
) {
    Text(
        text = stringResource(id = R.string.match_choose_face),
        fontSize = dims.bodySp,
        fontWeight = FontWeight.SemiBold
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dims.spaceXs)
    ) {
        (1..6).forEach { cara ->
            val isSelected = seleccionada == cara
            val background = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            Text(
                text = cara.toString(),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                    .background(background)
                    .clickable { onSeleccionCara(cara) }
                    .padding(vertical = dims.spaceSm),
                fontSize = dims.bodySp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ResultadoDado(uiState: MatchOnlineUiState, dims: UiDims) {
    val resultado = uiState.resultadoDado
    if (resultado == null && uiState.lanzamientos.isEmpty()) {
        Text(
            text = stringResource(id = R.string.match_no_roll),
            fontSize = dims.bodySp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        val valorMostrar = resultado ?: uiState.lanzamientos.values.maxOrNull().orEmptyValue()
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
                verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
            ) {
                Text(
                    text = stringResource(id = R.string.match_roll_result, valorMostrar),
                    fontSize = dims.bodySp,
                    fontWeight = FontWeight.Bold
                )
                uiState.lanzamientos.forEach { (jugador, valor) ->
                    Text(
                        text = stringResource(id = R.string.match_roll_player_result, jugador, valor),
                        fontSize = dims.bodySp
                    )
                }
                when {
                    uiState.huboEmpate -> {
                        Text(
                            text = stringResource(id = R.string.match_round_draw),
                            fontSize = dims.bodySp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    uiState.ganadorRonda != null -> {
                        Text(
                            text = stringResource(id = R.string.match_round_winner, uiState.ganadorRonda),
                            fontSize = dims.bodySp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    uiState.localListo && uiState.remotoListo -> {
                        Text(
                            text = stringResource(id = R.string.match_wait_remote_roll),
                            fontSize = dims.bodySp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchCard(
    nombre: String,
    puntos: Int,
    caraElegida: Int? = null,
    lanzamiento: Int? = null,
    dims: UiDims
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(dims.spaceXs)) {
                Text(
                    text = nombre,
                    fontSize = dims.bodySp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.match_points, puntos),
                    fontSize = dims.bodySp
                )
                caraElegida?.let {
                    Text(
                        text = stringResource(id = R.string.match_selected_face, it),
                        fontSize = dims.bodySp
                    )
                }
                lanzamiento?.let {
                    Text(
                        text = stringResource(id = R.string.match_last_roll, it),
                        fontSize = dims.bodySp
                    )
                }
            }
        }
    }
}

private fun Int?.orEmptyValue(): Int = this ?: 0
@Composable
private fun PrizeProgressCard(
    progreso: Float,
    premioDescripcion: String?,
    premioValor: Int?,
    dims: UiDims
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
            verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
        ) {
            Text(
                text = "Premio en juego",
                fontSize = dims.bodySp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = premioDescripcion ?: "Sin premio publicado",
                fontSize = dims.bodySp
            )
            Text(
                text = "Valor: ${premioValor ?: 0} pts",
                fontSize = dims.bodySp
            )
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Avance: ${(progreso * 100).toInt()}% hacia el premio",
                fontSize = dims.bodySp
            )
        }
    }
}

@Composable
private fun LeaderboardCard(topTen: List<LeaderboardEntry>, dims: UiDims) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
            verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
        ) {
            Text(
                text = "Top ten online",
                fontSize = dims.bodySp,
                fontWeight = FontWeight.Bold
            )
            if (topTen.isEmpty()) {
                Text(
                    text = "Aún no hay puntuaciones en línea",
                    fontSize = dims.bodySp
                )
            } else {
                topTen.forEachIndexed { index, entry ->
                    MatchCard(
                        nombre = "${index + 1}. ${entry.alias}",
                        puntos = entry.score,
                        dims = dims
                    )
                }
            }
        }
    }
}