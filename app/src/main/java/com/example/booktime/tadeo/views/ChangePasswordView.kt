package com.example.booktime.tadeo.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.ui.theme.ButtonGreen
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.components.SettingsSectionTitle
import com.example.booktime.tadeo.utils.ValidationUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordView(onBackClick: () -> Unit = {}) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPasswords by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val errorEmptyFields = stringResource(id = R.string.error_empty_fields)
    val errorPasswordRules = stringResource(id = R.string.error_password_rules)
    val errorPasswordsDontMatch = stringResource(id = R.string.error_passwords_dont_match)
    val errorSamePassword = stringResource(id = R.string.error_same_password)

    if (showErrorDialog) {
        AnimatedDialog(
            title = stringResource(id = R.string.security_error_title),
            text = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    ScreenWrapper(onBackClick = onBackClick) {
        SettingsSectionTitle(stringResource(id = R.string.change_password_title))
        
        Text(
            text = stringResource(id = R.string.security_tip),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        BooktimeTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            placeholder = stringResource(id = R.string.current_password_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation()
        )

        BooktimeTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            placeholder = stringResource(id = R.string.new_password_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation()
        )

        BooktimeTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = stringResource(id = R.string.confirm_new_password_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
            visualTransformation = if (showPasswords) VisualTransformation.None else PasswordVisualTransformation()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPasswords = !showPasswords }
                .padding(vertical = 8.dp)
        ) {
            Checkbox(
                checked = showPasswords,
                onCheckedChange = { showPasswords = it },
                colors = CheckboxDefaults.colors(checkedColor = ButtonGreen, uncheckedColor = Color.White.copy(alpha = 0.5f))
            )
            Text(stringResource(id = R.string.show_passwords), color = Color.White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        BooktimeButton(
            text = stringResource(id = R.string.update_password_button),
            onClick = { 
                if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = errorEmptyFields
                    showErrorDialog = true
                } else if (!ValidationUtils.isValidPassword(newPassword)) {
                    errorMessage = errorPasswordRules
                    showErrorDialog = true
                } else if (!ValidationUtils.passwordsMatch(newPassword, confirmPassword)) {
                    errorMessage = errorPasswordsDontMatch
                    showErrorDialog = true
                } else if (currentPassword == newPassword) {
                    errorMessage = errorSamePassword
                    showErrorDialog = true
                } else {
                    /* Actualizar contraseña logic here */
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangePasswordViewPreview() {
    BooktimeTheme(darkTheme = true, dynamicColor = false) {
        ChangePasswordView()
    }
}
