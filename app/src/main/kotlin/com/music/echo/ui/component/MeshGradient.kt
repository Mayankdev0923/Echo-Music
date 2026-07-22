package com.music.echo.ui.component

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import iad1tya.echo.music.constants.MeshTheme
import io.github.om252345.composemeshgradient.MeshGradient

@Composable
fun AnimatedMeshBackground(
    meshTheme: MeshTheme, 
    isDarkTheme: Boolean, 
    pureBlack: Boolean, 
    modifier: Modifier = Modifier
) {
    if (pureBlack) {
        Box(modifier = modifier.background(Color.Black))
        return
    }

    val baseColor = if (isDarkTheme) Color(0xFF141414) else Color(0xFFDDDDDD)

    val noiseBitmap = remember {
        val width = 128
        val height = 128
        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            val v = (Math.random() * 255).toInt()
            pixels[i] = android.graphics.Color.argb(30, v, v, v) // Low alpha noise
        }
        android.graphics.Bitmap.createBitmap(pixels, width, height, android.graphics.Bitmap.Config.ARGB_8888)
            .asImageBitmap()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mesh-transition")
    
    // Animate some offsets to make the mesh fluid
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )
    val offset2 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )
    
    val subtleGray = if (isDarkTheme) Color(0xFF333333) else Color(0xFFCCCCCC)
    val colorPair = when (meshTheme) {
        MeshTheme.KNIGHT -> Pair(baseColor, if (isDarkTheme) Color(0xFF4B2E83) else Color(0xFFB39DDB))
        MeshTheme.COFFEE -> Pair(baseColor, if (isDarkTheme) Color(0xFF8C7355) else Color(0xFFD7CCC8))
        MeshTheme.BLUSH -> Pair(baseColor, if (isDarkTheme) Color(0xFFAD5E71) else Color(0xFFF8BBD0))
        MeshTheme.SUNFLOWER -> Pair(baseColor, if (isDarkTheme) Color(0xFFA68A36) else Color(0xFFFFF59D))
        MeshTheme.MINT -> Pair(baseColor, if (isDarkTheme) Color(0xFF4C8C4C) else Color(0xFFA5D6A7))
        MeshTheme.HAKI -> Pair(baseColor, if (isDarkTheme) Color(0xFF732626) else Color(0xFFEF9A9A))
        MeshTheme.NONE -> Pair(baseColor, subtleGray)
    }

    val animatedColor1 by infiniteTransition.animateColor(
        initialValue = colorPair.first,
        targetValue = colorPair.second,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color1"
    )

    val animatedColor2 by infiniteTransition.animateColor(
        initialValue = colorPair.second,
        targetValue = colorPair.first,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color2"
    )

    val points = arrayOf(
        Offset(0f, 0f), Offset(0.5f, 0f), Offset(1f, 0f),
        Offset(0f, offset1), Offset(offset2, 0.5f), Offset(1f, offset1),
        Offset(0f, 1f), Offset(0.5f, 1f), Offset(1f, 1f)
    )

    val colors = arrayOf(
        baseColor, baseColor, baseColor,
        baseColor, animatedColor1, baseColor,
        baseColor, baseColor, animatedColor2
    )



    Box(modifier = modifier) {
        MeshGradient(
            width = 3,
            height = 3,
            points = points,
            colors = colors,
            modifier = Modifier.matchParentSize()
        )
        androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
            val paint = androidx.compose.ui.graphics.Paint().apply {
                blendMode = androidx.compose.ui.graphics.BlendMode.Overlay
                shader = androidx.compose.ui.graphics.ImageShader(
                    noiseBitmap,
                    androidx.compose.ui.graphics.TileMode.Repeated,
                    androidx.compose.ui.graphics.TileMode.Repeated
                )
            }
            drawContext.canvas.drawRect(0f, 0f, size.width, size.height, paint)
        }
    }
}
