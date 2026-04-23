package com.example.booktime.tadeo.views

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.*
import com.example.booktime.tadeo.ui.theme.*
import com.example.booktime.tadeo.viewmodels.AddBookViewModel

@Composable
fun AddBookScreen(
    viewModel: AddBookViewModel = AddBookViewModel(),
    userId: String,
    onBackClick: () -> Unit
) {
    val state = viewModel.state
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.onFileSelected(it) } }

    Scaffold(
        bottomBar = {
            BooktimeBottomNav(selectedItem = 2, onItemSelected = { /* Navegación */ })
        }
    ) { padding ->
        ScreenWrapper(onBackClick = onBackClick) {

            Text(
                text = stringResource(R.string.import_ebook_title),
                color = AppWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = FillRectangle
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(30.dp),
                        tint = AppWhite
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column {
                    Text(
                        text = stringResource(R.string.supported_formats_label),
                        color = AppWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    FormatItem(stringResource(R.string.format_epub))
                    FormatItem(stringResource(R.string.format_pdf))
                    FormatItem(stringResource(R.string.format_mobi))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { launcher.launch(arrayOf("application/pdf", "application/epub+zip")) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9D9D9))
            ) {
                Text(
                    text = stringResource(R.string.choose_file_button),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BooktimeTextField(
                value = state.title,
                onValueChange = viewModel::onTitleChange,
                placeholder = stringResource(R.string.book_title_label),
                modifier = Modifier.padding(vertical = 4.dp),
            )

            BooktimeTextField(
                value = state.author,
                onValueChange = viewModel::onAuthorChange,
                placeholder = stringResource(R.string.book_author_label),
                modifier = Modifier.padding(vertical = 4.dp),
            )

            BooktimeTextField(
                value = state.genre,
                onValueChange = viewModel::onGenreChange,
                placeholder = stringResource(R.string.book_genre_label),
                modifier = Modifier.padding(vertical = 4.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))

            BooktimeButton(
                text = stringResource(R.string.save_book_button),
                onClick = { viewModel.saveBook(userId, context) },
                isLoading = state.isLoading,
                modifier = Modifier.width(200.dp) // Ancho ajustado a la imagen
            )

            state.error?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }
            if (state.success) {
                Text(
                    text = stringResource(R.string.book_saved_success),
                    color = ButtonGreen,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun FormatItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = false,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                uncheckedColor = AppWhite.copy(alpha = 0.6f),
                checkmarkColor = ButtonGreen
            )
        )
        Text(text = text, color = AppWhite, fontSize = 12.sp)
    }
}
