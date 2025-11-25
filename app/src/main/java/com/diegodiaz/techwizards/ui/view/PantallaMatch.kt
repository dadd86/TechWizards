package com.diegodiaz.techwizards.ui.view
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims

@Composable fun PantallaMatch() = Responsive { dims ->
    val jugadores = remember {
        listOf(
            "Ana" to 12,
            "Luis" to 9,
            "Sara" to 15
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dims.spaceMd, vertical = dims.spaceSm),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        Text(
            text = stringResource(id = R.string.match_title),
            fontSize = dims.titleSp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(id = R.string.match_subtitle),
            fontSize = dims.bodySp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        jugadores.forEach { (nombre, puntos) ->
            MatchCard(nombre = nombre, puntos = puntos, dims = dims)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dims.spaceSm)
        ) {
            Button(
                onClick = { /* TODO iniciar */ },
                modifier = Modifier
                    .weight(1f)
                    .height(dims.buttonHeightSm),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text(stringResource(id = R.string.match_start), fontSize = dims.bodySp) }
            Button(
                onClick = { /* TODO finalizar */ },
                modifier = Modifier
                    .weight(1f)
                    .height(dims.buttonHeightSm),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) { Text(stringResource(id = R.string.match_finish), fontSize = dims.bodySp) }
        }
    }
}

@Composable
private fun MatchCard(nombre: String, puntos: Int, dims: UiDims) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                Text(text = nombre, fontSize = dims.bodySp, fontWeight = FontWeight.Bold)
                Text(text = stringResource(id = R.string.match_points, puntos), fontSize = dims.bodySp)
            }
            Button(
                onClick = { /* TODO sumar puntos */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(stringResource(id = R.string.match_add_points), fontSize = dims.bodySp)
            }
        }
    }
}