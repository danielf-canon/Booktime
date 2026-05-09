package com.example.booktime.tadeo.data.model

import com.google.firebase.Timestamp

data class Book(
    var id: String = "",
    var title: String = "",
    var author: String = "",
    var imageUrl: String = "",
    var addedAt: Timestamp? = null,
    var progress: Int = 0,
    var fileUri: String = "",
    var description: String = "",
    var favorite: Boolean = false
) {
    constructor() : this(
        "",
        "",
        "",
        "",
        null,
        0,
        "",
        "",
        false
    )
}