package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.repository.BookRepository
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteBooksView(
    onBackClick: () -> Unit,
    onBookClick: (String) -> Unit
) {
    val context = LocalContext.current
    val repository = remember { BookRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var favoriteBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        userId?.let { uid ->
            val library = repository.getUserLibrary(context, uid)
            favoriteBooks = library.filter { it.isFavorite }
            isLoading = false
        }
    }

    Scaffold(
        containerColor = PrincipalMenu,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Favoritos", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrincipalMenu)
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (favoriteBooks.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes libros favoritos aún", color = Color.White.copy(alpha = 0.5f))
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteBooks) { book ->
                    BookItem(book, onClick = { onBookClick(book.id) })
                }
            }
        }
    }
}
