package com.example.booktime.tadeo.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.data.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        coverBitmap = null // Limpiar portada anterior


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
            val mimeType = context.contentResolver.getType(uri)
            val extension = fileName.lowercase()
            val isPdf = mimeType == "application/pdf" || extension.endsWith(".pdf")
            
            Log.d("AddBookViewModel", "Archivo seleccionado: $fileName, Mime: $mimeType, esPdf: $isPdf")
            
            if (isPdf) {
                extractPdfCover(uri, context)
            } else {
                Log.d("AddBookViewModel", "No se intenta extraer portada (no es PDF)")
            }
        }
    }

    private suspend fun extractPdfCover(uri: Uri, context: Context) = withContext(Dispatchers.IO) {
        var tempFile: java.io.File? = null
        try {
            Log.d("AddBookViewModel", "Iniciando extracción de portada para: $uri")
            
            // 1. Crear una copia temporal local para asegurar que sea seekable (requerido por PdfRenderer)
            tempFile = java.io.File(context.cacheDir, "temp_pdf_cover.pdf")
            context.contentResolver.openInputStream(uri)?.use { input ->
                tempFile!!.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // 2. Abrir el archivo temporal
            val pfd = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pfd?.use { fd ->
                val renderer = PdfRenderer(fd)
                if (renderer.pageCount > 0) {
                    val page = renderer.openPage(0)
                    
                    // Escalar proporcionalmente
                    val width = 400 
                    val height = (width * (page.height.toFloat() / page.width.toFloat())).toInt()
                    
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    withContext(Dispatchers.Main) {
                        coverBitmap = bitmap
                        Log.d("AddBookViewModel", "Portada generada con éxito (${bitmap.width}x${bitmap.height})")
                    }
                    page.close()
                }
                renderer.close()
            }
        } catch (e: Exception) {
            Log.e("AddBookViewModel", "Error crítico al extraer portada", e)
        } finally {
            tempFile?.delete()
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

            // Convertir el bitmap de la portada a ByteArray para subida directa
            val coverBytes = coverBitmap?.let { bitmap ->
                val stream = java.io.ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, stream)
                stream.toByteArray()
            }

            val result = repository.uploadBook(
                uri = selectedUri!!,
                title = title,
                author = author,
                genre = genre,
                userId = userId,
                context = context,
                coverBytes = coverBytes
            )

            if (result.isSuccess) {
                Log.d("AddBookViewModel", "Libro guardado con éxito en Firestore")
                onSuccess()
            } else {
                val error = result.exceptionOrNull()?.message
                Log.e("AddBookViewModel", "Error al guardar libro: $error")
                errorMessage = context.getString(R.string.error_saving_book, error)
            }
            isLoading = false
        }
    }

}
