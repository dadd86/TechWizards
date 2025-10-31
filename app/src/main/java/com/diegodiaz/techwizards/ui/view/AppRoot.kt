// ui/view/AppRoot.kt
package com.diegodiaz.techwizards.ui.view

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.rememberNavController
import com.diegodiaz.techwizards.ui.theme.TechWizardsTheme

@Composable
fun AppRoot() {
    var isDarkTheme by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    TechWizardsTheme(darkTheme = isDarkTheme) {
        Scaffold { innerPadding ->
            NavGraph(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onToggleTheme = { isDarkTheme = it },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
