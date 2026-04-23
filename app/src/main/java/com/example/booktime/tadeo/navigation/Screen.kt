package com.example.booktime.tadeo.navigation

sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Main : Screen("main")
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object OnboardingTime : Screen("onboarding_time")
    object OnboardingGenre : Screen("onboarding_genre")
    object ComingSoon : Screen("coming_soon")
    object Books : Screen("books")
    object Settings : Screen("settings")
    object EditProfile : Screen("edit_profile")
    object ReadingSettings : Screen("reading_settings")
    object NewBook : Screen("add_books")
    object NewEbook : Screen("add_ebooks")

    object SearchBook : Screen("search_book")

    object BookAddedSuccess : Screen("book_added_success")



    companion object {
        fun isForward(route: String): Boolean {
            return route in listOf(
                Register.route,
                Login.route,
                ForgotPassword.route,
                OnboardingTime.route,
                OnboardingGenre.route,
                ComingSoon.route,
                Books.route,
                Settings.route,
                EditProfile.route,
                ReadingSettings.route,
                NewBook.route,
                NewEbook.route
            )
        }
    }
}
