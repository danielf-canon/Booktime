package com.example.booktime.tadeo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import com.example.booktime.tadeo.ui.theme.ButtonGreen
import com.example.booktime.tadeo.ui.theme.OtherMenuBackground
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.ui.theme.BooktimeTheme

data class Book(val id: Int, val title: String, val author: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksView(
    onFavoritesClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    val books = remember {
        mutableStateListOf<Book>()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = PrincipalMenu
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(id = R.string.my_books_title),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrincipalMenu
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = PrincipalMenu,
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = onFavoritesClick,
                        icon = { Icon(Icons.Default.Favorite, contentDescription = stringResource(id = R.string.favorites), tint = Color.White) },
                        label = null,
                        alwaysShowLabel = false
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = { /* Ya estamos en el menú principal */ },
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(id = R.string.main_menu_label), tint = Color.White) },
                        label = null,
                        alwaysShowLabel = false
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = onSettingsClick,
                        icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.options_label), tint = Color.White) },
                        label = null,
                        alwaysShowLabel = false
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = ButtonGreen,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_pdf_label))
                }
            },
            containerColor = PrincipalMenu
        ) { innerPadding ->
            if (books.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.digital_library),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(books) { book ->
                            BookItem(book)
                        }
                    }
                }
            } else {
                // Si no hay libros, no mostramos nada dentro del Scaffold
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize())
            }
        }
    }
}

@Composable
fun BookItem(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { /* Abrir libro */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = OtherMenuBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder para la carátula
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrincipalMenu.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "PDF",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = book.title,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp),
                maxLines = 2
            )
            Text(
                text = book.author,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BooksViewPreview() {
    BooktimeTheme(darkTheme = true, dynamicColor = false) {
        BooksView()
    }
}
