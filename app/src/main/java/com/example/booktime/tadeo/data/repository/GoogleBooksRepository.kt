package com.example.booktime.tadeo.data.repository

import android.content.Context
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.model.GoogleBooksResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query as ApiQuery

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @ApiQuery("q") query: String,
        @ApiQuery("key") apiKey: String,
        @ApiQuery("maxResults") maxResults: Int = 20
    ): GoogleBooksResponse
}

class GoogleBooksRepository {
    private val API_KEY = ""
    private val gson = Gson()

    private fun getPrefs(context: Context, userId: String) =
        context.getSharedPreferences("books_$userId", Context.MODE_PRIVATE)

    private val api = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleBooksApi::class.java)

    suspend fun search(query: String) = api.searchBooks(query, API_KEY)

    suspend fun saveBookToFirebase(context: Context, userId: String, book: Book) {
        delay(500)
        val prefs = getPrefs(context, userId)
        val existingBooksJson = prefs.getString("library", "[]")
        val type = object : TypeToken<List<Book>>() {}.type
        val books: MutableList<Book> = gson.fromJson(existingBooksJson, type)
        
        // Evitar duplicados por ID
        if (books.none { it.id == book.id }) {
            books.add(book)
            prefs.edit().putString("library", gson.toJson(books)).apply()
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
}

//Cosas para estudiar de IA

/*Representación de Distancias
Regresión Lineal Simple
Regresión Lineal Múltiple
Clasificador KNN: K-Nearest Neighbors
Clasificador KNN: Proyecto Cáncer de Seno
Clustering: K-Means*/

