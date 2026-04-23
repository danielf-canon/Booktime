package com.example.booktime.tadeo.data.repository

import android.content.Context
import android.net.Uri
import com.example.booktime.tadeo.data.model.Book
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class BookRepository {
    private val storage = FirebaseStorage.getInstance().reference
    private val db = FirebaseFirestore.getInstance()

    suspend fun uploadBook(
        uri: Uri,
        title: String,
        author: String,
        genre: String,
        userId: String,
        context: Context
    ): Result<Unit> {
        return try {
            val bookId = UUID.randomUUID().toString()

            val fileRef = storage.child("books/$userId/$bookId.pdf")
            fileRef.putFile(uri).await()
            val fileUrl = fileRef.downloadUrl.await().toString()

            val newBook = Book(
                id = bookId,
                title = title,
                author = author,
                imageUrl = "",
                addedAt = Timestamp.now(),
                progress = 0
            )

            db.collection("users")
                .document(userId)
                .collection("my_library")
                .document(bookId)
                .set(newBook)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
