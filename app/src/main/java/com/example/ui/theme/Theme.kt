package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IslamicDarkColorScheme = darkColorScheme(
  primary = IslamicGold,
  onPrimary = MidnightBlue,
  primaryContainer = NavyCardElevated,
  onPrimaryContainer = BrightGold,
  secondary = BrightGold,
  onSecondary = MidnightBlue,
  secondaryContainer = LightGoldTint,
  onSecondaryContainer = BrightGold,
  tertiary = EmeraldSuccess,
  onTertiary = Color.White,
  tertiaryContainer = EmeraldContainer,
  onTertiaryContainer = EmeraldSuccess,
  background = MidnightBlue,
  onBackground = TextWhite,
  surface = DeepNavy,
  onSurface = TextWhite,
  surfaceVariant = NavyCard,
  onSurfaceVariant = TextLight,
  outline = GoldBorder,
  outlineVariant = Color(0x20D4AF37),
  inverseSurface = TextLight,
  inverseOnSurface = MidnightBlue,
  error = Color(0xFFEF4444),
  onError = Color.White
)

private val IslamicLightColorScheme = IslamicDarkColorScheme

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  // We use the signature Dark Blue, Gold and White theme for Al-Hujur AI
  MaterialTheme(
    colorScheme = IslamicDarkColorScheme,
    typography = Typography,
    content = content
  )
}

