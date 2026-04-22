package com.example.booktime.tadeo.views


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booktime.tadeo.components.BooktimeBottomNav
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.navigation.Screen
import com.example.booktime.tadeo.ui.theme.AppWhite
import com.example.booktime.tadeo.ui.theme.FillRectangle
import com.example.booktime.tadeo.ui.theme.PrincipalMenu

@Composable
fun NewEbookScreen(
    navController: NavController,
    onBackClick: () -> Unit
) {

    var selectedItem by remember { mutableStateOf(2) }

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrincipalMenu)
    ) {

        // 🔝 CONTENIDO
        Column(
            modifier = Modifier
                .weight(1f)
        ) {

            ScreenWrapper(onBackClick = onBackClick) {

                Text(
                    text = "Nuevo libro",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppWhite
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "IMPORTAR E-BOOK",
                    color = AppWhite,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(FillRectangle, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AppWhite
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text("Formatos soportados", color = AppWhite)
                        Text("EPUB", color = AppWhite.copy(0.7f))
                        Text("PDF", color = AppWhite.copy(0.7f))
                        Text("MOBI", color = AppWhite.copy(0.7f))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 📂 BOTÓN ARCHIVO
                BooktimeButton(
                    text = "Elegir archivo",
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(16.dp))

                BooktimeTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Título"
                )

                Spacer(modifier = Modifier.height(10.dp))

                BooktimeTextField(
                    value = author,
                    onValueChange = { author = it },
                    placeholder = "Autor"
                )

                Spacer(modifier = Modifier.height(10.dp))

                BooktimeTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    placeholder = "Género"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 💾 GUARDAR
                BooktimeButton(
                    text = "GUARDAR",
                    onClick = { }
                )
            }
        }

        BooktimeBottomNav(
            selectedItem = selectedItem,
            onItemSelected = { index ->
                selectedItem = index

                when (index) {
                    0 -> navController.navigate(Screen.Main.route)
                    1 -> navController.navigate("stats")
                    2 -> navController.navigate(Screen.NewBook.route)
                    3 -> navController.navigate(Screen.ComingSoon.route)
                    4 -> navController.navigate(Screen.Settings.route)
                }
            }
        )
    }
}