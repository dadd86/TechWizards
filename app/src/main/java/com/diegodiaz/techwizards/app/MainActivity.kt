package com.diegodiaz.techwizards.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.diegodiaz.techwizards.ui.theme.TechWizardsTheme
import com.diegodiaz.techwizards.ui.view.AppRoot
import com.diegodiaz.techwizards.util.logging.DecentralizedLogger

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { !LocaleStartupState.isReady }
        DecentralizedLogger.d(
            "MainActivity",
            "LocaleStartupState.isReady=${LocaleStartupState.isReady}"
        )
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by rememberSaveable { mutableStateOf(false) }

            TechWizardsTheme(darkTheme = isDarkTheme) {
                AppRoot(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { isDarkTheme = it }
                )
            }
        }
    }
}

