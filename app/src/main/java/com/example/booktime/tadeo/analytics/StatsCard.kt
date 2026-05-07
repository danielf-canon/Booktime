package com.example.booktime.tadeo.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatsCard(
    title: String,
    value: String,
    emoji: String
) {

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF4A5A6E))
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )

            Column {

                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

