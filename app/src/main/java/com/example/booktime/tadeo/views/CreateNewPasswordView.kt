package com.example.booktime.tadeo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CreateNewPasswordScreen(onBackClick: () -> Unit, onPasswordCreated: () -> Unit) {
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showErrorDialog) {
        AnimatedDialog(
            title = "Error",
            text = errorMessage,
            onDismiss = { showErrorDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5A6E))
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nueva Contraseña",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = { Text("Nueva contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    focusedIndicatorColor = Color(0xFF54C35D),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                placeholder = { Text("Confirmar nueva contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFD9D9D9),
                    unfocusedContainerColor = Color(0xFFD9D9D9),
                    focusedIndicatorColor = Color(0xFF54C35D),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Button(
                onClick = { 
                    if (newPassword.isBlank() || confirmNewPassword.isBlank()) {
                        errorMessage = "Por favor, completa ambos campos."
                        showErrorDialog = true
                    } else if (newPassword.length < 8 || newPassword.length > 16) {
                        errorMessage = "La contraseña debe tener entre 8 y 16 caracteres."
                        showErrorDialog = true
                    } else if (newPassword != confirmNewPassword) {
                        errorMessage = "Las contraseñas no coinciden."
                        showErrorDialog = true
                    } else {
                        onPasswordCreated()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF54C35D),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Cambiar Contraseña",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}