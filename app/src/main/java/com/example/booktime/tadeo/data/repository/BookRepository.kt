package com.example.booktime.tadeo.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.booktime.tadeo.data.model.Book
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import java.util.UUID

class BookRepository {
    private val gson = Gson()

    private fun getPrefs(context: Context, userId: String) =
        context.getSharedPreferences("books_$userId", Context.MODE_PRIVATE)

    suspend fun uploadBook(
        uri: Uri,
        title: String,
        author: String,
        genre: String,
        userId: String,
        context: Context
    ): Result<Unit> {
        return try {
            delay(1000)
            val book = Book(
                id = UUID.randomUUID().toString(),
                title = title,
                author = author,
                fileUri = uri.toString(),
                addedAt = Timestamp.now(),
                progress = 0
            )

            val prefs = getPrefs(context, userId)
            val existingBooksJson = prefs.getString("library", "[]")
            val type = object : TypeToken<List<Book>>() {}.type
            val books: MutableList<Book> = gson.fromJson(existingBooksJson, type)
            books.add(book)

            prefs.edit().putString("library", gson.toJson(books)).apply()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserLibrary(context: Context, userId: String): List<Book> {
        return try {
            val prefs = getPrefs(context, userId)
            val existingBooksJson = prefs.getString("library", "[]")
            val type = object : TypeToken<List<Book>>() {}.type
            gson.fromJson(existingBooksJson, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleFavorite(context: Context, userId: String, bookId: String): Result<Boolean> {
        return try {
            val prefs = getPrefs(context, userId)
            val existingBooksJson = prefs.getString("library", "[]")
            val type = object : TypeToken<List<Book>>() {}.type
            val books: MutableList<Book> = gson.fromJson(existingBooksJson, type)
            
            var newState = false
            val updatedBooks = books.map { 
                if (it.id == bookId) {
                    newState = !it.isFavorite
                    it.copy(isFavorite = newState)
                } else it
            }
            
            prefs.edit().putString("library", gson.toJson(updatedBooks)).apply()
            Result.success(newState)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun downloadGoogleBook(
        bookId: String,
        title: String,
        location: String
    ): Result<String> {
        return try {
            // Simulamos la descarga de un libro de Google Books
            delay(2000)
            Log.d("BookRepository", "Descargando libro $title en: $location")
            Result.success("Libro descargado en $location/$title.pdf")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
