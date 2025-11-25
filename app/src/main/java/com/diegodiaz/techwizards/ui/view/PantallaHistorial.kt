package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.ui.responsive.UiDims
import java.text.DateFormat
import java.util.Date

@Composable
fun PantallaHistorial(
    isDarkTheme: Boolean,
    historial: List<Partida>,
    onVolverAlMenu: () -> Unit,
    dims: UiDims
) {
    val fondo = if (!isDarkTheme) Color(0xFFF6F7F8) else MaterialTheme.colorScheme.background
    val cardBg = if (!isDarkTheme) Color.White else MaterialTheme.colorScheme.surface
    val listState = rememberLazyListState()

    // Pesos de columnas según ancho
    val pesos = pesosPorAncho(dims.minSide)

    Scaffold(
        containerColor = fondo,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dims.spaceSm),
                contentAlignment = Alignment.Center
            ) {
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
                        color = Color.Black,
                        fontSize = dims.bodySp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(
                    start = dims.spaceMd,
                    end = dims.spaceMd,
                    top = dims.spaceSm,
                    bottom = dims.buttonHeightSm + dims.spaceLg
                )
        ) {
            // TITULO
            Text(
                text = stringResource(id = R.string.history_title),
                fontSize = dims.titleSp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dims.spaceSm)
            )

            // CARD grande
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = cardBg,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // ENCABEZADO
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFECEFF1))
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeaderCell(
                            text = stringResource(id = R.string.history_player),
                            modifier = Modifier.weight(pesos.jugador),
                            size = dims.bodySp,
                            align = TextAlign.Start
                        )
                        HeaderCell(
                            text = stringResource(id = R.string.history_date),
                            modifier = Modifier.weight(pesos.fecha),
                            size = dims.bodySp,
                            align = TextAlign.Start
                        )
                        HeaderCell(
                            text = stringResource(id = R.string.history_result),
                            modifier = Modifier.weight(pesos.resultado),
                            size = dims.bodySp,
                            align = TextAlign.Center
                        )
                        HeaderCell(
                            text = stringResource(id = R.string.history_coins),
                            modifier = Modifier.weight(pesos.deltaMonedas),
                            size = dims.bodySp,
                            align = TextAlign.End
                        )
                    }

                    HorizontalDivider(color = Color(0x14000000), thickness = 1.dp)

                    // LISTA
                    if (historial.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dims.spaceLg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(id = R.string.history_empty),
                                fontSize = dims.bodySp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(
                                start = dims.spaceSm,
                                end = dims.spaceSm,
                                top = dims.spaceXs,
                                bottom = dims.spaceXs
                            )
                        ) {
                            itemsIndexed(historial) { index, partida ->
                                val isOdd = index % 2 == 1

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isOdd) Color(0xFFF7F7F7)
                                            else Color.Transparent
                                        )
                                        .padding(vertical = dims.spaceXs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BodyCell(
                                        text = partida.aliasJugador,
                                        modifier = Modifier.weight(pesos.jugador),
                                        dims = dims,
                                        align = TextAlign.Start,
                                        bold = true
                                    )

                                    BodyCell(
                                        text = partida.fecha.formateaComoFecha(),
                                        modifier = Modifier.weight(pesos.fecha),
                                        dims = dims,
                                        align = TextAlign.Start
                                    )

                                    BodyCell(
                                        text = if (partida.resultado == Resultado.GANADO)
                                            stringResource(id = R.string.history_win)
                                        else stringResource(id = R.string.history_loss),
                                        modifier = Modifier.weight(pesos.resultado),
                                        dims = dims,
                                        align = TextAlign.Center
                                    )

                                    BodyCell(
                                        text =
                                            if (partida.deltaMonedas >= 0)
                                                "+${partida.deltaMonedas}"
                                            else partida.deltaMonedas.toString(),
                                        modifier = Modifier.weight(pesos.deltaMonedas),
                                        dims = dims,
                                        align = TextAlign.End
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ----------------------------- CELDAS ----------------------------- */

@Composable
private fun HeaderCell(
    text: String,
    modifier: Modifier,
    size: androidx.compose.ui.unit.TextUnit,
    align: TextAlign
) {
    Text(
        text = text,
        modifier = modifier.padding(vertical = 2.dp, horizontal = 4.dp),
        fontWeight = FontWeight.SemiBold,
        fontSize = size,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = align,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun BodyCell(
    text: String,
    modifier: Modifier,
    dims: UiDims,
    align: TextAlign = TextAlign.Start,
    bold: Boolean = false
) {
    Text(
        text = text,
        modifier = modifier.padding(vertical = dims.spaceXs, horizontal = dims.spaceXs),
        fontSize = dims.bodySp,
        fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = align,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/* ----------------------------- UTILIDADES ----------------------------- */

private data class PesosColumnas(
    val jugador: Float,
    val fecha: Float,
    val resultado: Float,
    val deltaMonedas: Float
)

private fun pesosPorAncho(minSide: Dp): PesosColumnas =
    if (minSide < 360.dp) {
        PesosColumnas(0.28f, 0.30f, 0.22f, 0.20f)
    } else {
        PesosColumnas(0.26f, 0.30f, 0.24f, 0.20f)
    }

fun Long.formateaComoFecha(): String {
    val date = Date(this)
    val format = DateFormat.getDateInstance(DateFormat.MEDIUM)
    return format.format(date)
}
