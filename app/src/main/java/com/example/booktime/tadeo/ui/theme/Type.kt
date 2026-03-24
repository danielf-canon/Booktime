package com.example.booktime.tadeo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.booktime.tadeo.R

val SuezOne = FontFamily(
    Font(R.font.suez_one_regular, FontWeight.Normal),
    Font(R.font.suez_one_regular, FontWeight.Bold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = SuezOne),
    displayMedium = TextStyle(fontFamily = SuezOne),
    displaySmall = TextStyle(fontFamily = SuezOne),
    headlineLarge = TextStyle(fontFamily = SuezOne, fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontFamily = SuezOne, fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall = TextStyle(fontFamily = SuezOne, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontFamily = SuezOne, fontSize = 22.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = SuezOne, fontSize = 16.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = SuezOne, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = SuezOne, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
    bodyMedium = TextStyle(fontFamily = SuezOne, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
    bodySmall = TextStyle(fontFamily = SuezOne, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelLarge = TextStyle(fontFamily = SuezOne, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
    labelMedium = TextStyle(fontFamily = SuezOne, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
    labelSmall = TextStyle(fontFamily = SuezOne, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp)
)