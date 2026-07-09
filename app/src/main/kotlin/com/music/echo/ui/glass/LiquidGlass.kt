package iad1tya.echo.music.ui.glass

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.liquidGlass(
    backdrop: Color = Color.Transparent,
    shape: Shape = RoundedCornerShape(12.dp),
    blurRadius: Dp = 24.dp,
    lensInner: Dp = 4.dp,
    lensOuter: Dp = 12.dp,
    surfaceColor: Color = Color(0xFFF2F2F7),
    heavyVisuals: Boolean = true
): Modifier = this.drawBehind {
    drawRoundRect(
        color = surfaceColor,
        cornerRadius = CornerRadius(lensOuter.toPx())
    )
}
