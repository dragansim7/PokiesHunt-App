package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = Amber400,
  onPrimary = Amber950,
  primaryContainer = Amber950,
  onPrimaryContainer = Amber300,
  secondary = Amber300,
  onSecondary = Amber950,
  tertiary = Emerald400,
  background = DarkBackground,
  onBackground = ForegroundWhite,
  surface = DarkSurface,
  onSurface = ForegroundWhite,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = MutedForeground,
  outline = DarkCardBorder,
  outlineVariant = DarkCardBorderHover
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
