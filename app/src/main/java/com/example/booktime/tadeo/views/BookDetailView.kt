package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.repository.BookRepository
import com.example.booktime.tadeo.ui.theme.ButtonGreen
import com.example.booktime.tadeo.ui.theme.OtherMenuBackground
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailView(
    bookId: String,
    onBackClick: () -> Unit,
    onReadClick: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { BookRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    
    var book by remember { mutableStateOf<Book?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(bookId, userId) {
        if (userId != null) {
            val library = repository.getUserLibrary(context, userId)
            book = library.find { it.id == bookId }
            isLoading = false
        }
    }

    Scaffold(
        containerColor = PrincipalMenu,
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Libro", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    book?.let { b ->
                        IconButton(onClick = {
                            userId?.let { uid ->
                                scope.launch {
                                    val result = repository.toggleFavorite(context, uid, b.id)
                                    if (result.isSuccess) {
                                        book = b.copy(isFavorite = result.getOrThrow())
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (b.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (b.isFavorite) Color.Red else Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrincipalMenu)
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ButtonGreen)
            }
        } else if (book == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Libro no encontrado", color = Color.White)
            }
        } else {
            val currentBook = book!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Portada del Libro
                Surface(
                    modifier = Modifier
                        .width(180.dp)
                        .height(270.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    color = OtherMenuBackground
                ) {
                    if (currentBook.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = currentBook.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Book, contentDescription = null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(80.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Título y Autor
                Text(
                    text = currentBook.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = currentBook.author,
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Descripción
                Text(
                    text = "Descripción",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (currentBook.description.isNotEmpty()) currentBook.description else "No hay descripción disponible para este libro.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(40.dp))

                // Botón Leer
                Button(
                    onClick = { onReadClick(currentBook.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
                ) {
                    Text("Leer", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
