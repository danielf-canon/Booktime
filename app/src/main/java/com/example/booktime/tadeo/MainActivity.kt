package com.example.booktime.tadeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.views.LoadingScreen
import com.example.booktime.tadeo.views.MainMenu
import com.example.booktime.tadeo.views.RegisterScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BooktimeTheme {
                var currentScreen by remember { mutableStateOf("loading") }
                
                LaunchedEffect(Unit) {
                    delay(3000) // Simulating loading for 3 seconds
                    currentScreen = "main"
                }
                
                when (currentScreen) {
                    "loading" -> LoadingScreen()
                    "main" -> MainMenu(
                        onRegisterClick = { currentScreen = "register" }
                    )
                    "register" -> RegisterScreen(
                        onBackClick = { currentScreen = "main" }
                    )
                }
            }
        }
    }
}