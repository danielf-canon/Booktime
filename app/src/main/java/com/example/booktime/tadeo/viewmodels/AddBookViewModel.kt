package com.example.booktime.tadeo.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booktime.tadeo.data.repository.BookRepository
import kotlinx.coroutines.launch

data class AddBookState(
    val title: String = "",
    val author: String = "",
    val genre: String = "",
    val fileUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AddBookViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {

    var state by mutableStateOf(AddBookState())
        private set

    fun onTitleChange(value: String) {
        state = state.copy(title = value)
    }

    fun onAuthorChange(value: String) {
        state = state.copy(author = value)
    }

    fun onGenreChange(value: String) {
        state = state.copy(genre = value)
    }

    fun onFileSelected(uri: Uri) {
        state = state.copy(fileUri = uri)
    }

    fun saveBook(userId: String, context: Context) {

        val uri = state.fileUri ?: run {
            state = state.copy(error = "Selecciona un archivo")
            return
        }

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            val result = repository.uploadBook(
                uri,
                state.title,
                state.author,
                state.genre,
                userId,
                context
            )

            state = result.fold(
                onSuccess = {
                    state.copy(
                        isLoading = false,
                        success = true,
                        title = "",
                        author = "",
                        genre = "",
                        fileUri = null
                    )
                },
                onFailure = {
                    state.copy(
                        isLoading = false,
                        error = it.message ?: "Error desconocido"
                    )
                }
            )
        }
    }
}