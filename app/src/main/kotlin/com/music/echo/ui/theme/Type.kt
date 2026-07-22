package com.music.echo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import iad1tya.echo.music.R

// Custom premium Helvetica font family
val HelveticaFontFamily = FontFamily(
    Font(R.font.helvetica, FontWeight.Normal),
    Font(R.font.helvetica, FontWeight.Medium),
    Font(R.font.helvetica, FontWeight.SemiBold),
    Font(R.font.helvetica, FontWeight.Bold),
    Font(R.font.helvetica, FontWeight.ExtraBold)
)

private val defaultPlatformTextStyle = PlatformTextStyle(
    includeFontPadding = false
)
private val defaultLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)

// Helper for high quality text rendering
private fun highQualityTextStyle(
    fontWeight: FontWeight,
    fontSize: Int,
    lineHeight: Int,
    letterSpacing: Double
) = TextStyle(
    fontFamily = HelveticaFontFamily,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
    platformStyle = defaultPlatformTextStyle,
    lineHeightStyle = defaultLineHeightStyle,
    fontFeatureSettings = "kern 1, liga 1"
)

// Flamingo/Apple SF Pro-like typography (softer, premium feel with bold emphasis)
val AppTypography = Typography(
    displayLarge = highQualityTextStyle(FontWeight.ExtraBold, 57, 64, -0.03),
    displayMedium = highQualityTextStyle(FontWeight.Bold, 45, 52, -0.02),
    displaySmall = highQualityTextStyle(FontWeight.Bold, 36, 44, -0.02),
    headlineLarge = highQualityTextStyle(FontWeight.Bold, 32, 40, 0.0),
    headlineMedium = highQualityTextStyle(FontWeight.SemiBold, 28, 36, 0.0),
    headlineSmall = highQualityTextStyle(FontWeight.SemiBold, 24, 32, 0.0),
    titleLarge = highQualityTextStyle(FontWeight.Bold, 22, 28, 0.0),
    titleMedium = highQualityTextStyle(FontWeight.SemiBold, 17, 24, 0.1),
    titleSmall = highQualityTextStyle(FontWeight.Medium, 15, 20, 0.1),
    bodyLarge = highQualityTextStyle(FontWeight.Normal, 16, 24, 0.25),
    bodyMedium = highQualityTextStyle(FontWeight.Normal, 14, 20, 0.25),
    bodySmall = highQualityTextStyle(FontWeight.Normal, 12, 16, 0.4),
    labelLarge = highQualityTextStyle(FontWeight.Bold, 11, 16, 0.12),
    labelMedium = highQualityTextStyle(FontWeight.Medium, 12, 16, 0.5),
    labelSmall = highQualityTextStyle(FontWeight.Medium, 11, 16, 0.5)
)
