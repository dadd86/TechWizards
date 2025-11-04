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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.data.local.entity.Resultado
import com.diegodiaz.techwizards.domain.model.Partida
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims
/**
 * Renderiza el historial de partidas mostrando alias, resultado y variación de monedas.
 *
 * @param isDarkTheme Indica si se usa tema oscuro.
 * @param historial Listado de partidas en orden descendente.
 * @param onVolverAlMenu Acción al pulsar el botón de regreso.
 * @return Unit al tratarse de una función composable.
 * @throws IllegalStateException No lanza excepciones; se limita a componer UI.
 * @security
 * - Solo muestra alias locales y resultados almacenados en Room.
 */
@Composable
fun PantallaHistorial(
    isDarkTheme: Boolean,
    historial: List<Partida>,
    onVolverAlMenu: () -> Unit
) = Responsive { dims ->
    val fondo = if (!isDarkTheme) Color(0xFFF6F7F8) else MaterialTheme.colorScheme.background
    val cardBg = if (!isDarkTheme) Color.White else MaterialTheme.colorScheme.surface
    val listState = rememberLazyListState()

    // Pesos de columnas ajustados por el lado corto (para móviles muy estrechos)
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
                        text = "Volver al menú",
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
            Text(
                text = "Historial de partidas",
                fontSize = dims.titleSp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dims.spaceSm)
            )

            // CARD ÚNICO: encabezado + lista dentro del mismo Surface
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = cardBg,
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // Encabezado (dentro del mismo card)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFECEFF1))
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .padding(horizontal = dims.spaceSm, vertical = dims.spaceXs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeaderCell(
                            text = "Jugador",
                            modifier = Modifier.weight(pesos.jugador, fill = true),
                            size = if (dims.minSide < 360.dp) dims.bodySp * 0.9f else dims.bodySp,
                            align = TextAlign.Start
                        )
                        HeaderCell(
                            text = "Fecha",
                            modifier = Modifier.weight(pesos.fecha, fill = true),
                            size = if (dims.minSide < 360.dp) dims.bodySp * 0.9f else dims.bodySp,
                            align = TextAlign.Start
                        )
                        HeaderCell(
                            text = "Resultado",
                            modifier = Modifier.weight(pesos.resultado, fill = true),
                            size = if (dims.minSide < 360.dp) dims.bodySp * 0.9f else dims.bodySp,
                            align = TextAlign.Center
                        )
                        HeaderCell(
                            text = "Monedas",
                            modifier = Modifier.weight(pesos.deltaMonedas, fill = true),
                            size = if (dims.minSide < 360.dp) dims.bodySp * 0.9f else dims.bodySp,
                            align = TextAlign.End
                        )
                    }

                    HorizontalDivider(color = Color(0x14000000), thickness = 1.dp)

                    if (historial.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dims.spaceLg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Sin partidas todavía",
                                fontSize = dims.bodySp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Solo las filas hacen scroll (el header permanece dentro del card)
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
                                        .background(if (isOdd) Color(0xFFF7F7F7) else Color.Transparent)
                                        .padding(horizontal = 0.dp, vertical = dims.spaceXs),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BodyCell(
                                        text = partida.aliasJugador,
                                        modifier = Modifier.weight(pesos.jugador, fill = true),
                                        dims = dims,
                                        align = TextAlign.Start,
                                        bold = true
                                    )
                                    BodyCell(
                                        text = partida.fecha.formateaComoFecha(),
                                        modifier = Modifier.weight(pesos.fecha, fill = true),
                                        dims = dims,
                                        align = TextAlign.Start
                                    )
                                    BodyCell(
                                        text = if (partida.resultado == Resultado.GANADO) "Ganó" else "Perdió",
                                        modifier = Modifier.weight(pesos.resultado, fill = true),
                                        dims = dims,
                                        align = TextAlign.Center
                                    )
                                    BodyCell(
                                        text = if (partida.deltaMonedas >= 0) "+${partida.deltaMonedas}" else partida.deltaMonedas.toString(),
                                        modifier = Modifier.weight(pesos.deltaMonedas, fill = true),
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

/* ----------------------------- Celdas ----------------------------- */
/**
 * Crea una celda de encabezado reutilizable para el historial.
 *
 * @param text Texto del encabezado.
 * @param modifier Modificador aplicado al contenedor.
 * @param size Tamaño de fuente calculado.
 * @param align Alineación del texto.
 * @return Unit ya que únicamente emite UI.
 * @throws IllegalArgumentException No lanza excepciones; parámetros se validan previamente.
 * @security
 * - No manipula datos sensibles.
 */
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
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}
/**
 * Crea una celda de contenido para cada fila del historial.
 *
 * @param text Texto a mostrar.
 * @param modifier Modificador aplicado al texto.
 * @param dims Dimensiones responsivas calculadas.
 * @param align Alineación del texto.
 * @param bold Indica si el texto se muestra en negrita.
 * @return Unit porque solo compone UI.
 * @throws IllegalArgumentException No lanza excepciones; entradas inválidas se muestran tal cual.
 * @security
 * - Presenta únicamente información local de partidas.
 */
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
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}

/* ---------------------------- Utilidades ---------------------------- */

private data class PesosColumnas(
    val jugador: Float,
    val fecha: Float,
    val resultado: Float,
    val deltaMonedas: Float
)

/**
 * Ajusta los pesos de las columnas según el ancho disponible.
 *
 * @param minSide Lado mínimo detectado por la capa responsiva.
 * @return Pesos adecuados para cada columna.
 * @throws IllegalArgumentException No lanza excepciones; se usan valores constantes.
 * @security
 * - Función puramente de layout, no procesa datos sensibles.
 */
private fun pesosPorAncho(minSide: Dp): PesosColumnas =
    if (minSide < 360.dp) {
        PesosColumnas(jugador = 0.28f, fecha = 0.30f, resultado = 0.22f, deltaMonedas = 0.20f)
    } else {
        PesosColumnas(jugador = 0.26f, fecha = 0.30f, resultado = 0.24f, deltaMonedas = 0.20f)
    }

/**
 * Formatea una marca de tiempo en formato corto amigable.
 *
 * @return Cadena formateada en `dd/MM/yyyy`.
 * @throws IllegalArgumentException Podría lanzar si la configuración regional no es válida (controlado por JVM).
 * @security
 * - No expone información adicional; solo transforma fechas.
 */
fun Long.formateaComoFecha(): String {
    val date = java.util.Date(this)
    val format = java.text.SimpleDateFormat("dd/MM/yyyy")
    return format.format(date)
}
