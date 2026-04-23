package com.example.booktime.tadeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavType

import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.navigation.Screen
import com.example.booktime.tadeo.views.*

import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            BooktimeTheme {

                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()
                val user = FirebaseAuth.getInstance().currentUser
                val userId = user?.uid

                NavHost(
                    navController = navController,
                    startDestination = Screen.Loading.route,
                    modifier = Modifier
                ) {

                     composable(
                        route = Screen.Loading.route,
                        enterTransition = { slideInRight() },
                        exitTransition = { slideOutLeft() }
                    ) {
                        LaunchedEffect(Unit) {
                            delay(3000)

                            if (auth.currentUser != null) {
                                navController.navigate(Screen.Books.route) {
                                    popUpTo(Screen.Loading.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Loading.route) { inclusive = true }
                                }
                            }
                        }

                        LoadingScreen()
                    }

                    composable(
                        route = Screen.Main.route,
                        enterTransition = { slideInRight() },
                        exitTransition = { slideOutLeft() },
                        popEnterTransition = { slideInLeft() },
                        popExitTransition = { slideOutRight() }
                    ) {
                        MainMenu(
                            onLoginClick = { navController.navigate(Screen.Login.route) },
                            onRegisterClick = { navController.navigate(Screen.Register.route) }
                        )
                    }

                    composable(
                        route = Screen.Login.route,
                        enterTransition = { slideInRight() },
                        popExitTransition = { slideOutRight() }
                    ) {
                        LoginScreen(
                            onBackClick = { navController.popBackStack() },
                            onForgotPasswordClick = {
                                navController.navigate(Screen.ForgotPassword.route)
                            },
                            onLoginSuccess = {
                                navController.navigate(Screen.Books.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        route = Screen.Register.route,
                        enterTransition = { slideInRight() },
                        popExitTransition = { slideOutRight() }
                    ) {
                        RegisterScreen(
                            onBackClick = { navController.popBackStack() },
                            onRegisterSuccess = {
                                navController.navigate(Screen.OnboardingTime.route)
                            }
                        )
                    }

                    composable(
                        route = Screen.OnboardingTime.route,
                        enterTransition = { slideInRight() },
                        popExitTransition = { slideOutRight() }
                    ) {
                        OnboardingTimeScreen(
                            onNext = {
                                navController.navigate(Screen.OnboardingGenre.route)
                            }
                        )
                    }

                    composable(
                        route = Screen.OnboardingGenre.route,
                        enterTransition = { slideInRight() },
                        popExitTransition = { slideOutRight() }
                    ) {
                        OnboardingGenreScreen(
                            onFinish = {
                                navController.navigate(Screen.ComingSoon.route)
                            }
                        )
                    }

                    composable(
                        route = Screen.ForgotPassword.route,
                        popExitTransition = { slideOutRight() }
                    ) {
                        ForgotPasswordScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.ComingSoon.route
                    ) {
                        ComingSoonScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.Books.route
                    ) {
                        BooksView(
                            onFavoritesClick = {
                                navController.navigate(Screen.ComingSoon.route)
                            },
                            onSettingsClick = {
                                navController.navigate(Screen.Settings.route)
                            },
                            onAddClick = {
                                navController.navigate(Screen.NewBook.route)
                            }
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsView(
                            onBackClick = { navController.popBackStack() },
                            onEditProfileClick = {
                                navController.navigate(Screen.EditProfile.route)
                            },
                            onReadingSettingsClick = {
                                navController.navigate(Screen.ReadingSettings.route)
                            },
                            onLogoutClick = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.EditProfile.route) {
                        EditProfileView(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ReadingSettings.route) {
                        ReadingSettingsView(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.NewBook.route) {
                        NewBookScreen(
                            navController = navController,
                            onBackClick = { navController.popBackStack() },
                            onSearchClick = { navController.navigate(Screen.SearchBook.route) },
                            onImportEbookClick = { navController.navigate(Screen.NewEbook.route) },
                            onImportAudiobookClick = { }
                        )
                    }

                    composable(Screen.NewEbook.route) {
                        AddBookScreen(
                            userId = userId ?: "",
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.SearchBook.route) {
                        SearchBookView(
                            navController = navController
                        )
                    }

                    composable(
                        route = "${Screen.BookAddedSuccess.route}/{title}/{author}/{imageUrl}?date={date}",
                        arguments = listOf(
                            navArgument("title") { type = NavType.StringType },
                            navArgument("author") { type = NavType.StringType },
                            navArgument("imageUrl") { type = NavType.StringType },
                            navArgument("date") {
                                type = NavType.StringType
                                defaultValue = "Desconocida"
                            }
                        )
                    ) { backStackEntry ->
                        val title = backStackEntry.arguments?.getString("title") ?: ""
                        val author = backStackEntry.arguments?.getString("author") ?: ""
                        val date = backStackEntry.arguments?.getString("date") ?: ""

                        val encodedUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                        val imageUrl = java.net.URLDecoder.decode(encodedUrl, "UTF-8")

                        BookAddedSuccessView(
                            title = title,
                            author = author,
                            imageUrl = imageUrl,
                            dateAdded = date,
                            onStartClick = {
                                navController.navigate(Screen.Books.route) {
                                    popUpTo(Screen.Books.route) { inclusive = true }
                                }
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

fun slideInRight(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(400),
        initialOffsetX = { it }
    )

fun slideOutLeft(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(400),
        targetOffsetX = { -it / 2 }
    )

fun slideInLeft(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(400),
        initialOffsetX = { -it }
    )

fun slideOutRight(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(400),
        targetOffsetX = { it / 2 }
    )
