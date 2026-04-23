package com.example.booktime.tadeo.viewmodels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.model.GoogleBookItem
import com.example.booktime.tadeo.data.repository.GoogleBooksRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class SearchBookViewModel : ViewModel() {
    private val repository = GoogleBooksRepository()

    var query by mutableStateOf("")
    var results by mutableStateOf<List<GoogleBookItem>>(emptyList())
    var isLoading by mutableStateOf(false)
    var isSaving by mutableStateOf(false)
    var selectedBookId by mutableStateOf<String?>(null)

    fun onSearch() {
        if (query.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            selectedBookId = null
            try {
                val response = repository.search(query)
                results = response.items ?: emptyList()
            } catch (e: Exception) {
                results = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun selectBook(id: String) { selectedBookId = id }

    fun saveBookToFirebase(userId: String, onSuccess: (Book) -> Unit) {
        val selectedItem = results.find { it.id == selectedBookId } ?: return

        viewModelScope.launch {
            isSaving = true
            val newBook = Book(
                id = selectedItem.id,
                title = selectedItem.volumeInfo.title,
                author = selectedItem.volumeInfo.authors?.joinToString() ?: "Desconocido",
                imageUrl = selectedItem.volumeInfo.imageLinks?.httpsThumbnail ?: "",
                addedAt = Timestamp.now(),
                progress = 0
            )

            try {
                repository.saveBookToFirebase(userId, newBook)
                onSuccess(newBook)
            } catch (e: Exception) {
                Log.e("SAVE_ERROR", e.message.toString())
            } finally {
                isSaving = false
            }
        }
    }
}
