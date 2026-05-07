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
import com.example.booktime.tadeo.data.chat.ChatRepository
import com.example.booktime.tadeo.data.model.ChatMessage
import com.google.firebase.auth.FirebaseAuth
@Composable
fun ChatBottomSheet(
    bookTitle: String,
    onClose: () -> Unit
) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var text by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var lastRequestTime by remember { mutableStateOf(0L) }

    val prompts = listOf("Resumen", "Personajes", "Tema principal")

    val scope = rememberCoroutineScope()
    val gemini = remember { GeminiRepository() }
    val chatRepository = remember { ChatRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
    LaunchedEffect(Unit) {

        chatRepository.loadMessages(userId, bookTitle) {
            messages = it
        }
    }

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
                items(messages) { message ->

                    val prefix =
                        if (message.sender == "user") "Tú: "
                        else "IA: "

                    Text(
                        text = prefix + message.text,
                        color = Color.Black
                    )
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
                                    messages = messages + ChatMessage(
                                        "Espera unos segundos antes de volver a preguntar",
                                        "ai"
                                    )
                                    return@launch
                                }

                                lastRequestTime = currentTime

                                if (isLoading) return@launch
                                isLoading = true

                                delay(3000)

                                val fullPrompt = """
                                    Eres una inteligencia artificial especializada únicamente en libros.

                                        Libro actual: "$bookTitle"

                                    INSTRUCCIONES IMPORTANTES:
                                    - Solo puedes responder preguntas relacionadas con este libro.
                                    - Si el usuario pregunta algo fuera del libro responde EXACTAMENTE:
                                "No tengo contexto para responder eso. Solo puedo ayudarte con preguntas relacionadas con el libro."

                                    - No respondas preguntas generales.
                                    - No respondas temas de programación, deportes, chistes o actualidad.
                                    - Responde máximo en 5 líneas.
                                    - Sé claro y breve.
                                        - No inventes información.

                                     Pregunta del usuario:
                                        $prompt
                                        """.trimIndent()

                                chatRepository.saveMessage(
                                    userId,
                                    bookTitle,
                                    ChatMessage(prompt, "user")
                                )

                                val response = gemini.ask(fullPrompt)

                                messages = messages +
                                        ChatMessage(prompt, "user") +
                                        ChatMessage(response, "ai")

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
                                messages = messages + ChatMessage(
                                    "Espera unos segundos antes de volver a preguntar",
                                    "ai"
                                )
                                return@launch
                            }

                            lastRequestTime = currentTime

                            if (isLoading) return@launch
                            isLoading = true

                            delay(3000)

                            val fullPrompt = """
                                Eres una inteligencia artificial especializada únicamente en libros.

                                Libro actual: "$bookTitle"

                                INSTRUCCIONES IMPORTANTES:
                                - Solo puedes responder preguntas relacionadas con este libro.
                                - Si el usuario pregunta algo fuera del libro responde EXACTAMENTE:
                                "No tengo contexto para responder eso. Solo puedo ayudarte con preguntas relacionadas con el libro."

                                - No respondas preguntas generales.
                                - No respondas temas de programación, deportes, chistes o actualidad.
                                - Responde máximo en 5 líneas.
                                - Sé claro y breve.
                                - No inventes información.

                                Pregunta del usuario:
                                $text
                                """.trimIndent()
                            val response = gemini.ask(fullPrompt)

                            chatRepository.saveMessage(
                                userId,
                                bookTitle,
                                ChatMessage(response, "ai")
                            )

                            messages = messages +
                                    ChatMessage(text, "user") +
                                    ChatMessage(response, "ai")

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