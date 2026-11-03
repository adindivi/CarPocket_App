package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SafeCoachLightColorScheme = lightColorScheme(
  primary = CyanNeon,
  onPrimary = TextOnAccent,
  primaryContainer = CyanGlow,
  onPrimaryContainer = CyanNeon,
  secondary = EmeraldSafe,
  onSecondary = Color.White,
  secondaryContainer = EmeraldGlow,
  onSecondaryContainer = EmeraldSafe,
  tertiary = AmberWarning,
  onTertiary = Color.White,
  tertiaryContainer = AmberGlow,
  onTertiaryContainer = AmberWarning,
  background = LightAppBg,
  onBackground = TextPrimary,
  surface = LightSurface,
  onSurface = TextPrimary,
  surfaceVariant = LightSurfaceSubtle,
  onSurfaceVariant = TextSecondary,
  outline = LightCardBorder,
  error = CrimsonAlert,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = SafeCoachLightColorScheme,
    typography = Typography,
    content = content
  )
}
