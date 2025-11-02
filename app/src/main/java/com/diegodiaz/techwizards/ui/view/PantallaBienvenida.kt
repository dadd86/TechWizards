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

@Composable
fun PantallaBienvenida(
    isDarkTheme: Boolean,
    onJugar: () -> Unit
)  = Responsive { dims ->
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(if (!isDarkTheme) Color(0xFF7EC8E3) else MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = Color(0xFF5597CF),
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dado),
                    contentDescription = "Dado",
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                text = "¡Bienvenido a\nJuegosAzar!",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onJugar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
            ) {
                Text(
                    text = "JUGAR",
                    fontSize = 22.sp,
                    color = Color(0xFF3B71B8),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
