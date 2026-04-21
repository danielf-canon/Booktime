package com.example.booktime.tadeo.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val userId = auth.currentUser?.uid

    private val _darkModeEnabled = mutableStateOf(false)
    val darkModeEnabled: State<Boolean> = _darkModeEnabled

    private val _readingReminders = mutableStateOf(true)
    val readingReminders: State<Boolean> = _readingReminders

    init {
        loadSettings()
    }

    private fun loadSettings() {
        userId?.let { uid ->
            database.child("users").child(uid).child("settings").get()
                .addOnSuccessListener { snapshot ->
                    _darkModeEnabled.value = snapshot.child("darkMode").getValue(Boolean::class.java) ?: false
                    _readingReminders.value = snapshot.child("readingReminders").getValue(Boolean::class.java) ?: true
                }
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        saveSetting("darkMode", enabled)
    }

    fun toggleReadingReminders(enabled: Boolean) {
        _readingReminders.value = enabled
        saveSetting("readingReminders", enabled)
    }

    private fun saveSetting(key: String, value: Any) {
        userId?.let { uid ->
            database.child("users").child(uid).child("settings").child(key).setValue(value)
        }
    }

    fun logout(onLogout: () -> Unit) {
        auth.signOut()
        onLogout()
    }
}
