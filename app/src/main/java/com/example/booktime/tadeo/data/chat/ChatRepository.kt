package com.example.booktime.tadeo.data.chat

import com.example.booktime.tadeo.data.model.ChatMessage
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveMessage(
        userId: String,
        bookId: String,
        message: ChatMessage
    ) {

        db.collection("users")
            .document(userId)
            .collection("chats")
            .document(bookId)
            .collection("messages")
            .add(message)
    }

    fun loadMessages(
        userId: String,
        bookId: String,
        onResult: (List<ChatMessage>) -> Unit
    ) {

        db.collection("users")
            .document(userId)
            .collection("chats")
            .document(bookId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { result ->

                val messages = result.documents.mapNotNull {
                    it.toObject(ChatMessage::class.java)
                }

                onResult(messages)
            }
    }
}