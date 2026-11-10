package com.Hefputu5.Nugsis.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayMedium = TextStyle(
        fontSize = 42.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.0.sp
    ),
    displaySmall = TextStyle(
        fontSize = 32.sp,
        lineHeight = 38.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.2.sp
    ),
    headlineMedium = TextStyle(
        fontSize = 25.sp,
        lineHeight = 30.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp
    ),
    headlineSmall = TextStyle(
        fontSize = 21.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.4.sp
    ),
    titleMedium = TextStyle(
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.2.sp
    ),
    titleSmall = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 15.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal
    ),
    labelSmall = TextStyle(
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.6.sp
    )
)
