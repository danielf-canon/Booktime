package com.example.booktime.tadeo.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.data.chat.GeminiRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBottomSheet(
    bookTitle: String,
    onClose: () -> Unit
) {
    // Estado y lógica original
    var messages by remember { mutableStateOf(listOf<Pair<String, String>>()) } // Pair para diferenciar Usuario/IA
    var text by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var lastRequestTime by remember { mutableStateOf(0L) }

    val scope = rememberCoroutineScope()
    val gemini = remember { GeminiRepository() }

    // Colores de la imagen
    val cardBackground = Color(0xFF5A667A)
    val textColor = Color.White
    val inputBackground = Color(0xFFFFFFFF).copy(alpha = 0.2f)

    val prompts = listOf(
        "¿Cuándo se publicó el libro?",
        "¿Puedes hacerme un resumen del libro?",
        "¿Cuál es el tema principal del libro?",
        "¿Cuántas páginas tiene el libro?"
    )

    val onSendMessage: (String) -> Unit = { query ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRequestTime >= 5000) {
            if (!isLoading && query.isNotBlank()) {
                scope.launch {
                    lastRequestTime = currentTime
                    isLoading = true
                    val response = gemini.ask("Sobre el libro '$bookTitle': $query")
                    messages = messages + (query to response)
                    text = ""
                    isLoading = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 24.dp, vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(cardBackground)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.timo),
                contentDescription = "Chat con IA",
                modifier = Modifier.size(52.dp).padding(bottom = 12.dp)
            )
            Text(
                text = "Pregúntale cualquier cosa a tu asistente de IA Timo",
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            if (messages.isEmpty()) {
                Text(
                    text = "Preguntas frecuentes",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                prompts.forEach { prompt ->
                    TextButton(
                        onClick = { onSendMessage(prompt) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = prompt,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                // Si ya hay chat, mostramos los mensajes
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    items(messages) { message ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("Tú: ${message.first}", color = textColor.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("IA: ${message.second}", color = textColor, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Pregúntale a timo", color = textColor.copy(alpha = 0.6f)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(inputBackground),
                shape = RoundedCornerShape(28.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = textColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = textColor, strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = { onSendMessage(text) }) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = textColor.copy(alpha = 0.7f))
                        }
                    }
                }
            )

            TextButton(onClick = onClose, modifier = Modifier.padding(top = 8.dp)) {
                Text("Cerrar", color = textColor.copy(alpha = 0.5f))
            }
        }
    }
}
