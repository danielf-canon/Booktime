package com.example.booktime.tadeo.utils

object ValidationUtils {
    // Regex más flexible para evitar problemas con espacios o formatos estrictos
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun isValidEmail(email: String): Boolean {
        return email.trim().matches(emailRegex)
    }

    fun isValidPassword(password: String): Boolean {
        return password.length in 8..16 && password.any { it.isDigit() }
    }

    fun passwordsMatch(p1: String, p2: String): Boolean {
        return p1 == p2
    }
}
