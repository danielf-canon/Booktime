package com.example.booktime.tadeo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingTimeScreen(onNext: () -> Unit) {
    var selectedTime by remember { mutableStateOf("") }
    
    val readingTimes = listOf(
        "15 minutos",
        "30 minutos",
        "1 hora",
        "2 horas",
        "Más de 3 horas"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5A6E))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))
            
            Text(
                text = "¿Cuánto tiempo quieres leer?",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Time Selection List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                readingTimes.forEach { time ->
                    val isSelected = selectedTime == time
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTime = time },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF54C35D) else Color(0xFFD9D9D9),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = time,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = if (isSelected) Color.White else Color(0xFF4A5A6E),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { if (selectedTime.isNotEmpty()) onNext() },
                enabled = selectedTime.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF54C35D),
                    disabledContainerColor = Color(0xFF657285)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Continuar",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
