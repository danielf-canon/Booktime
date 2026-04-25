package com.example.booktime.tadeo.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private var user = auth.currentUser

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    fun updateProfile(name: String, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _errorMessage.value = "Usuario no autenticado."
            return
        }
        
        if (name.isBlank()) {
            _errorMessage.value = "Por favor completa el nombre."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        _successMessage.value = null

        viewModelScope.launch {
            try {
                // 1. Actualizar nombre en Profile
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                currentUser.updateProfile(profileUpdates).await()

                // Recargar usuario para asegurar que los cambios se reflejen localmente
                currentUser.reload().await()
                user = auth.currentUser

                _successMessage.value = "Perfil actualizado correctamente."
                onSuccess()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile", e)
                _errorMessage.value = "Error al actualizar: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String, onSuccess: () -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null || currentUser.email == null) {
            _errorMessage.value = "Sesión no válida."
            return
        }

        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            _errorMessage.value = "Por favor completa todos los campos."
            return
        }

        if (!com.example.booktime.tadeo.utils.ValidationUtils.isValidPassword(newPassword)) {
            _errorMessage.value = "La nueva contraseña debe tener entre 8 y 16 caracteres y al menos un número."
            return
        }

        if (newPassword != confirmPassword) {
            _errorMessage.value = "Las nuevas contraseñas no coinciden."
            return
        }

        if (currentPassword == newPassword) {
            _errorMessage.value = "La nueva contraseña no puede ser igual a la actual."
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
        
        currentUser.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                currentUser.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    _isLoading.value = false
                    if (updateTask.isSuccessful) {
                        _successMessage.value = "Contraseña actualizada correctamente."
                        onSuccess()
                    } else {
                        _errorMessage.value = "Error al actualizar contraseña: ${updateTask.exception?.message}"
                    }
                }
            } else {
                _isLoading.value = false
                _errorMessage.value = "La contraseña actual es incorrecta o la sesión ha expirado."
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
