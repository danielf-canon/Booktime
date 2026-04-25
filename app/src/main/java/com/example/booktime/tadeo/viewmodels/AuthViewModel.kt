package com.example.booktime.tadeo.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import android.util.Log

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _successMessage = mutableStateOf<String?>(null)
    val successMessage: State<String?> = _successMessage

    fun login(email: String, password: String, errorEmptyFields: String, errorInvalidEmail: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = errorEmptyFields
            return
        }
        
        if (!com.example.booktime.tadeo.utils.ValidationUtils.isValidEmail(email)) {
            _errorMessage.value = errorInvalidEmail
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Login: Intentando con $email")
                // Aseguramos que Firebase esté listo
                val firebaseAuth = auth 
                
                // Añadimos un timeout de 15 segundos para evitar carga infinita
                val result = withTimeoutOrNull(15000) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).await()
                }

                if (result != null) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Error de conexión: El servidor no responde."
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error en login", e)
                _errorMessage.value = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "El usuario no existe."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta."
                    else -> "Error al iniciar sesión: ${e.localizedMessage}"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        errorEmptyFields: String,
        errorInvalidEmail: String,
        errorPasswordRules: String,
        errorPasswordsDontMatch: String,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _errorMessage.value = errorEmptyFields
            return
        }
        if (!com.example.booktime.tadeo.utils.ValidationUtils.isValidEmail(email)) {
            _errorMessage.value = errorInvalidEmail
            return
        }
        if (!com.example.booktime.tadeo.utils.ValidationUtils.isValidPassword(password)) {
            _errorMessage.value = errorPasswordRules
            return
        }
        if (!com.example.booktime.tadeo.utils.ValidationUtils.passwordsMatch(password, confirmPassword)) {
            _errorMessage.value = errorPasswordsDontMatch
            return
        }

        _isLoading.value = true
        _errorMessage.value = null
        Log.d("AuthViewModel", ">>> INICIANDO PROCESO DE REGISTRO <<<")
        Log.d("AuthViewModel", "Email: $email")

        viewModelScope.launch {
            try {
                // Verificación de seguridad de inicialización
                val firebaseAuth = try { auth } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error al obtener FirebaseAuth", e)
                    _errorMessage.value = "Error crítico: Firebase no está configurado correctamente."
                    return@launch
                }

                Log.d("AuthViewModel", "Paso 1: Intentando crear usuario en Firebase Auth...")
                
                // Verificamos si hay conectividad básica (opcional, pero ayuda)
                Log.d("AuthViewModel", "Verificando instancia de Firebase: ${firebaseAuth.app.name}")

                // 1. Crear usuario con Timeout reducido para detectar fallos de red más rápido
                val authResult = withTimeoutOrNull(15000) {
                    try {
                        Log.d("AuthViewModel", "Llamando a createUserWithEmailAndPassword...")
                        val task = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                        Log.d("AuthViewModel", "Task completada con éxito")
                        task
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "EXCEPCIÓN DENTRO DE CREATEUSER: ${e.message}")
                        throw e
                    }
                }

                if (authResult == null) {
                    Log.e("AuthViewModel", "TIMEOUT: Firebase Auth no respondió en 15 segundos.")
                    _errorMessage.value = "No hay respuesta del servidor. ¿Tienes internet en el emulador?"
                    return@launch
                }

                val user = authResult.user
                if (user != null) {
                    Log.d("AuthViewModel", "Paso 2: Usuario creado (UID: ${user.uid}). Actualizando perfil...")
                    
                    // 2. Actualizar Perfil
                    try {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user.updateProfile(profileUpdates).await()
                        Log.d("AuthViewModel", "Perfil actualizado correctamente.")
                        
                        Log.d("AuthViewModel", ">>> REGISTRO COMPLETADO EXITOSAMENTE <<<")
                        onSuccess()
                    } catch (e: Exception) {
                        Log.e("AuthViewModel", "Error al actualizar perfil: ${e.message}")
                        // Si falla el perfil pero el usuario se creó, igual consideramos éxito
                        onSuccess()
                    }
                } else {
                    Log.e("AuthViewModel", "Error: AuthResult exitoso pero User es nulo.")
                    _errorMessage.value = "Error inesperado de Firebase: Usuario nulo."
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "EXCEPCIÓN CAPTURADA: ${e.javaClass.simpleName} - ${e.message}")
                _errorMessage.value = when (e) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Este correo ya está registrado."
                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "La contraseña es muy débil (mínimo 6 caracteres)."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "El formato del correo es inválido."
                    is com.google.firebase.FirebaseNetworkException -> "Error de red: Verifica tu conexión a internet."
                    else -> "Fallo al registrar: ${e.localizedMessage ?: "Error desconocido"}"
                }
            } finally {
                Log.d("AuthViewModel", "Finalizando estado de carga (isLoading = false)")
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
