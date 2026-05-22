package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EditorialSecondary,
    onPrimary = EditorialOnPrimaryContainer,
    primaryContainer = EditorialPrimary,
    onPrimaryContainer = EditorialOnPrimary,
    secondary = EditorialSecondary,
    onSecondary = EditorialOnPrimary,
    background = EditorialOnBackground,
    surface = EditorialOnBackground,
    surfaceVariant = EditorialSecondary,
    onBackground = EditorialBackground,
    onSurface = EditorialBackground,
    onSurfaceVariant = EditorialPrimaryContainer
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EditorialPrimary,
    onPrimary = EditorialOnPrimary,
    primaryContainer = EditorialPrimaryContainer,
    onPrimaryContainer = EditorialOnPrimaryContainer,
    secondary = EditorialSecondary,
    onSecondary = EditorialOnPrimary,
    secondaryContainer = EditorialSecondaryContainer,
    onSecondaryContainer = EditorialOnSecondaryContainer,
    tertiary = EditorialTertiary,
    onTertiary = EditorialOnPrimary,
    tertiaryContainer = EditorialTertiaryContainer,
    onTertiaryContainer = EditorialOnTertiaryContainer,
    background = EditorialBackground,
    surface = EditorialSurface,
    surfaceVariant = EditorialSurfaceVariant,
    onBackground = EditorialOnBackground,
    onSurface = EditorialOnSurface,
    onSurfaceVariant = EditorialOnSurfaceVariant,
    outline = EditorialBorder,
    outlineVariant = EditorialBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
