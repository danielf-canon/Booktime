package com.example.booktime.tadeo.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AnimatedDialog(title: String, text: String, onDismiss: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    
    // Trigger animation when entering
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Dialog(onDismissRequest = { 
        isVisible = false
        onDismiss() 
    }) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(animationSpec = tween(300)),
            exit = scaleOut(animationSpec = tween(300))
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Entendido",
                            color = Color(0xFF54C35D)
                        )
                    }
                }
            }
        }
    }
}