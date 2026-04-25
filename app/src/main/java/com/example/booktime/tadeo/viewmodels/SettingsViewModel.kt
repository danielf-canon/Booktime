package com.example.booktime.tadeo.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log
import android.content.Context
import coil.imageLoader
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.utils.ReminderScheduler

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    
    // Función para obtener las preferencias específicas del usuario actual
    private fun getPrefs(uid: String? = auth.currentUser?.uid): android.content.SharedPreferences {
        return getApplication<Application>().getSharedPreferences(
            "booktime_settings_${uid ?: "guest"}", 
            Context.MODE_PRIVATE
        )
    }
    
    private val userId: String? get() = auth.currentUser?.uid

    private val _darkModeEnabled = mutableStateOf(false)
    val darkModeEnabled: State<Boolean> = _darkModeEnabled

    private val _readingReminders = mutableStateOf(true)
    val readingReminders: State<Boolean> = _readingReminders

    private val _reminderFrequency = mutableStateOf("")
    val reminderFrequency: State<String> = _reminderFrequency

    private val _reminderTime = mutableStateOf("20:00")
    val reminderTime: State<String> = _reminderTime

    private val _reminderDayOfWeek = mutableStateOf("")
    val reminderDayOfWeek: State<String> = _reminderDayOfWeek

    private val _fontSize = mutableStateOf(16f)
    val fontSize: State<Float> = _fontSize

    private val _downloadLocation = mutableStateOf("Almacenamiento interno")
    val downloadLocation: State<String> = _downloadLocation

    private val _showDialog = mutableStateOf<String?>(null)
    val showDialog: State<String?> = _showDialog

    init {
        Log.d("SettingsViewModel", "Initializing with userId: $userId")
        
        // Inicializar estados con valores locales del usuario actual antes de cargar de Firebase
        val prefs = getPrefs()
        _reminderFrequency.value = prefs.getString("reminder_frequency", application.getString(R.string.reminder_frequency_daily)) 
            ?: application.getString(R.string.reminder_frequency_daily)
        _reminderTime.value = prefs.getString("reminder_time", "20:00") ?: "20:00"
        _reminderDayOfWeek.value = prefs.getString("reminder_day", application.getString(R.string.day_monday)) 
            ?: application.getString(R.string.day_monday)
        _downloadLocation.value = prefs.getString("download_location", "Almacenamiento interno") ?: "Almacenamiento interno"
        
        loadSettings()
    }

    fun loadSettings() {
        userId?.let { uid ->
            viewModelScope.launch {
                try {
                    val snapshot = database.child("users").child(uid).child("settings").get().await()
                    if (snapshot.exists()) {
                        val app = getApplication<Application>()
                        _readingReminders.value = snapshot.child("readingReminders").getValue(Boolean::class.java) ?: true
                        _reminderFrequency.value = snapshot.child("reminderFrequency").getValue(String::class.java) ?: app.getString(R.string.reminder_frequency_daily)
                        _reminderTime.value = snapshot.child("reminderTime").getValue(String::class.java) ?: "20:00"
                        _reminderDayOfWeek.value = snapshot.child("reminderDayOfWeek").getValue(String::class.java) ?: app.getString(R.string.day_monday)
                        _darkModeEnabled.value = snapshot.child("darkModeEnabled").getValue(Boolean::class.java) ?: false
                        
                        // Sincronizar con local prefs específicas del usuario
                        getPrefs(uid).edit().apply {
                            putBoolean("reading_reminders", _readingReminders.value)
                            putString("reminder_frequency", _reminderFrequency.value)
                            putString("reminder_time", _reminderTime.value)
                            putString("reminder_day", _reminderDayOfWeek.value)
                            apply()
                        }
                        updateReminder()
                    }
                } catch (e: Exception) {
                    Log.e("SettingsViewModel", "Error loading settings from Firebase", e)
                }
            }
        }
    }

    private fun saveSettingsToFirebase() {
        userId?.let { uid ->
            val settings = mapOf(
                "readingReminders" to _readingReminders.value,
                "reminderFrequency" to _reminderFrequency.value,
                "reminderTime" to _reminderTime.value,
                "reminderDayOfWeek" to _reminderDayOfWeek.value,
                "darkModeEnabled" to _darkModeEnabled.value
            )
            viewModelScope.launch {
                try {
                    database.child("users").child(uid).child("settings").setValue(settings).await()
                    updateReminder()
                } catch (e: Exception) {
                    Log.e("SettingsViewModel", "Error saving settings to Firebase", e)
                }
            }
        }
    }

    private fun updateReminder() {
        ReminderScheduler.scheduleReminder(
            getApplication(),
            _readingReminders.value,
            _reminderFrequency.value,
            _reminderTime.value,
            _reminderDayOfWeek.value
        )
    }

    fun setFontSize(size: Float) {
        _fontSize.value = size
    }

    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
        saveSettingsToFirebase()
    }

    fun toggleReadingReminders(enabled: Boolean) {
        _readingReminders.value = enabled
        getPrefs().edit().putBoolean("reading_reminders", enabled).apply()
        saveSettingsToFirebase()
    }

    fun setReminderFrequency(frequency: String) {
        _reminderFrequency.value = frequency
        getPrefs().edit().putString("reminder_frequency", frequency).apply()
        saveSettingsToFirebase()
    }

    fun setReminderTime(time: String) {
        _reminderTime.value = time
        getPrefs().edit().putString("reminder_time", time).apply()
        saveSettingsToFirebase()
    }

    fun setReminderDayOfWeek(day: String) {
        _reminderDayOfWeek.value = day
        getPrefs().edit().putString("reminder_day", day).apply()
        saveSettingsToFirebase()
    }

    fun clearCache(context: Context) {
        try {
            context.cacheDir.deleteRecursively()
            context.externalCacheDir?.deleteRecursively()
            context.imageLoader.memoryCache?.clear()
            context.imageLoader.diskCache?.clear()
            _showDialog.value = "Caché limpiada correctamente (Archivos e Imágenes)"
        } catch (e: Exception) {
            _showDialog.value = "Error al limpiar caché: ${e.message}"
        }
    }

    fun setDownloadLocation(path: String) {
        _downloadLocation.value = path
        getPrefs().edit().putString("download_location", path).apply()
    }

    fun dismissDialog() {
        _showDialog.value = null
    }

    fun logout(onLogout: () -> Unit) {
        Log.d("SettingsViewModel", "Logging out user $userId")
        
        // 1. Cancelar cualquier recordatorio activo antes de salir
        ReminderScheduler.scheduleReminder(
            getApplication(),
            isEnabled = false,
            frequency = _reminderFrequency.value,
            time = _reminderTime.value,
            dayOfWeek = _reminderDayOfWeek.value
        )
        
        // 2. Cerrar sesión en Firebase
        auth.signOut()
        onLogout()
    }
}
