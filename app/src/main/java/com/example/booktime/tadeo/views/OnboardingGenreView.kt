package com.example.booktime.tadeo.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.ui.theme.ButtonGreen

@Composable
fun OnboardingGenreScreen(onFinish: () -> Unit) {
    val selectedGenres = remember { mutableStateListOf<String>() }
    
    val genres = listOf(
        "Fantasía", "Ciencia Ficción", "Misterio", 
        "Romance", "Terror", "Aventura", 
        "Historia", "Biografía", "Autoayuda",
        "Poesía", "Cómics", "Clásicos"
    )

    ScreenWrapper {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = stringResource(id = R.string.onboarding_genre_question),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Genre Selection List (Scrollable)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(genres) { genre ->
                val isSelected = selectedGenres.contains(genre)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isSelected) {
                                selectedGenres.remove(genre)
                            } else {
                                selectedGenres.add(genre)
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) ButtonGreen else Color(0xFFD9D9D9),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = genre,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = if (isSelected) Color.White else Color(0xFF4A5A6E)
                        )
                        if (isSelected) {
                            Text(
                                "✓",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        BooktimeButton(
            text = stringResource(id = R.string.finish),
            enabled = selectedGenres.isNotEmpty(),
            onClick = { if (selectedGenres.isNotEmpty()) onFinish() }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
