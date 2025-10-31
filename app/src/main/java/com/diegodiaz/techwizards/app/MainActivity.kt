package com.diegodiaz.techwizards.app

import android.os.Bundle
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diegodiaz.techwizards.ui.theme.TechWizardsTheme
import com.diegodiaz.techwizards.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Tema claro u oscuro
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            TechWizardsTheme (darkTheme = isDarkTheme){
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "bienvenida"
                ) {
                    composable("bienvenida") {
                        PantallaBienvenida(
                            isDarkTheme = isDarkTheme,
                            onJugar = { navController.navigate("menu") }
                        )
                    }
                    composable("menu") {
                        PantallaMenu(
                            isDarkTheme = isDarkTheme,
                            onJugar = { navController.navigate("partida") },
                            onHistorial = { navController.navigate("historial") },
                            onAjustes = { navController.navigate("ajustes") }
                        )
                    }
                    composable("partida") {
                        PantallaPartida(
                            isDarkTheme = isDarkTheme,
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }
                    composable("historial") {
                        PantallaHistorial(
                            isDarkTheme = isDarkTheme,
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }
                    composable("ajustes") {
                        PantallaAjustes(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = it },
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }
                }
            }
        }
    }


    @Composable
    fun PantallaPartida(isDarkTheme: Boolean, onVolverAlMenu: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (!isDarkTheme) Color(0xFFD6E8CD)
                else MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(Modifier.height(32.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.93f)
                        .padding(start = 8.dp, bottom = 12.dp)
                ) {
                    //Monedas
                    Image(
                        painter = painterResource(id = R.drawable.moneda),
                        contentDescription = "Moneda",
                        modifier = Modifier.size(38.dp)
                    )
                    Text(
                        text = " 100",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(Modifier.height(18.dp))
                //Dado
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .background(Color(0xFFF9F9EB), shape = RoundedCornerShape(36.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dado_negro),
                        contentDescription = "Dado_negro",
                        modifier = Modifier.size(180.dp)
                    )
                }
                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = { /* ACCIÓN PARA LANZAR*/ },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(72.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color(0xFF96C78F)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF96C78F))
                ) {
                    Text(
                        text = "Lanzar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Último lanzamiento: Ganó", // CAMBIAR!!!!
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Botón de volver al menú
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onVolverAlMenu,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .clip(RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Volver al menú",
                        color = Color(0xFF96C78F),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }

            }
