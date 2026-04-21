package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.utils.ValidationUtils

@Composable
fun CreateNewPasswordScreen(onBackClick: () -> Unit, onPasswordCreated: () -> Unit) {
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val errorEmptyFields = stringResource(id = R.string.error_empty_fields)
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
            text = stringResource(id = R.string.change_password_title),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        BooktimeTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            placeholder = stringResource(id = R.string.new_password_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        BooktimeTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            placeholder = stringResource(id = R.string.confirm_new_password_placeholder),
            modifier = Modifier.padding(bottom = 32.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        BooktimeButton(
            text = stringResource(id = R.string.update_password_button),
            onClick = { 
                if (newPassword.isBlank() || confirmNewPassword.isBlank()) {
                    errorMessage = errorEmptyFields
                    showErrorDialog = true
                } else if (!ValidationUtils.isValidPassword(newPassword)) {
                    errorMessage = errorPasswordRules
                    showErrorDialog = true
                } else if (!ValidationUtils.passwordsMatch(newPassword, confirmNewPassword)) {
                    errorMessage = errorPasswordsDontMatch
                    showErrorDialog = true
                } else {
                    onPasswordCreated()
                }
            }
        )
    }
}