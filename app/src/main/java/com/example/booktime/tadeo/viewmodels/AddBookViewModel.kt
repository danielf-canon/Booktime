package com.example.booktime.tadeo.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktime.tadeo.data.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.booktime.tadeo.R
import androidx.core.graphics.createBitmap

class AddBookViewModel : ViewModel() {
    private val repository = BookRepository()

    var title by mutableStateOf("")
    var author by mutableStateOf("")
    var genre by mutableStateOf("")
    var selectedUri by mutableStateOf<Uri?>(null)
    var coverBitmap by mutableStateOf<Bitmap?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)


    fun onFileSelected(uri: Uri, context: Context, fileName: String) {
        selectedUri = uri


        val cleanName = fileName.replace(".pdf", "", true)
            .replace(".epub", "", true)
            .replace("_", " ")

        if (cleanName.contains("-")) {
            val parts = cleanName.split("-")
            title = parts[0].trim()
            author = parts[1].trim()
        } else {
            title = cleanName.trim()
        }

        viewModelScope.launch {
            if (context.contentResolver.getType(uri) == "application/pdf") {
                extractPdfCover(uri, context)
            }
        }
    }

    private suspend fun extractPdfCover(uri: Uri, context: Context) = withContext(Dispatchers.IO) {
        try {
            val pfd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")
            pfd?.use { fd ->
                val renderer = PdfRenderer(fd)
                if (renderer.pageCount > 0) {
                    val page = renderer.openPage(0)
                    val bitmap = createBitmap(page.width, page.height)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    withContext(Dispatchers.Main) {
                        coverBitmap = bitmap
                    }
                    page.close()
                }
                renderer.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Dentro de AddBookViewModel.kt
    fun saveBook(userId: String, context: Context, onSuccess: () -> Unit) {
        if (selectedUri == null || title.isBlank()) {
            errorMessage = context.getString(R.string.error_select_file_and_title)
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            val result = repository.uploadBook(
                uri = selectedUri!!,
                title = title,
                author = author,
                genre = genre,
                userId = userId,
                context = context
            )

            if (result.isSuccess) {
                onSuccess()
            } else {
                errorMessage = context.getString(R.string.error_saving_book, result.exceptionOrNull()?.message)
            }
            isLoading = false
        }
    }

}
