package com.example.booktime.tadeo.data.model

data class ChatMessage(
    val text: String = "",
    val sender: String = "",
    val timestamp: Long = System.currentTimeMillis()
)