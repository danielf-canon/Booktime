package com.example.booktime.tadeo.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.booktime.tadeo.data.model.Book

class BooksViewModel : ViewModel() {
    private val _books = mutableStateListOf<Book>(
        Book("1", "El Principito", "Antoine de Saint-Exupéry"),
        Book("2", "Cien años de soledad", "Gabriel García Márquez"),
        Book("3", "Don Quijote de la Mancha", "Miguel de Cervantes"),
        Book("4", "1984", "George Orwell"),
        Book("5", "Orgullo y Prejuicio", "Jane Austen"),
        Book("6", "Rayuela", "Julio Cortázar")
    )
    val books: SnapshotStateList<Book> = _books

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun addBook(book: Book) {
        _books.add(book)
    }

    fun removeBook(book: Book) {
        _books.remove(book)
    }
}
