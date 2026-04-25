package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeBottomNav
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.components.SettingsSectionTitle
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.google.firebase.auth.FirebaseAuth

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileView(
    onBackClick: () -> Unit = {},
    onBottomNavClick: (Int) -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    
    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val successMessage by viewModel.successMessage

    if (errorMessage != null || successMessage != null) {
        AnimatedDialog(
            title = if (errorMessage != null) stringResource(id = R.string.error_title) else "Éxito",
            text = errorMessage ?: successMessage!!,
            onDismiss = { 
                viewModel.clearMessages()
            }
        )
    }

    Scaffold(
        bottomBar = { BooktimeBottomNav(selectedItem = 4, onItemSelected = onBottomNavClick) },
        containerColor = com.example.booktime.tadeo.ui.theme.PrincipalMenu
    ) { padding ->
        ScreenWrapper(onBackClick = onBackClick) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                // Espaciador para bajar el título e información según la solicitud del usuario
                Spacer(modifier = Modifier.height(32.dp))

                SettingsSectionTitle(stringResource(id = R.string.personal_info_section))
                
                Spacer(modifier = Modifier.height(24.dp))
                
                BooktimeTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = stringResource(id = R.string.full_name_placeholder),
                    modifier = Modifier.padding(bottom = 24.dp),
                )

                BooktimeTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = stringResource(id = R.string.email_placeholder),
                    modifier = Modifier.padding(bottom = 24.dp),
                    enabled = false
                )

                Spacer(modifier = Modifier.weight(1f))

                BooktimeButton(
                    text = stringResource(id = R.string.save_changes),
                    isLoading = isLoading,
                    onClick = {
                        viewModel.updateProfile(
                            name = name,
                            onSuccess = {
                                // Podríamos volver atrás o simplemente mostrar el mensaje
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileViewPreview() {
    BooktimeTheme(darkTheme = true, dynamicColor = false) {
        EditProfileView()
    }
}
