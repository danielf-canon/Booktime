package com.example.booktime.tadeo.data.model

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("items") val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    @SerializedName("id") val id: String,
    @SerializedName("volumeInfo") val volumeInfo: GoogleVolumeInfo
)

data class GoogleVolumeInfo(
    @SerializedName("title") val title: String,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("description") val description: String?,
    @SerializedName("categories") val categories: List<String>?,
    @SerializedName("imageLinks") val imageLinks: GoogleImageLinks?
)

data class GoogleImageLinks(
    @SerializedName("thumbnail") val thumbnail: String?
) {
    val httpsThumbnail: String? get() = thumbnail?.replace("http://", "https://")
}
