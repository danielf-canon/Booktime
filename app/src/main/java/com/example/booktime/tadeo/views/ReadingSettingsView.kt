package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.ui.theme.ButtonGreen
import com.example.booktime.tadeo.ui.theme.OtherMenuBackground

import com.example.booktime.tadeo.components.BooktimeButton
import com.example.booktime.tadeo.components.ScreenWrapper
import com.example.booktime.tadeo.components.SettingsSectionTitle

import androidx.compose.ui.res.stringResource
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.ui.theme.BooktimeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingSettingsView(onBackClick: () -> Unit = {}) {
    var fontSize by remember { mutableFloatStateOf(16f) }

    ScreenWrapper(onBackClick = onBackClick) {
        // Previsualización de texto
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = OtherMenuBackground)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(id = R.string.preview_text),
                    fontSize = fontSize.sp,
                    color = Color.White,
                    lineHeight = (fontSize * 1.5).sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tamaño de fuente
        SettingsSectionTitle(stringResource(id = R.string.font_size_label, fontSize.toInt()))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.TextFields, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Slider(
                value = fontSize,
                onValueChange = { fontSize = it },
                valueRange = 12f..32f,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                colors = SliderDefaults.colors(
                    thumbColor = ButtonGreen,
                    activeTrackColor = ButtonGreen,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                )
            )
            Icon(Icons.Default.FormatSize, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        BooktimeButton(
            text = stringResource(id = R.string.apply_changes),
            onClick = { /* Aplicar */ }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReadingSettingsViewPreview() {
    BooktimeTheme(darkTheme = true, dynamicColor = false) {
        ReadingSettingsView()
    }
}
