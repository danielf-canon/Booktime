package com.example.booktime.tadeo.views

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.repository.BookRepository
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderView(
    bookId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { BookRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var book by remember { mutableStateOf<Book?>(null) }
    var pdfPages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(bookId, userId) {
        if (userId != null) {
            val library = repository.getUserLibrary(context, userId)
            book = library.find { it.id == bookId }
            
            book?.let { b ->
                if (b.fileUri.isNotEmpty()) {
                    loadPdfPages(context, Uri.parse(b.fileUri)) { pages, error ->
                        pdfPages = pages
                        errorMessage = error
                        isLoading = false
                    }
                } else {
                    errorMessage = "No se encontró el archivo del libro"
                    isLoading = false
                }
            } ?: run {
                errorMessage = "Libro no encontrado"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(book?.title ?: "Lector PDF", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrincipalMenu)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else if (errorMessage != null) {
                Text(errorMessage!!, color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            } else {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pdfPages) { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentScale = ContentScale.FillWidth
                        )
                    }
                }
            }
        }
    }
}

private suspend fun loadPdfPages(
    context: android.content.Context,
    uri: Uri,
    onResult: (List<Bitmap>, String?) -> Unit
) = withContext(Dispatchers.IO) {
    val bitmaps = mutableListOf<Bitmap>()
    try {
        // We need to take persistable URI permission if it's from gallery
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            // Might already have it or not needed
        }

        val pfd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
        pfd?.use { fd ->
            val renderer = PdfRenderer(fd)
            val pageCount = renderer.pageCount
            for (i in 0 until pageCount) {
                val page = renderer.openPage(i)
                val bitmap = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
                page.close()
            }
            renderer.close()
        }
        withContext(Dispatchers.Main) {
            onResult(bitmaps, null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            onResult(emptyList(), "Error al abrir el PDF: ${e.message}")
        }
    }
}
