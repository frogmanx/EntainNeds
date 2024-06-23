package com.example.entainneds.ui.theme

import androidx.compose.ui.graphics.Color

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val secondaryText: Color,
)  {
    data object Night: ThemeColors(
        background = Color.Black,
        surface = Color.White,
        text = Color.White,
        secondaryText = Color.Black,
    )
    data object Day: ThemeColors(
        background = Color.White,
        surface = Color.Black,
        text = Color.Black,
        secondaryText = Color.White,
    )
}
