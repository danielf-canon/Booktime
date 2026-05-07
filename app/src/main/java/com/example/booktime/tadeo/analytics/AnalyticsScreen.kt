package com.example.booktime.tadeo.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.booktime.tadeo.components.BooktimeBottomNav

@Composable
fun AnalyticsScreen(
    onBottomNavItemSelected: (Int) -> Unit
) {

    Scaffold(

        bottomBar = {
            BooktimeBottomNav(
                selectedItem = 1,
                onItemSelected = onBottomNavItemSelected
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2E3B4E))
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Analítica 📊",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                StatsCard(
                    title = "Libros leídos",
                    value = "12",
                    emoji = "📚"
                )

                StatsCard(
                    title = "Favoritos",
                    value = "3",
                    emoji = "❤️"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                StatsCard(
                    title = "Horas leyendo",
                    value = "15h",
                    emoji = "⏱"
                )

                StatsCard(
                    title = "Top género",
                    value = "Fantasy",
                    emoji = "🔥"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Progreso semanal 📈",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            ReadingChart()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    AnalyticsScreen(
        onBottomNavItemSelected = {}
    )
}