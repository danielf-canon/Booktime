package com.example.booktime.tadeo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.booktime.tadeo.data.chat.GeminiRepository
import kotlinx.coroutines.delay

@Composable
fun ChatBottomSheet(
    bookTitle: String,
    onClose: () -> Unit
) {
    var messages by remember { mutableStateOf(listOf<String>()) }
    var text by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var lastRequestTime by remember { mutableStateOf(0L) }

    val prompts = listOf("Resumen", "Personajes", "Tema principal")

    val scope = rememberCoroutineScope()
    val gemini = remember { GeminiRepository() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(350.dp)
                .background(Color.White)
                .padding(12.dp)
        ) {

            TextButton(onClick = onClose) {
                Text("Cerrar")
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(messages) {
                    Text(it, color = Color.Black)
                }
            }


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prompts.forEach { prompt ->
                    Button(
                        enabled = !isLoading,
                        onClick = {
                            scope.launch {

                                val currentTime = System.currentTimeMillis()

                                if (currentTime - lastRequestTime < 5000) {
                                    messages = messages + "Sistema: Espera unos segundos antes de volver a preguntar"
                                    return@launch
                                }

                                lastRequestTime = currentTime

                                if (isLoading) return@launch
                                isLoading = true

                                delay(3000)

                                val fullPrompt =
                                    "Explícame el libro '$bookTitle'. Pregunta: $prompt"

                                val response = gemini.ask(fullPrompt)

                                messages = messages + "Tú: $prompt" + "IA: $response"

                                isLoading = false
                            }
                        }
                    ) {
                        Text(prompt)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Row {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                )

                Button(
                    enabled = !isLoading,
                    onClick = {
                        scope.launch {

                            val currentTime = System.currentTimeMillis()

                            if (currentTime - lastRequestTime < 8000) {
                                messages = messages + "Sistema: Espera unos segundos antes de volver a preguntar"
                                return@launch
                            }

                            lastRequestTime = currentTime

                            if (isLoading) return@launch
                            isLoading = true

                            delay(3000)

                            val fullPrompt =
                                "Sobre el libro '$bookTitle': $text"

                            val response = gemini.ask(fullPrompt)

                            messages = messages + "Tú: $text" + "IA: $response"
                            text = ""

                            isLoading = false
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}