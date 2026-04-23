package com.example.booktime.tadeo.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import com.example.booktime.tadeo.data.model.Book
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
    ): Result<Unit> = withContext(Dispatchers.IO) {

        try {
            val extension = getFileExtension(uri, context)
                ?: return@withContext Result.failure(Exception("Formato no válido"))

            val bookId = UUID.randomUUID().toString()

            val bookRef = storage.child("books/$userId/$bookId.$extension")
            bookRef.putFile(uri).await()

            val fileUrl = bookRef.downloadUrl.await().toString()

            val coverUrl = if (extension == "pdf") {
                generateAndUploadPdfCover(uri, userId, bookId, context)
            } else {
                ""
            }

            val book = hashMapOf(
                "id" to bookId,
                "title" to title,
                "author" to author,
                "genre" to genre,
                "fileUrl" to fileUrl,
                "fileType" to extension,
                "coverUrl" to coverUrl,
                "userId" to userId,
                "createdAt" to FieldValue.serverTimestamp()
            )

            db.collection("books")
                .document(bookId)
                .set(book)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateAndUploadPdfCover(
        uri: Uri,
        userId: String,
        bookId: String,
        context: Context
    ): String {

        val bitmap = generatePdfThumbnail(context, uri) ?: return ""

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)

        val coverRef = storage.child("covers/$userId/$bookId.jpg")

        coverRef.putBytes(baos.toByteArray()).await()

        return coverRef.downloadUrl.await().toString()
    }

    private fun generatePdfThumbnail(context: Context, uri: Uri): Bitmap? {
        return try {
            val fileDescriptor: ParcelFileDescriptor =
                context.contentResolver.openFileDescriptor(uri, "r") ?: return null

            val renderer = PdfRenderer(fileDescriptor)
            val page = renderer.openPage(0)

            val bitmap = createBitmap(page.width, page.height)

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            page.close()
            renderer.close()
            fileDescriptor.close()

            bitmap

        } catch (e: Exception) {
            null
        }
    }

    private fun getFileExtension(uri: Uri, context: Context): String? {
        val type = context.contentResolver.getType(uri)

        return when (type) {
            "application/pdf" -> "pdf"
            "application/epub+zip" -> "epub"
            "application/x-mobipocket-ebook" -> "mobi"
            else -> null
        }
    }

}
