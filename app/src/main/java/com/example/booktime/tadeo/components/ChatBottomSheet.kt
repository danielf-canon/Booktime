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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import com.example.booktime.tadeo.data.utils.PdfContextHelper
@Composable
fun ChatBottomSheet(
    bookTitle: String,
    bookAuthor: String,
    bookDescription: String,
    pdfContext: String,
    onClose: () -> Unit
)




{
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var text by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var lastRequestTime by remember { mutableStateOf(0L) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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
                .background(Color(0xFF1E1F2B))
                .padding(12.dp)
        ) {

            TextButton(onClick = onClose) {
                Text("Cerrar")
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(messages) { message ->

                    val isUser = message.sender == "user"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            if (isUser) Arrangement.End
                            else Arrangement.Start
                    ) {

                        Column(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 18.dp,
                                        topEnd = 18.dp,
                                        bottomStart =
                                            if (isUser) 18.dp else 4.dp,
                                        bottomEnd =
                                            if (isUser) 4.dp else 18.dp
                                    )
                                )
                                .background(
                                    if (isUser)
                                        PrincipalMenu
                                    else
                                        Color(0xFF2B2D42)
                                )
                                .padding(12.dp)
                        ) {

                            Text(
                                text =
                                    if (isUser) "Tú"
                                    else "Teemo AI",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                            if (isLoading) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.Start
                                ) {

                                    Text(
                                        text = "Teemo AI está escribiendo...",
                                        color = Color.LightGray,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prompts.forEach { prompt ->
                    Button(
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrincipalMenu
                        ),
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
Eres teemo AI, un asistente avanzado especializado en libros, lectura y análisis literario.

CONTEXTO GENERAL:
El usuario está interactuando contigo dentro de una aplicación de lectura llamada Booktime.

LIBRO ACTUAL:
Título: "$bookTitle"

Autor:
"$bookAuthor"

Descripción del libro:
"$bookDescription"
CONTENIDO EXTRAÍDO DEL PDF:
"$pdfContext"
OBJETIVO PRINCIPAL:
Ayudar al usuario a comprender mejor el libro mediante:
- resúmenes
- análisis
- explicación de personajes
- temas principales
- interpretación de capítulos
- aclaración de dudas
- análisis literario
- apoyo académico relacionado con lectura

REGLAS IMPORTANTES:
- Prioriza siempre el contexto del libro actual.
- Si conoces el libro, utiliza tu conocimiento para responder de manera útil.
- Si la información del libro es limitada, responde de forma general sin inventar detalles específicos.
- Nunca inventes hechos, personajes o eventos que no conozcas.
- Si no tienes suficiente contexto, dilo de forma natural y educada.
- Si la pregunta es ambigua, pide aclaración antes de responder.
- Si el usuario hace una pregunta fuera del contexto de libros o lectura, responde amablemente:
"No tengo suficiente contexto para responder eso. Solo puedo ayudarte con temas relacionados con libros y lectura."

TEMAS QUE SÍ PUEDES RESPONDER:
- libros
- autores
- literatura
- análisis narrativo
- géneros literarios
- personajes
- lectura
- escritura
- interpretación de historias
- comprensión lectora

TEMAS QUE DEBES EVITAR:
- programación
- hacking
- deportes
- política
- noticias
- salud médica
- matemáticas complejas
- temas ilegales
- información peligrosa

COMPORTAMIENTO:
- Responde como un asistente profesional y amigable.
- Usa un tono natural y claro.
- Sé útil y directo.
- Evita respuestas demasiado largas.
- Mantén coherencia con la conversación.
- Si el usuario se equivoca, corrígelo de forma amable.
- Si no sabes algo, admítelo honestamente.
- Evita repetir frases innecesarias.

FORMATO DE RESPUESTA:
- Máximo 8 líneas.
- Usa párrafos cortos.
- Explica de manera sencilla y entendible.
- Prioriza claridad sobre complejidad.

MANEJO DE ERRORES:
- Si la pregunta no tiene suficiente información, pide más contexto.
- Si el libro no es reconocido, intenta ayudar usando información general relacionada.
- Si no puedes responder con seguridad, indícalo claramente.
- Nunca generes información falsa solo para completar una respuesta.

PREGUNTA DEL USUARIO:
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
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp)),
                    enabled = !isLoading,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF2B2D42),
                        unfocusedContainerColor = Color(0xFF2B2D42),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(
                            "Pregunta sobre el libro...",
                            color = Color.LightGray
                        )
                    }
                )

                Button(
                    enabled = !isLoading,
                    modifier = Modifier.padding(start = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrincipalMenu
                    ),
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
                            val relevantContext =
                                PdfContextHelper.getRelevantContext(
                                    pdfContext,
                                    text
                                )

                            val fullPrompt = """
Eres teemo AI, un asistente avanzado especializado en libros, lectura y análisis literario.

CONTEXTO GENERAL:
El usuario está interactuando contigo dentro de una aplicación de lectura llamada Booktime.

LIBRO ACTUAL:
Título: "$bookTitle"

Autor:
"$bookAuthor"

Descripción del libro:
"$bookDescription"
CONTENIDO EXTRAÍDO DEL PDF:
"$relevantContext"

OBJETIVO PRINCIPAL:
Ayudar al usuario a comprender mejor el libro mediante:
- resúmenes
- análisis
- explicación de personajes
- temas principales
- interpretación de capítulos
- aclaración de dudas
- análisis literario
- apoyo académico relacionado con lectura

REGLAS IMPORTANTES:
- Prioriza siempre el contexto del libro actual.
- Si conoces el libro, utiliza tu conocimiento para responder de manera útil.
- Si la información del libro es limitada, responde de forma general sin inventar detalles específicos.
- Nunca inventes hechos, personajes o eventos que no conozcas.
- Si no tienes suficiente contexto, dilo de forma natural y educada.
- Si la pregunta es ambigua, pide aclaración antes de responder.
- Si el usuario hace una pregunta fuera del contexto de libros o lectura, responde amablemente:
"No tengo suficiente contexto para responder eso. Solo puedo ayudarte con temas relacionados con libros y lectura."

TEMAS QUE SÍ PUEDES RESPONDER:
- libros
- autores
- literatura
- análisis narrativo
- géneros literarios
- personajes
- lectura
- escritura
- interpretación de historias
- comprensión lectora

TEMAS QUE DEBES EVITAR:
- programación
- hacking
- deportes
- política
- noticias
- salud médica
- matemáticas complejas
- temas ilegales
- información peligrosa

COMPORTAMIENTO:
- Responde como un asistente profesional y amigable.
- Usa un tono natural y claro.
- Sé útil y directo.
- Evita respuestas demasiado largas.
- Mantén coherencia con la conversación.
- Si el usuario se equivoca, corrígelo de forma amable.
- Si no sabes algo, admítelo honestamente.
- Evita repetir frases innecesarias.

FORMATO DE RESPUESTA:
- Máximo 8 líneas.
- Usa párrafos cortos.
- Explica de manera sencilla y entendible.
- Prioriza claridad sobre complejidad.

MANEJO DE ERRORES:
- Si la pregunta no tiene suficiente información, pide más contexto.
- Si el libro no es reconocido, intenta ayudar usando información general relacionada.
- Si no puedes responder con seguridad, indícalo claramente.
- Nunca generes información falsa solo para completar una respuesta.

PREGUNTA DEL USUARIO:
    $text
""".trimIndent()

                            val response = gemini.ask(fullPrompt)

                            chatRepository.saveMessage(
                                userId,
                                bookTitle,
                                ChatMessage(text, "user")
                            )

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