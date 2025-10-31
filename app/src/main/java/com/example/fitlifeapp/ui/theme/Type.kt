package com.example.fitlifeapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.fitlifeapp.R

// 💬 Fuente base (puedes cambiarla a Montserrat, Lato, Roboto, etc.)
val FitLifeFont = FontFamily.Default

val FitLifeTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FitLifeFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FitLifeFont,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FitLifeFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FitLifeFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FitLifeFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)
