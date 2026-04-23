package com.example.booktime.tadeo.data.repository

import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.model.GoogleBooksResponse
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
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
    private val API_KEY = "key"
    private val db = FirebaseFirestore.getInstance()

    private val api = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GoogleBooksApi::class.java)

    suspend fun search(query: String) = api.searchBooks(query, API_KEY)

    suspend fun saveBookToFirebase(userId: String, book: Book) {
        db.collection("users")
            .document(userId)
            .collection("my_library")
            .document(book.id.ifEmpty { java.util.UUID.randomUUID().toString() })
            .set(book)
            .await()
    }

    suspend fun getUserLibrary(userId: String): List<Book> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("my_library")
                .orderBy("addedAt", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(Book::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
