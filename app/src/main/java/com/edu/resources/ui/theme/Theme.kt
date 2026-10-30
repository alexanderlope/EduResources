package com.edu.resources.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AppColors {
    val Bg = Color(0xFF0F172A)
    val Surface = Color(0xFF1E293B)
    val Border = Color(0xFF334155)
    val Accent = Color(0xFF6EE7B7)
    val Text = Color(0xFFF8FAFC)
    val Muted = Color(0xFF94A3B8)
    val Dim = Color(0xFF64748B)
    val Dimmer = Color(0xFF475569)
    val Red = Color(0xFFF87171)
    val Pink = Color(0xFFF472B6)
    val Yellow = Color(0xFFFBBF24)
}

private val Scheme = darkColorScheme(
    primary = AppColors.Accent,
    onPrimary = AppColors.Bg,
    background = AppColors.Bg,
    onBackground = AppColors.Text,
    surface = AppColors.Surface,
    onSurface = AppColors.Text,
    error = AppColors.Red,
)

@Composable
fun EduTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, content = content)
}
