package com.example.booktime.tadeo.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.booktime.tadeo.data.model.Book
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.UUID

class BookRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val gson = Gson()

    private fun getPrefs(context: Context, userId: String) =
        context.getSharedPreferences("books_$userId", Context.MODE_PRIVATE)

    suspend fun uploadBook(
        uri: Uri,
        title: String,
        author: String,
        genre: String,
        userId: String,
        context: Context,
        coverBytes: ByteArray? = null
    ): Result<Unit> {
        return try {
            val bookId = UUID.randomUUID().toString()

            // 1. Subir PDF a Firebase Storage
            val pdfRef = storage.reference.child("users/$userId/books/$bookId.pdf")
            pdfRef.putFile(uri).await()
            val fileUrl = pdfRef.downloadUrl.await().toString()

            // 2. Subir portada si existen los bytes
            var imageUrl = ""
            coverBytes?.let {
                val coverRef = storage.reference.child("users/$userId/covers/$bookId.jpg")
                coverRef.putBytes(it).await()
                imageUrl = coverRef.downloadUrl.await().toString()
                Log.d("BookRepository", "Portada subida con éxito: $imageUrl")
            }

            // 3. Crear objeto Book
            val book = Book(
                id = bookId,
                title = title,
                author = author,
                fileUri = fileUrl,
                imageUrl = imageUrl,
                addedAt = Timestamp.now(),
                progress = 0,
                description = "Género: $genre"
            )

            // 4. Guardar en Firestore
            firestore.collection("users").document(userId)
                .collection("library").document(bookId)
                .set(book).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("BookRepository", "Error uploading book", e)
            Result.failure(e)
        }
    }

    suspend fun getUserLibrary(context: Context, userId: String): List<Book> {
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("library")
                .get().await()
            Log.d("BOOKS_DEBUG", "Documentos encontrados: ${snapshot.documents.size}")
            snapshot.documents.forEach {
                Log.d("BOOKS_DEBUG", "DATA: ${it.data}")
            }

            snapshot.documents.mapNotNull {
                try {
                    it.toObject(Book::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("BookRepository", "Error getting library", e)
            // Fallback al mock si falla (opcional, mejor devolver lista vacía o error)
            emptyList()
        }
    }

    suspend fun toggleFavorite(
        context: Context,
        userId: String,
        bookId: String
    ): Result<Boolean> {

        return try {

            val docRef = firestore.collection("users")
                .document(userId)
                .collection("library")
                .document(bookId)

            val snapshot = docRef.get().await()

            val current =
                snapshot.getBoolean("favorite") ?: false

            val newState = !current

            docRef.update("favorite", newState).await()

            Result.success(newState)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}