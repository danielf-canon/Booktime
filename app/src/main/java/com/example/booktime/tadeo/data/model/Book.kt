package com.example.booktime.tadeo.data.model

import com.google.firebase.Timestamp
data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val addedAt: Timestamp? = null,
    val progress: Int = 0,
    val fileUri: String = "",
    val description: String = "",
    val isFavorite: Boolean = false
)
