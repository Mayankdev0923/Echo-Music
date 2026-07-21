package iad1tya.echo.music.ui.theme

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score
import com.music.echo.ui.theme.AppTypography

val DefaultThemeColor = Color(0xFFED5564)

// Apple Glass Effect Colors
val AppleGlassLight = Color(0xFFFFFFFF).copy(alpha = 0.15f)
val AppleGlassMedium = Color(0xFFFFFFFF).copy(alpha = 0.10f)
val AppleGlassDark = Color(0xFF000000).copy(alpha = 0.25f)
val AppleBlurBase = Color(0xFF1C1C1E)

val AppleLightColorScheme = lightColorScheme(
    primary = Color(0xFFFA243C), // Apple Music Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD1D6),
    onPrimaryContainer = Color(0xFF3F0008),
    secondary = Color(0xFF8E8E93), // Neutral Apple Gray instead of blue link color
    onSecondary = Color.White,
    background = Color(0xFFF2F2F7), // Apple Grouped Background
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF3C3C43),
    outline = Color(0xFFC7C7CC),
    outlineVariant = Color(0xFFE5E5EA)
)

val AppleDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFA243C), // Apple Music Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A000D),
    onPrimaryContainer = Color(0xFFFFD1D6),
    secondary = Color(0xFF8E8E93), // Neutral Apple Gray instead of blue link color
    onSecondary = Color.White,
    background = Color(0xFF1C1C1E), // Apple Dark Component Background (instead of pitch black)
    onBackground = Color.White,
    surface = Color(0xFF1C1C1E), // Apple Dark Gray Surface
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFE5E5EA),
    outline = Color(0xFF38383A),
    outlineVariant = Color(0xFF2C2C2E)
)

@Composable
fun echomusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    appleGlassMode: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    val colorScheme = if (dynamicColor) {
        val useSystemDynamicColor = (themeColor == DefaultThemeColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)

        val baseColorScheme = if (useSystemDynamicColor) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            rememberDynamicColorScheme(
                seedColor = themeColor,
                isDark = darkTheme,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
                style = PaletteStyle.TonalSpot
            )
        }

        var scheme = if (darkTheme && pureBlack) {
            baseColorScheme.pureBlack(true)
        } else {
            baseColorScheme
        }

        if (appleGlassMode && darkTheme) {
            scheme = scheme.applyAppleGlass()
        }
        scheme
    } else {
        if (darkTheme) {
            val scheme = if (pureBlack) AppleDarkColorScheme.copy(background = Color.Black, surface = Color.Black)
            else AppleDarkColorScheme
            scheme.copy(primary = themeColor)
        } else {
            AppleLightColorScheme.copy(primary = themeColor)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

fun ColorScheme.applyAppleGlass(): ColorScheme = copy(
    surface = AppleBlurBase,
    background = Color(0xFF1C1C1E),
    surfaceVariant = AppleGlassMedium,
    surfaceDim = AppleGlassDark,
    outlineVariant = AppleGlassLight
)

fun Bitmap.extractThemeColor(): Color {
    val colorsToPopulation = Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .swatches
        .associate { it.rgb to it.population }
    val rankedColors = Score.score(colorsToPopulation)
    return Color(rankedColors.first())
}

fun Bitmap.extractGradientColors(): List<Color> {
    val extractedColors = Palette.from(this)
        .maximumColorCount(64)
        .generate()
        .swatches
        .associate { it.rgb to it.population }

    val orderedColors = Score.score(extractedColors, 2, 0xff4285f4.toInt(), true)
        .sortedByDescending { Color(it).luminance() }

    return if (orderedColors.size >= 2)
        listOf(Color(orderedColors[0]), Color(orderedColors[1]))
    else
        listOf(Color(0xFF595959), Color(0xFF0D0D0D))
}

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}