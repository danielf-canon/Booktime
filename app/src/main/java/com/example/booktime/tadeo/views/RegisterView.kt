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

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage

    val errorEmptyFields = stringResource(id = R.string.error_empty_fields)
    val errorInvalidEmail = stringResource(id = R.string.error_invalid_email)
    val errorPasswordRules = stringResource(id = R.string.error_password_rules)
    val errorPasswordsDontMatch = stringResource(id = R.string.error_passwords_dont_match)

    if (errorMessage != null || successMessage != null) {
        AnimatedDialog(
            title = if (errorMessage != null) stringResource(id = R.string.error_title) else "Éxito",
            text = errorMessage ?: successMessage!!,
            onDismiss = { 
                val wasSuccess = successMessage != null
                viewModel.clearError()
                if (wasSuccess) onBackClick() // Redirigir al Login/Inicio tras éxito
            }
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
                viewModel.register(
                    name = name,
                    email = email.trim(),
                    password = password,
                    confirmPassword = confirmPassword,
                    errorEmptyFields = errorEmptyFields,
                    errorInvalidEmail = errorInvalidEmail,
                    errorPasswordRules = errorPasswordRules,
                    errorPasswordsDontMatch = errorPasswordsDontMatch,
                    onSuccess = onRegisterSuccess
                )
            }
        )
    }
}
