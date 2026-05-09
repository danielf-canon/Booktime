package com.example.booktime.tadeo.analytics

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Context
import com.example.booktime.tadeo.data.repository.BookRepository

class AnalyticsViewModel : ViewModel() {

    var totalBooks = mutableStateOf(0)
        private set

    var favoriteBooks = mutableStateOf(0)
        private set

    private val db = FirebaseFirestore.getInstance()

    private val auth = FirebaseAuth.getInstance()

    private val repository = BookRepository()

    fun loadAnalytics(context: Context) {

        val userId = auth.currentUser?.uid ?: return

        totalBooks.value = 0
        favoriteBooks.value = 0

        repository.getUserLibrary(
            context = context,
            userId = userId
        ) { books ->

            totalBooks.value = books.size

            favoriteBooks.value = books.count {
                it.isFavorite
            }
        }
    }