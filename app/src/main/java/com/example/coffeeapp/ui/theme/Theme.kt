package com.example.coffeeapp.ui.theme

import android.app.Activity
import androidx.compose.runtime.mutableStateOf
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat


val currentAppTheme = mutableStateOf("coffee")

// Тёмная палитра
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    background = Color(0xFF121212), // Тёмный фон
    surface = Color(0xFF1E1E1E)
)

// Светлая палитра
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3E2723),
    background = Color(0xFFF5F5F5), // Светло-серый фон
    surface = Color(0xFFFFFFFF)
)

// Наша кастомная КОФЕЙНАЯ палитра!
private val CoffeeColorScheme = lightColorScheme(
    primary = Color(0xFF3E2723),     // Темно-шоколадный для текста и главных кнопок
    background = Color(0xFFE7CC89),  // Твой любимый песочно-кофейный фон
    surface = Color(0xFFFFF8E1)     // Светло-кремовый для карточек
)

@Composable
fun CoffeeAppTheme(
    content: @Composable () -> Unit
) {
    // Выбираем схему в зависимости от текущего состояния
    val colorScheme = when (currentAppTheme.value) {
        "light" -> LightColorScheme
        "dark" -> DarkColorScheme
        else -> CoffeeColorScheme // "coffee"
    }

    // Твой код окрашивания системной полоски (SideEffect)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Если тема тёмная, красим статус-бар в тёмный, иначе в кофейный/светлый
            if (currentAppTheme.value == "dark") {
                window.statusBarColor = Color(0xFF121212).toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Белые иконки
            } else if (currentAppTheme.value == "light") {
                window.statusBarColor = Color(0xFFF5F5F5).toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true  // Тёмные иконки
            } else {
                window.statusBarColor = Color(0xFFE7CC89).toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true  // Тёмные иконки
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}