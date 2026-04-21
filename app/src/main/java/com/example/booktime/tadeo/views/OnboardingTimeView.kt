package com.example.booktime.tadeo.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.ui.theme.ButtonGreen

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

    ScreenWrapper {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = stringResource(id = R.string.onboarding_time_question),
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
                    color = if (isSelected) ButtonGreen else Color(0xFFD9D9D9),
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
        
        BooktimeButton(
            text = stringResource(id = R.string.continue_text),
            enabled = selectedTime.isNotEmpty(),
            onClick = { if (selectedTime.isNotEmpty()) onNext() }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
