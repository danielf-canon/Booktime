package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeBottomNav
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.components.SettingsCard
import com.example.booktime.tadeo.components.SettingsItem
import com.example.booktime.tadeo.components.SettingsSectionTitle
import com.example.booktime.tadeo.components.SettingsSwitchItem
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    onBackClick: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onReadingSettingsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onBottomNavClick: (Int) -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val darkModeEnabled by viewModel.darkModeEnabled
    val readingReminders by viewModel.readingReminders

    Scaffold(
        bottomBar = { BooktimeBottomNav(selectedItem = 4, onItemSelected = onBottomNavClick) },
        containerColor = PrincipalMenu
    ) { padding ->
        ScreenWrapper(onBackClick = onBackClick) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                // Sección: Perfil
                SettingsSectionTitle(stringResource(id = R.string.my_account_section))
                SettingsCard {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = stringResource(id = R.string.edit_profile),
                        subtitle = stringResource(id = R.string.edit_profile_subtitle),
                        onClick = onEditProfileClick
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = stringResource(id = R.string.privacy_security),
                        subtitle = stringResource(id = R.string.privacy_security_subtitle),
                        onClick = onChangePasswordClick
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección: Preferencias de Lectura
                SettingsSectionTitle(stringResource(id = R.string.reading_prefs_section))
                SettingsCard {
                    SettingsSwitchItem(
                        icon = Icons.Default.Brightness4,
                        title = stringResource(id = R.string.dark_mode),
                        checked = darkModeEnabled,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        icon = Icons.Default.ImportContacts,
                        title = stringResource(id = R.string.reading_style),
                        subtitle = stringResource(id = R.string.reading_style_subtitle),
                        onClick = onReadingSettingsClick
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección: Biblioteca y Datos
                SettingsSectionTitle(stringResource(id = R.string.library_storage_section))
                SettingsCard {
                    SettingsItem(
                        icon = Icons.Default.DeleteSweep,
                        title = stringResource(id = R.string.clear_cache),
                        subtitle = stringResource(id = R.string.clear_cache_subtitle)
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        icon = Icons.Default.SdCard,
                        title = stringResource(id = R.string.download_location),
                        subtitle = stringResource(id = R.string.download_location_subtitle)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sección: Notificaciones
                SettingsSectionTitle(stringResource(id = R.string.notifications_section))
                SettingsCard {
                    SettingsSwitchItem(
                        icon = Icons.Default.NotificationsActive,
                        title = stringResource(id = R.string.reading_reminders),
                        checked = readingReminders,
                        onCheckedChange = { viewModel.toggleReadingReminders(it) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón de Cerrar Sesión
                BooktimeButton(
                    text = stringResource(id = R.string.logout_button),
                    onClick = {
                        viewModel.logout(onLogoutClick)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.app_version),
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsViewPreview() {
    BooktimeTheme(darkTheme = true, dynamicColor = false) {
        SettingsView()
    }
}
