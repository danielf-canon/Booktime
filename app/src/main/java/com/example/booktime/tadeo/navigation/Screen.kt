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
    object ChangePassword : Screen("change_password")
    object NewBook : Screen("add_books")
    object NewEbook : Screen("add_ebooks")

    object SearchBook : Screen("search_book")

    object BookAddedSuccess : Screen("book_added_success")

    object FavoriteBooks : Screen("favorite_books")

    object Analytics : Screen("analytics")
    object PdfReader : Screen("pdf_reader/{bookId}") {
        fun createRoute(bookId: String) = "pdf_reader/$bookId"
    }

    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }




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
                ChangePassword.route,
                NewBook.route,
                NewEbook.route
            )
        }
    }
}
