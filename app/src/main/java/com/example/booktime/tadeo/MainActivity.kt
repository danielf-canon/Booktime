package com.example.booktime.tadeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.FirebaseApp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.navigation.Screen
import com.example.booktime.tadeo.views.*
import kotlinx.coroutines.delay

import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            BooktimeTheme {
                val auth = FirebaseAuth.getInstance()
                
                var currentScreen by remember { mutableStateOf(Screen.Loading.route) }
                
                LaunchedEffect(Unit) {
                    delay(3000) // Simulacion de carga
                    currentScreen = if (auth.currentUser != null) {
                        Screen.Books.route
                    } else {
                        Screen.Main.route
                    }
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4A5A6E))
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (Screen.isForward(targetState) || (initialState == Screen.Loading.route && targetState == Screen.Main.route)) {
                                slideInHorizontally(animationSpec = tween(400)) { it }
                                    .togetherWith(slideOutHorizontally(animationSpec = tween(400)) { -it / 2 })
                            } else {
                                slideInHorizontally(animationSpec = tween(400)) { -it }
                                    .togetherWith(slideOutHorizontally(animationSpec = tween(400)) { it / 2 })
                            }
                        },
                        label = "ScreenTransition"
                    ) { screenRoute ->
                        when (screenRoute) {
                            Screen.Loading.route -> LoadingScreen()
                            Screen.Main.route -> MainMenu(
                                onLoginClick = { currentScreen = Screen.Login.route },
                                onRegisterClick = { currentScreen = Screen.Register.route }
                            )
                            Screen.Register.route -> RegisterScreen(
                                onBackClick = { currentScreen = Screen.Main.route },
                                onRegisterSuccess = { currentScreen = Screen.OnboardingTime.route }
                            )
                            Screen.OnboardingTime.route -> OnboardingTimeScreen(
                                onNext = { currentScreen = Screen.OnboardingGenre.route }
                            )
                            Screen.OnboardingGenre.route -> OnboardingGenreScreen(
                                onFinish = { currentScreen = Screen.ComingSoon.route }
                            )
                            Screen.Login.route -> LoginScreen(
                                onBackClick = { currentScreen = Screen.Main.route },
                                onForgotPasswordClick = { currentScreen = Screen.ForgotPassword.route },
                                onLoginSuccess = { currentScreen = Screen.ComingSoon.route }
                            )
                            Screen.ForgotPassword.route -> ForgotPasswordScreen(
                                onBackClick = { currentScreen = Screen.Login.route }
                            )
                            Screen.CreateNewPassword.route -> CreateNewPasswordScreen(
                                onBackClick = { currentScreen = Screen.ForgotPassword.route },
                                onPasswordCreated = { currentScreen = Screen.Login.route }
                            )
                            Screen.ComingSoon.route -> ComingSoonScreen(
                                onBackClick = { currentScreen = Screen.Main.route }
                            )
                            Screen.Books.route -> BooksView(
                                onFavoritesClick = { currentScreen = Screen.ComingSoon.route },
                                onSettingsClick = { currentScreen = Screen.Settings.route },
                                onAddClick = { currentScreen = Screen.ComingSoon.route }
                            )
                            Screen.Settings.route -> SettingsView(
                                onBackClick = { currentScreen = Screen.Books.route },
                                onEditProfileClick = { currentScreen = Screen.EditProfile.route },
                                onChangePasswordClick = { currentScreen = Screen.ChangePassword.route },
                                onReadingSettingsClick = { currentScreen = Screen.ReadingSettings.route },
                                onLogoutClick = { currentScreen = Screen.Main.route }
                            )
                            Screen.EditProfile.route -> EditProfileView(
                                onBackClick = { currentScreen = Screen.Settings.route }
                            )
                            Screen.ChangePassword.route -> ChangePasswordView(
                                onBackClick = { currentScreen = Screen.Settings.route }
                            )
                            Screen.ReadingSettings.route -> ReadingSettingsView(
                                onBackClick = { currentScreen = Screen.Settings.route }
                            )
                        }
                    }
                }
            }
        }
    }
}