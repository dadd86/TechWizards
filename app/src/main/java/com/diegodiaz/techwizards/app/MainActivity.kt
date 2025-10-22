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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diegodiaz.techwizards.ui.theme.TechWizardsTheme
import com.diegodiaz.techwizards.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TechWizardsTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "bienvenida"
                ) {
                    composable("bienvenida") {
                        PantallaBienvenida(
                            onJugar = { navController.navigate("menu") }
                        )
                    }
                    composable("menu") {
                        PantallaMenu(
                            onJugar = { navController.navigate("partida") },
                            onHistorial = { navController.navigate("historial") },
                            onAjustes = { navController.navigate("ajustes") }
                        )
                    }
                    composable("partida") {
                        PantallaPartida(
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }
                    composable("historial") {
                        PantallaHistorial(
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }
                    composable("ajustes") {
                        PantallaAjustes(
                            onVolverAlMenu = { navController.navigate("menu") }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PantallaBienvenida(onJugar: () -> Unit) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF7EC8E3)) //Azul claro
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
                            color = Color(0xFF5597CF), // Azul círculo
                            shape = CircleShape
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dado), //Referencia imagen del dado
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
                        color = Color(0xFF3B71B8), //Azul
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    @Composable
    fun PantallaMenu(
        onJugar: () -> Unit,
        onHistorial: () -> Unit = {},
        onAjustes: () -> Unit = {}
    ) {
        // Estado para el diálogo
        var showDialog by remember { mutableStateOf(false) }

        // Acceso al contexto (para cerrar la Activity)
        val context = LocalContext.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F7F7)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Menú principal",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(Modifier.height(16.dp))
                MenuBoton("Jugar", onClick = onJugar)
                MenuBoton("Historial", onClick = onHistorial)
                MenuBoton("Ajustes", onClick = onAjustes)
                MenuBoton("Salir", onClick = { showDialog = true })

                // Muestra el AlertDialog FUERA del Column, pero dentro del Box
                if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text("Confirmar salida") },
                        text = { Text("¿Deseas salir de la aplicación?") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDialog = false
                                (context as? Activity)?.finish()
                            }) {
                                Text("Confirmar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun MenuBoton(texto: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F3F3)),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Text(
                text = texto,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 20.sp
            )
        }
    }

    @Composable
    fun PantallaPartida(onVolverAlMenu: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD6E8CD)), //Fondo verde claro
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
                        fontWeight = FontWeight.Bold
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
                    fontSize = 22.sp
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
        }
    }

    @Composable
    fun PantallaHistorial(onVolverAlMenu: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9F9F9)), //Fondo gris claro
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.height(40.dp))
                Text(
                    text = "Historial de partidas",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF222222)
                )
                Spacer(Modifier.height(40.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .background(Color.White, shape = RoundedCornerShape(36.dp))
                        .padding(vertical = 24.dp),
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HistorialItem("02/10/2025", "Ganó", "+50") //CAMBIAR!!! SON VARIABLES
                        HistorialItem("02/10/2025", "Perdió", "-30")
                        HistorialItem("02/10/2025", "Ganó", "+50")
                        HistorialItem("02/10/2025", "Perdió", "-30")
                    }
                }
            }
            // Botón de volver al menú
            Button(
                onClick = onVolverAlMenu,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 24.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Volver al menú",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }
    }

    @Composable
    fun HistorialItem(fecha: String, resultado: String, monedas: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(20.dp))
                .padding(vertical = 18.dp, horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = fecha,
                fontSize = 20.sp,
                color = Color(0xFF555555),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = resultado,
                fontSize = 20.sp,
                color = Color(0xFF222222),
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = monedas,
                fontSize = 20.sp,
                color = Color(0xFF555555),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
    }

    @Composable
    fun PantallaAjustes(onVolverAlMenu: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFB5E2F8)), // Fondo azul claro
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AjustesSeccion(titulo = "Sonido") {
                    AjusteSwitch("Música de fondo", checked = true)
                    AjusteSwitch("Efectos de sonido", checked = false)
                }
                AjustesSeccion(titulo = "Visualización") {
                    AjusteSwitch("Tema claro/oscuro", checked = true)
                    AjusteSwitch("Animaciones gráficas del juego", checked = false)
                }
                AjustesSeccion(titulo = "Notificaciones") {
                    AjusteSwitch("Notificaciones de resultado o logros", checked = true)
                }

                Text(
                    text = "Monedas / Datos de jugador",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.moneda), // Ajusta el nombre
                        contentDescription = "Moneda",
                        tint = Color(0xFFFFC947),
                        modifier = Modifier.size(26.dp)
                    )
                    Text(text = " Monedas: 100", fontSize = 17.sp, color = Color(0xFF444444))
                }
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = { /* Acción: reiniciar monedas */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Reiniciar monedas", fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Información",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { /* Acción versión de la app */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Versión de la app", color = Color.Black)
                    }
                    Button(
                        onClick = { /* Acción acerca de */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Acerca de", color = Color.Black)
                    }
                }
                Spacer(modifier = Modifier.height(48.dp)) // Para separar del botón fijo de abajo
            }
            // Botón de volver al menú
            Button(
                onClick = onVolverAlMenu,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.6f)
                    .padding(bottom = 28.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Volver al menú",
                    color = Color(0xFF3B71B8),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }
    }

    @Composable
    fun AjustesSeccion(titulo: String, content: @Composable ColumnScope.() -> Unit) {
        Text(
            text = titulo,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }

    @Composable
    fun AjusteSwitch(label: String, checked: Boolean) {
        var isChecked by remember { mutableStateOf(checked) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 16.sp, color = Color(0xFF3A3A3A))
            Switch(checked = isChecked, onCheckedChange = { isChecked = it })
        }
    }
}
