package com.music.echo.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val resolvedBg = if (backgroundColor != Color.Unspecified) {
        backgroundColor
    } else if (isDark) {
        Color(0xFF111113).copy(alpha = 0.70f)
    } else {
        Color(0xFFF6F6F8).copy(alpha = 0.75f)
    }
    Box(
        modifier = modifier
            .clip(shape)
            .background(resolvedBg, shape),
        content = content
    )
}
