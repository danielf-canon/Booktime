package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.BooktimeBottomNav
import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.navigation.Screen
import com.example.booktime.tadeo.ui.theme.AppWhite

@Composable
fun NewBookScreen(
    navController: NavController,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onImportEbookClick: () -> Unit,
    onImportAudiobookClick: () -> Unit
) {

    var selectedItem by remember { mutableIntStateOf(2) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
        ) {

            ScreenWrapper(onBackClick = onBackClick) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.new_book_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = AppWhite,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.new_book_phrase),
                    style = MaterialTheme.typography.bodyLarge,
                    color = AppWhite.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                BooktimeButton(
                    text = stringResource(id = R.string.new_book_button_online),
                    onClick = onSearchClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                BooktimeButton(
                    text = stringResource(id = R.string.new_book_button_EBOOK),
                    onClick = onImportEbookClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                BooktimeButton(
                    text = stringResource(id = R.string.new_book_button_AUDIOLIBRO),
                    onClick = onImportAudiobookClick
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