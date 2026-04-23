package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.utils.ValidationUtils

@Composable
fun RegisterScreen(onBackClick: () -> Unit, onRegisterSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val errorEmptyFields = stringResource(id = R.string.error_empty_fields)
    val errorInvalidEmail = stringResource(id = R.string.error_invalid_email)
    val errorPasswordRules = stringResource(id = R.string.error_password_rules)
    val errorPasswordsDontMatch = stringResource(id = R.string.error_passwords_dont_match)

    if (showErrorDialog) {
        AnimatedDialog(
            title = stringResource(id = R.string.error_title),
            text = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    ScreenWrapper(onBackClick = onBackClick) {
        Text(
            text = stringResource(id = R.string.register_title),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        BooktimeTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = stringResource(id = R.string.full_name_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        BooktimeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(id = R.string.email_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        BooktimeTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(id = R.string.password_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation(),
        )

        BooktimeTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = stringResource(id = R.string.confirm_password_placeholder),
            modifier = Modifier.padding(bottom = 32.dp),
            visualTransformation = PasswordVisualTransformation(),
        )

        BooktimeButton(
            text = stringResource(id = R.string.register_button),
            isLoading = isLoading,
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = errorEmptyFields
                    showErrorDialog = true
                } else if (!ValidationUtils.isValidEmail(email)) {
                    errorMessage = errorInvalidEmail
                    showErrorDialog = true
                } else if (!ValidationUtils.isValidPassword(password)) {
                    errorMessage = errorPasswordRules
                    showErrorDialog = true
                } else if (!ValidationUtils.passwordsMatch(password, confirmPassword)) {
                    errorMessage = errorPasswordsDontMatch
                    showErrorDialog = true
                } else {
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()
                                user?.updateProfile(profileUpdates)
                                onRegisterSuccess()
                            } else {
                                val exception = task.exception
                                errorMessage = when (exception) {
                                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Este correo ya está registrado."
                                    else -> "Error al registrar: ${exception?.message}"
                                }
                                showErrorDialog = true
                            }
                        }
                }
            }
        )
    }
}
