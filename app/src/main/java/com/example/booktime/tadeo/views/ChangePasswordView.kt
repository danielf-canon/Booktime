package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.viewmodels.ProfileViewModel

@Composable
fun ChangePasswordView(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage

    if (errorMessage != null || successMessage != null) {
        AnimatedDialog(
            title = if (errorMessage != null) stringResource(id = R.string.error_title) else "Éxito",
            text = errorMessage ?: successMessage!!,
            onDismiss = {
                val wasSuccess = successMessage != null
                viewModel.clearMessages()
                if (wasSuccess) onBackClick()
            }
        )
    }

    ScreenWrapper(onBackClick = onBackClick) {
        Text(
            text = stringResource(id = R.string.change_password_title),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.security_tip),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        BooktimeTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            placeholder = stringResource(id = R.string.current_password_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation()
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
            isLoading = isLoading,
            onClick = {
                viewModel.changePassword(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    confirmPassword = confirmNewPassword,
                    onSuccess = {
                        // El mensaje de éxito se maneja vía el State en el ViewModel
                    }
                )
            }
        )
    }
}
