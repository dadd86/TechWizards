package com.diegodiaz.techwizards.ui.view

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.diegodiaz.techwizards.ui.responsive.Responsive
import com.diegodiaz.techwizards.ui.responsive.UiDims

@Composable
fun AppRoot(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    Responsive { dims: UiDims ->
        Scaffold { innerPadding ->
            NavGraph(
                navController = navController,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                dims = dims, // ← se lo pasamos a todo el grafo
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}