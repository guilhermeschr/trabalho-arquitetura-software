package br.edu.arquitetura.newsdemo.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF176B4B), onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F0DF), onPrimaryContainer = Color(0xFF0A3021),
    secondaryContainer = Color(0xFFE0EFE5), onSecondaryContainer = Color(0xFF174C34),
    background = Color(0xFFF8FAF8), surface = Color(0xFFF8FAF8),
    surfaceVariant = Color(0xFFECF1ED), onSurface = Color(0xFF16251D),
)
private val Dark = darkColorScheme(primary = Color(0xFF91D5AC), background = Color(0xFF111B15), surface = Color(0xFF111B15))

@Composable
fun NewsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
