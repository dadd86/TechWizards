package com.diegodiaz.techwizards.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import com.diegodiaz.techwizards.R
import com.diegodiaz.techwizards.ui.responsive.Responsive

// ui/view/PantallaBienvenida.kt
@Composable
fun PantallaBienvenida(
    isDarkTheme: Boolean,
    onJugar: () -> Unit
) = Responsive { dims ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFF7EC8E3) else MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dims.spaceLg)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(dims.imageLg * 1.25f)
                    .background(Color(0xFF5597CF), shape = CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dado),
                    contentDescription = "Dado",
                    modifier = Modifier.size(dims.imageLg)
                )
            }
            Text(
                text = "¡Bienvenido a\nJuegosAzar!",
                fontSize = dims.titleSp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = (dims.titleSp * 1.2f)
            )
            Button(
                onClick = onJugar,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(dims.buttonHeight)
                    .clip(RoundedCornerShape(dims.cardCorner)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("JUGAR", fontSize = dims.bodySp, color = Color(0xFF3B71B8), fontWeight = FontWeight.Bold)
            }
        }
    }
}
