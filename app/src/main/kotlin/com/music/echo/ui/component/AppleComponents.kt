package com.music.echo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp

/**
 * Standardized Apple Spacing and Padding Values
 * Aligned with Flamingo Design Guide principles.
 */
object ApplePadding {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp      // Recommended for internal item vertical padding
    val md = 16.dp      // Standard screen margin
    val lg = 24.dp      // Section separation / Header-to-content spacing
    val xl = 32.dp      // Spacious segment spacing
    val xxl = 48.dp     // Bottom player clearance / Large hero margins
}

/**
 * Standardized Apple Border Radius
 */
object AppleRadius {
    val small = 8.dp
    val medium = 12.dp
    val large = 20.dp
    val extraLarge = 28.dp
}

/**
 * Apple Surface Colors - Context aware based on theme and depth.
 */
@Composable
fun appleSurfaceColor(depth: Int = 1): Color {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.background.luminance() < 0.5f
    return when {
        isDark && depth == 1 -> Color(0xFF111113).copy(alpha = 0.94f)
        isDark && depth == 2 -> Color(0xFF1C1C1E).copy(alpha = 0.82f)
        !isDark && depth == 1 -> Color(0xFFF6F6F8).copy(alpha = 0.92f)
        else -> Color(0xFFFFFFFF).copy(alpha = 0.88f)
    }
}

/**
 * Full Apple Glassmorphic Modifier
 * High-depth variant with soft shadows and dual-layer translucency.
 * Use for main cards, dialogs, and floating pills.
 */
fun Modifier.appleGlass(
    shape: Shape = RoundedCornerShape(AppleRadius.large),
    elevation: androidx.compose.ui.unit.Dp = 6.dp
): Modifier = this.composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = if (isDark) Color.Black.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.15f),
            spotColor = if (isDark) Color.Black.copy(alpha = 0.9f) else Color.Black.copy(alpha = 0.3f)
        )
        .background(
            color = appleSurfaceColor(depth = 1),
            shape = shape
        )
        .background(
            color = if (isDark) {
                Color.White.copy(alpha = 0.05f)
            } else {
                Color.White.copy(alpha = 0.25f)
            },
            shape = shape
        )
        .border(
            width = 0.5.dp,
            color = if (isDark) {
                Color.White.copy(alpha = 0.15f)
            } else {
                Color.Black.copy(alpha = 0.08f)
            },
            shape = shape
        )
        .clip(shape)
}

/**
 * Optimized Light Apple Glassmorphic Modifier
 * Shadow-less variant for performance-critical lists and dense grids.
 */
fun Modifier.appleGlassLight(
    shape: Shape = RoundedCornerShape(AppleRadius.medium)
): Modifier = this.composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Modifier
        .background(
            color = appleSurfaceColor(depth = 1).copy(alpha = if (isDark) 0.88f else 0.85f),
            shape = shape
        )
        .border(
            width = 0.5.dp,
            color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.06f),
            shape = shape
        )
        .clip(shape)
}

/**
 * Monochrome Blur Background
 * A soft, neutral full-screen background that simulates a smooth blur effect
 * using layered semi-transparent colors. Works in both dark and light mode.
 * Apply to the root container of a screen to replace static solid backgrounds.
 */
fun Modifier.monochromeBlurBackground(): Modifier = this.composed {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val baseColor = if (isDark) Color(0xFF0A0A0C) else Color(0xFFF5F5F5)
    val accentColor = if (isDark) Color(0xFF1A1A2E) else Color(0xFFE8E8F0)
    Modifier
        .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    baseColor.copy(alpha = 0.92f),
                    accentColor.copy(alpha = 0.08f),
                    baseColor.copy(alpha = 0.88f),
                )
            ),
            shape = RectangleShape
        )
}

/**
 * Apple Section Header Spacer
 * Implements the "No Dividers" principle by using whitespace for separation.
 */
@Composable
fun AppleSectionSpacer() {
    Spacer(modifier = Modifier.height(ApplePadding.lg))
}

/**
 * Apple List Item Spacer
 * Consistent vertical spacing between rows.
 */
@Composable
fun AppleItemSpacer() {
    Spacer(modifier = Modifier.height(ApplePadding.xs))
}

/**
 * Dynamic Background Gradient
 * Creates a subtle immersion based on a key color (extracted from artwork).
 */
@Composable
fun appleImmersionBrush(baseColor: Color): Brush {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return Brush.verticalGradient(
        colors = if (isDark) {
            listOf(baseColor.copy(alpha = 0.15f), Color.Transparent)
        } else {
            listOf(baseColor.copy(alpha = 0.1f), Color.Transparent)
        }
    )
}
