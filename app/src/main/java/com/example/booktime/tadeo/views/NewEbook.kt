package com.example.booktime.tadeo.views

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeBottomNav
import com.example.booktime.tadeo.components.BooktimeTextField
import com.example.booktime.tadeo.ui.theme.AppWhite
import com.example.booktime.tadeo.ui.theme.ButtonGreen
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import com.example.booktime.tadeo.viewmodels.AddBookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(
    viewModel: AddBookViewModel = viewModel(),
    userId: String,
    onBackClick: () -> Unit,
    onBottomNavClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    val defaultTitle = stringResource(R.string.book_title_label)

    fun getFileName(uri: Uri): String {
        var name = ""
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex != -1) {
                    name = cursor.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return name.ifEmpty { uri.path?.substringAfterLast('/') ?: defaultTitle }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val fileName = getFileName(it)
            viewModel.onFileSelected(it, context, fileName)
        }
    }

    Scaffold(
        containerColor = PrincipalMenu,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.new_book_title), color = AppWhite, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back), tint = AppWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrincipalMenu,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        },
        bottomBar = {
            BooktimeBottomNav(selectedItem = 2, onItemSelected = onBottomNavClick)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.import_ebook_title), color = AppWhite, fontSize = 16.sp, fontWeight = FontWeight.Black)

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Caja de portada dinámica
                Surface(
                    modifier = Modifier.width(100.dp).height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF636D7A)
                ) {
                    if (viewModel.coverBitmap != null) {
                        Image(
                            bitmap = viewModel.coverBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(35.dp), tint = AppWhite)
                    }
                }

                Spacer(modifier = Modifier.width(24.dp))

                Column {
                    Text(stringResource(id = R.string.supported_formats_label), color = AppWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    FormatItem(stringResource(id = R.string.format_epub))
                    FormatItem(stringResource(id = R.string.format_pdf))
                    FormatItem(stringResource(id = R.string.format_mobi))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { launcher.launch(arrayOf("application/pdf", "application/epub+zip")) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9D9D9))
            ) {
                Text(
                    text = if (viewModel.selectedUri != null) stringResource(id = R.string.file_ready) else stringResource(id = R.string.choose_file_button),
                    color = Color.Black, fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BooktimeTextField(value = viewModel.title, onValueChange = { viewModel.title = it }, placeholder = stringResource(id = R.string.book_title_label))
            Spacer(modifier = Modifier.height(12.dp))
            BooktimeTextField(value = viewModel.author, onValueChange = { viewModel.author = it }, placeholder = stringResource(id = R.string.book_author_label))
            Spacer(modifier = Modifier.height(12.dp))
            BooktimeTextField(value = viewModel.genre, onValueChange = { viewModel.genre = it }, placeholder = stringResource(id = R.string.book_genre_label))

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.saveBook(userId, context) { onBackClick() } },
                modifier = Modifier.width(220.dp).height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(id = R.string.save_book_button), color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            viewModel.errorMessage?.let {
                Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

@Composable
private fun FormatItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Surface(modifier = Modifier.size(14.dp), shape = RoundedCornerShape(2.dp), color = Color(0xFF636D7A)) {}
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = AppWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
