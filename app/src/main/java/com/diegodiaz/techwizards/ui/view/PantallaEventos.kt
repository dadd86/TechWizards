package com.diegodiaz.techwizards.ui.view
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims

@Composable fun PantallaEventos() = Responsive { dims ->
    val eventos = listOf(
        Triple(stringResource(id = R.string.events_daily_challenge), 0.6f, stringResource(id = R.string.events_progress_label)),
        Triple(stringResource(id = R.string.events_weekend_tournament), 0.3f, stringResource(id = R.string.events_progress_label)),
        Triple(stringResource(id = R.string.events_special_drop), 0.9f, stringResource(id = R.string.events_progress_label))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dims.spaceMd, vertical = dims.spaceSm),
        verticalArrangement = Arrangement.spacedBy(dims.spaceSm)
    ) {
        Text(
            text = stringResource(id = R.string.events_title),
            fontSize = dims.titleSp,
            fontWeight = FontWeight.Bold
        )

        eventos.forEach { (titulo, progreso, etiqueta) ->
            EventCard(titulo = titulo, progreso = progreso, etiqueta = etiqueta, dims = dims)
        }
    }
}

@Composable
private fun EventCard(titulo: String, progreso: Float, etiqueta: String, dims: UiDims) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dims.spaceSm, vertical = dims.spaceSm),
            verticalArrangement = Arrangement.spacedBy(dims.spaceXs)
        ) {
            Text(text = titulo, fontSize = dims.bodySp, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier.fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surface,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = etiqueta, fontSize = dims.bodySp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}