package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.components.SettingsSectionTitle
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileView(onBackClick: () -> Unit = {}) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val database = FirebaseDatabase.getInstance().reference
    
    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    ScreenWrapper(onBackClick = onBackClick) {
        // Sección: Información Básica
        SettingsSectionTitle(stringResource(id = R.string.personal_info_section))
        
        BooktimeTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = stringResource(id = R.string.full_name_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        BooktimeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(id = R.string.email_placeholder),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        BooktimeButton(
            text = stringResource(id = R.string.save_changes),
            isLoading = isLoading,
            onClick = {
                if (user != null) {
                    isLoading = true
                    // Actualizar en Realtime Database
                    val updates = mapOf(
                        "name" to name,
                        "email" to email
                    )
                    database.child("users").child(user.uid).updateChildren(updates)
                        .addOnCompleteListener { 
                            isLoading = false
                            // Aquí podrías añadir un mensaje de éxito
                        }
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileViewPreview() {
    BooktimeTheme(darkTheme = true, dynamicColor = false) {
        EditProfileView()
    }
}
