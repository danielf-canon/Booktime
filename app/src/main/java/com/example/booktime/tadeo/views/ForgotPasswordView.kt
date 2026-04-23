package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.utils.ValidationUtils

@Composable
fun ForgotPasswordScreen(onBackClick: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val errorInvalidEmail = stringResource(id = R.string.error_invalid_email)
    val recoverySuccessMsg = stringResource(id = R.string.recovery_success_msg)
    val errorEmailEmpty = stringResource(id = R.string.error_email_empty)
    val sendEmailButtonText = stringResource(id = R.string.send_email_button)

    if (showErrorDialog) {
        AnimatedDialog(
            title = stringResource(id = R.string.error_title),
            text = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    ScreenWrapper(onBackClick = onBackClick) {
        Text(
            text = stringResource(id = R.string.recovery_title),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        BooktimeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(id = R.string.email_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        BooktimeButton(
            text = sendEmailButtonText,
            isLoading = isLoading,
            onClick = {
                if (email.isBlank()) {
                    errorMessage = errorEmailEmpty
                    showErrorDialog = true
                } else if (!ValidationUtils.isValidEmail(email)) {
                    errorMessage = errorInvalidEmail
                    showErrorDialog = true
                } else {
                    isLoading = true
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                successMessage = recoverySuccessMsg
                            } else {
                                errorMessage = "Error al enviar correo: ${task.exception?.message}"
                                showErrorDialog = true
                            }
                        }
                }
            }
        )

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = Color.Green,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
