package com.example.booktime.tadeo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.booktime.tadeo.ui.theme.*

@Composable
fun BookAddedSuccessView(
    title: String,
    author: String,
    imageUrl: String?,
    dateAdded: String,
    onStartClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val figmaGradient = Brush.linearGradient(
        0.0f to Color(0xFF56779E), // Azul claro (0%)
        0.79f to Color(0xFF1E2A38) // Azul oscuro (79%)
    )

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets.statusBars
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(figmaGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Botón atrás
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp) // Espacio respecto a la barra de estado
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.align(Alignment.CenterStart) // Alinea al inicio del Box
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Flecha estándar de Android
                            contentDescription = "Atrás",
                            tint = AppWhite,
                            modifier = Modifier.size(28.dp) // Tamaño más visible
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Portada
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .width(200.dp)
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = title,
                    color = AppWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = author,
                    color = AppWhite.copy(alpha = 0.8f),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Progreso: 0%",
                    color = AppWhite.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Abrir un libro es abrir un universo.\n¿Te atreves a explorarlo?",
                    color = AppWhite,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Iniciar
                Button(
                    onClick = onStartClick,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Iniciar", color = AppWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Tu libro ha sido añadido!",
                    color = ButtonGreen,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 1.dp,
                    color = AppWhite.copy(alpha = 0.3f)
                )

                Text(
                    text = "Agregado el $dateAdded",
                    color = AppWhite.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
