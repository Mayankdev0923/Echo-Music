package com.music.echo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp

/**
 * Apple Card Colors - Use with CardDefaults.cardColors()
 */
@Composable
fun appleCardColors(
    alpha: Float = 0.82f
): androidx.compose.material3.CardColors {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (colorScheme.background.red + colorScheme.background.green + colorScheme.background.blue) < 1.5f
    return CardDefaults.cardColors(
        containerColor = if (isDark) {
            Color(0xFF1C1C1E).copy(alpha = 0.75f)
        } else {
            Color(0xFFFFFFFF).copy(alpha = alpha)
        }
    )
}

/**
 * Apple Spacing and Padding Values
 */
object ApplePadding {
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 24.dp      // Decongested section margins
    val xl = 32.dp      // Spacious segment spacing
    val xxl = 48.dp
}

/**
 * Apple Border Radius
 */
object AppleRadius {
    val small = 12.dp
    val medium = 16.dp
    val large = 20.dp
    val extraLarge = 24.dp
}

/**
 * Apple Gradient Overlay for Images
 */
@Composable
fun appleGradientOverlay(
    isLight: Boolean = false
): Brush {
    return if (isLight) {
        Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.1f),
                Color.Black.copy(alpha = 0.4f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.2f),
                Color.Black.copy(alpha = 0.6f)
            )
        )
    }
}

/**
 * Apple Surface Colors
 */
@Composable
fun appleSurfaceColor(depth: Int = 1): Color {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (colorScheme.background.red + colorScheme.background.green + colorScheme.background.blue) < 1.5f
    return when {
        isDark && depth == 1 -> Color(0xFF1C1C1E).copy(alpha = 0.75f)
        isDark && depth == 2 -> Color(0xFF2C2C2E).copy(alpha = 0.55f)
        !isDark && depth == 1 -> Color(0xFFFFFFFF).copy(alpha = 0.85f)
        else -> Color(0xFFF2F2F7).copy(alpha = 0.65f)
    }
}

/**
 * Apple Glassmorphic Modifier
 * Applies a consistent translucent background, a thin glass-edge border, and custom shadows for depth.
 */
fun Modifier.appleGlass(
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: androidx.compose.ui.unit.Dp = 6.dp
): Modifier = this.composed {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (colorScheme.background.red + colorScheme.background.green + colorScheme.background.blue) < 1.5f
    Modifier
        .shadow(
            elevation = elevation,
            shape = shape,
            clip = false,
            ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.12f),
            spotColor = if (isDark) Color.Black.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.24f)
        )
        .background(
            color = if (isDark) {
                Color(0xFF111113).copy(alpha = 0.96f)
            } else {
                Color(0xFFF6F6F8).copy(alpha = 0.9f)
            },
            shape = shape
        )
        .background(
            color = if (isDark) {
                Color.White.copy(alpha = 0.06f)
            } else {
                Color.White.copy(alpha = 0.36f)
            },
            shape = shape
        )
        .border(
            width = 1.dp,
            color = if (isDark) {
                Color(0xFFFFFFFF).copy(alpha = 0.28f)
            } else {
                Color(0xFF000000).copy(alpha = 0.1f)
            },
            shape = shape
        )
        .clip(shape)
}

/**
 * Apple Glassmorphic Modifier for List Rows
 */
fun Modifier.appleGlassRow(
    shape: Shape = RectangleShape,
    isActive: Boolean = false,
    isSelected: Boolean = false,
    drawHighlight: Boolean = true
): Modifier = this.composed {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (colorScheme.background.red + colorScheme.background.green + colorScheme.background.blue) < 1.5f
    val bg = when {
        isActive -> colorScheme.secondaryContainer
        isSelected && drawHighlight -> colorScheme.primary.copy(alpha = 0.35f)
        else -> if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.75f) else Color(0xFFFFFFFF).copy(alpha = 0.82f)
    }
    Modifier
        .background(color = bg, shape = shape)
        .border(
            width = 0.5.dp,
            color = if (isDark) Color(0xFFFFFFFF).copy(alpha = 0.12f) else Color(0xFF000000).copy(alpha = 0.08f),
            shape = shape
        )
}
