package com.diegodiaz.techwizards.ui.view

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun AppRoot(isDarkTheme: Boolean, onToggleTheme: (Boolean) -> Unit) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavGraph(
            navController = navController,
            isDarkTheme = isDarkTheme,
            onToggleTheme = onToggleTheme,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
