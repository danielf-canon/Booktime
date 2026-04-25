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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Timer
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
import com.example.booktime.tadeo.components.*
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.viewmodels.SettingsViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import com.example.booktime.tadeo.ui.theme.ButtonGreen
import java.util.Calendar

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
    val context = LocalContext.current
    val darkModeEnabled by viewModel.darkModeEnabled
    val readingReminders by viewModel.readingReminders
    val reminderFrequency by viewModel.reminderFrequency
    val reminderTime by viewModel.reminderTime
    val reminderDayOfWeek by viewModel.reminderDayOfWeek
    val downloadLocation by viewModel.downloadLocation
    val showDialog by viewModel.showDialog

    // Cargar ajustes al entrar
    LaunchedEffect(Unit) {
        viewModel.loadSettings()
    }

    var showFrequencyMenu by remember { mutableStateOf(false) }
    var showDayMenu by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Launcher para permiso de notificaciones (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            viewModel.toggleReadingReminders(false)
        }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = reminderTime.split(":")[0].toIntOrNull() ?: 20,
        initialMinute = reminderTime.split(":")[1].toIntOrNull() ?: 0,
        is24Hour = true
    )

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val formattedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    viewModel.setReminderTime(formattedTime)
                    showTimePicker = false
                }) {
                    Text("Confirmar", color = ButtonGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = PrincipalMenu,
            title = { Text("Seleccionar hora", color = Color.White) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = Color.White.copy(alpha = 0.1f),
                            selectorColor = ButtonGreen,
                            containerColor = PrincipalMenu,
                            periodSelectorSelectedContainerColor = ButtonGreen,
                            periodSelectorUnselectedContainerColor = Color.Transparent,
                            timeSelectorSelectedContainerColor = ButtonGreen.copy(alpha = 0.2f),
                            timeSelectorUnselectedContainerColor = Color.White.copy(alpha = 0.1f),
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = Color.White
                        )
                    )
                }
            }
        )
    }

    // Launcher para seleccionar carpeta de descarga
    val directoryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            viewModel.setDownloadLocation(it.toString())
        }
    }

    if (showDialog != null) {
        AnimatedDialog(
            title = "Ajustes",
            text = showDialog!!,
            onDismiss = { viewModel.dismissDialog() }
        )
    }

    Scaffold(
        bottomBar = { BooktimeBottomNav(selectedItem = 4, onItemSelected = onBottomNavClick) },
        containerColor = PrincipalMenu
    ) { padding ->
        ScreenWrapper(
            onBackClick = onBackClick,
            verticalArrangement = Arrangement.Top
        ) {
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
                        subtitle = stringResource(id = R.string.clear_cache_subtitle),
                        onClick = { viewModel.clearCache(context) }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        icon = Icons.Default.SdCard,
                        title = stringResource(id = R.string.download_location),
                        subtitle = downloadLocation,
                        onClick = { directoryPickerLauncher.launch(null) }
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
                        onCheckedChange = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                            viewModel.toggleReadingReminders(enabled)
                        }
                    )
                    
                    if (readingReminders) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        
                        // Frecuencia
                        Box {
                            SettingsItem(
                                icon = Icons.Default.Schedule,
                                title = stringResource(id = R.string.reminder_frequency_label),
                                subtitle = reminderFrequency,
                                onClick = { showFrequencyMenu = true }
                            )
                            DropdownMenu(
                                expanded = showFrequencyMenu,
                                onDismissRequest = { showFrequencyMenu = false },
                                modifier = Modifier.background(PrincipalMenu)
                            ) {
                                val frequencies = listOf(
                                    stringResource(id = R.string.reminder_frequency_daily),
                                    stringResource(id = R.string.reminder_frequency_weekly),
                                    stringResource(id = R.string.reminder_frequency_custom)
                                )
                                frequencies.forEach { freq ->
                                    DropdownMenuItem(
                                        text = { Text(freq, color = Color.White) },
                                        onClick = {
                                            viewModel.setReminderFrequency(freq)
                                            showFrequencyMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))

                        // Día de la semana (Solo si es Semanalmente)
                        if (reminderFrequency == stringResource(id = R.string.reminder_frequency_weekly)) {
                            Box {
                                SettingsItem(
                                    icon = Icons.Default.Schedule,
                                    title = stringResource(id = R.string.reminder_day_label),
                                    subtitle = reminderDayOfWeek,
                                    onClick = { showDayMenu = true }
                                )
                                DropdownMenu(
                                    expanded = showDayMenu,
                                    onDismissRequest = { showDayMenu = false },
                                    modifier = Modifier.background(PrincipalMenu)
                                ) {
                                    val days = listOf(
                                        stringResource(id = R.string.day_monday),
                                        stringResource(id = R.string.day_tuesday),
                                        stringResource(id = R.string.day_wednesday),
                                        stringResource(id = R.string.day_thursday),
                                        stringResource(id = R.string.day_friday),
                                        stringResource(id = R.string.day_saturday),
                                        stringResource(id = R.string.day_sunday)
                                    )
                                    days.forEach { day ->
                                        DropdownMenuItem(
                                            text = { Text(day, color = Color.White) },
                                            onClick = {
                                                viewModel.setReminderDayOfWeek(day)
                                                showDayMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        }

                        // Hora
                        SettingsItem(
                            icon = Icons.Default.Timer,
                            title = stringResource(id = R.string.reminder_time_label),
                            subtitle = reminderTime,
                            onClick = { showTimePicker = true }
                        )
                    }
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
