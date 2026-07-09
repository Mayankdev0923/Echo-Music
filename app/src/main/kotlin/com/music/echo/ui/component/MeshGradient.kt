package com.music.echo.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import iad1tya.echo.music.constants.PureBlackKey
import iad1tya.echo.music.utils.rememberPreference
import kotlin.math.floor

private const val MESH_COLS = 8
private const val MESH_ROWS = 10
private const val TIME = 0.5f

@Composable
fun Modifier.meshGradientBackground(): Modifier {
    val pureBlack = rememberPreference(PureBlackKey, defaultValue = false).value
    if (pureBlack) return this

    val bg = MaterialTheme.colorScheme.background
    val isDark = (bg.red * 0.299f + bg.green * 0.587f + bg.blue * 0.114f) < 0.5f

    val colorA = if (isDark) Color(0xFF000000) else Color(0xFFFFFFFF)
    val colorB = if (isDark) Color(0xFF434343) else Color(0xFFFFE0E5)
    val colorC = if (isDark) null else Color(0xFFBFE9FF)

    val key = if (isDark) {
        (colorA.value.toLong() shl 32) or (colorB.value.toLong() and 0xFFFFFFFFL)
    } else {
        ((colorA.value.toLong() shl 32) or (colorB.value.toLong() and 0xFFFFFFFFL)) xor 0x1
    }

    val newBitmap = remember(key) {
        buildMeshGradient(MESH_COLS, MESH_ROWS, colorA, colorB, colorC)
    }

    val previousBitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    val crossfadeProgress = remember { Animatable(1f) }

    LaunchedEffect(newBitmap) {
        if (previousBitmap.value != null && previousBitmap.value !== newBitmap) {
            crossfadeProgress.snapTo(0f)
            crossfadeProgress.animateTo(1f, animationSpec = tween(400))
        }
        previousBitmap.value = newBitmap
    }

    return this.drawWithCache {
        onDrawWithContent {
            val prev = previousBitmap.value
            val progress = crossfadeProgress.value
            if (prev != null && prev !== newBitmap && progress < 1f) {
                drawImage(
                    image = prev,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                    alpha = 1f - progress,
                )
                drawImage(
                    image = newBitmap,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                    alpha = progress,
                )
            } else {
                drawImage(
                    image = newBitmap,
                    dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                )
            }
            drawContent()
        }
    }
}

private fun buildMeshGradient(cols: Int, rows: Int, colorA: Color, colorB: Color, colorC: Color?): ImageBitmap {
    val perm = (0 until 256).shuffled().let { p ->
        IntArray(512) { i -> p[i % 256] }
    }
    val t = TIME

    val argb = IntArray(cols * rows) { idx ->
        val col = idx % cols
        val row = idx / cols
        val nx = col.toFloat() / (cols - 1)
        val ny = row.toFloat() / (rows - 1)

        val noise = noise3d(nx * 0.7f, ny * 0.9f + 1.5f, t, perm)
        val blend = ((noise + 1f) * 0.5f).coerceIn(0f, 1f)

        val grain = noise3d(nx * 40f, ny * 60f, t * 2f + 7f, perm)
        val grainAmount = 0.035f

        val (r, g, b) = if (colorC != null) {
            val t2 = blend * 2f
            val ca: Color
            val cb: Color
            val ct: Float
            if (t2 < 1f) {
                ca = colorA; cb = colorB; ct = t2
            } else {
                ca = colorB; cb = colorC; ct = t2 - 1f
            }
            tripleOf(
                (ca.red + (cb.red - ca.red) * ct + grain * grainAmount).coerceIn(0f, 1f),
                (ca.green + (cb.green - ca.green) * ct + grain * grainAmount).coerceIn(0f, 1f),
                (ca.blue + (cb.blue - ca.blue) * ct + grain * grainAmount).coerceIn(0f, 1f),
            )
        } else {
            tripleOf(
                (colorA.red + (colorB.red - colorA.red) * blend + grain * grainAmount).coerceIn(0f, 1f),
                (colorA.green + (colorB.green - colorA.green) * blend + grain * grainAmount).coerceIn(0f, 1f),
                (colorA.blue + (colorB.blue - colorA.blue) * blend + grain * grainAmount).coerceIn(0f, 1f),
            )
        }

        Color(red = r, green = g, blue = b, alpha = 1f).toArgb()
    }

    val androidBitmap = android.graphics.Bitmap.createBitmap(argb, cols, rows, android.graphics.Bitmap.Config.ARGB_8888)
    return androidBitmap.asImageBitmap()
}

private fun tripleOf(r: Float, g: Float, b: Float) = Triple(r, g, b)

private val grad = arrayOf(
    floatArrayOf(1f, 1f), floatArrayOf(-1f, 1f),
    floatArrayOf(1f, -1f), floatArrayOf(-1f, -1f),
    floatArrayOf(1f, 0f), floatArrayOf(-1f, 0f),
    floatArrayOf(0f, 1f), floatArrayOf(0f, -1f),
)

private fun fade(t: Float) = t * t * t * (t * (t * 6f - 15f) + 10f)
private fun lerp(a: Float, b: Float, t: Float) = a + t * (b - a)

private fun noise3d(xIn: Float, yIn: Float, zIn: Float, perm: IntArray): Float {
    val xi = floor(xIn).toInt() and 255
    val yi = floor(yIn).toInt() and 255
    val zi = floor(zIn).toInt() and 255
    val xf = xIn - floor(xIn)
    val yf = yIn - floor(yIn)
    val zf = zIn - floor(zIn)
    val u = fade(xf)
    val v = fade(yf)
    val w = fade(zf)
    val aaa = perm[perm[perm[xi] + yi] + zi]
    val aba = perm[perm[perm[xi] + yi + 1] + zi]
    val aab = perm[perm[perm[xi] + yi] + zi + 1]
    val abb = perm[perm[perm[xi] + yi + 1] + zi + 1]
    val baa = perm[perm[perm[xi + 1] + yi] + zi]
    val bba = perm[perm[perm[xi + 1] + yi + 1] + zi]
    val bab = perm[perm[perm[xi + 1] + yi] + zi + 1]
    val bbb = perm[perm[perm[xi + 1] + yi + 1] + zi + 1]
    return lerp(
        lerp(
            lerp(grad3(aaa, xf, yf, zf), grad3(baa, xf - 1, yf, zf), u),
            lerp(grad3(aba, xf, yf - 1, zf), grad3(bba, xf - 1, yf - 1, zf), u), v,
        ),
        lerp(
            lerp(grad3(aab, xf, yf, zf - 1), grad3(bab, xf - 1, yf, zf - 1), u),
            lerp(grad3(abb, xf, yf - 1, zf - 1), grad3(bbb, xf - 1, yf - 1, zf - 1), u), v,
        ), w,
    )
}

private fun grad3(hash: Int, x: Float, y: Float, z: Float): Float {
    val g = grad[hash and 7]
    return g[0] * (x + z * 0.1f) + g[1] * y
}
