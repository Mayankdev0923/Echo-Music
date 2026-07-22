
package iad1tya.echo.music.constants

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.music.echo.ui.component.AppleRadius

const val CONTENT_TYPE_HEADER = 0
const val CONTENT_TYPE_LIST = 1
const val CONTENT_TYPE_SONG = 2
const val CONTENT_TYPE_ARTIST = 3
const val CONTENT_TYPE_ALBUM = 4
const val CONTENT_TYPE_PLAYLIST = 5

val FloatingToolbarHeight = 72.dp
val FloatingToolbarHorizontalPadding = 12.dp
val FloatingToolbarBottomPadding = 16.dp
val NavigationBarHeight = FloatingToolbarHeight
val SlimNavBarHeight = 64.dp
val MiniPlayerHeight = 64.dp // 75% of original 68dp
val MinMiniPlayerHeight = 16.dp
val MiniPlayerBottomSpacing = 8.dp 
val QueuePeekHeight = 64.dp
val AppBarHeight = 64.dp

val ListItemHeight = 66.dp // Aligned with Flamingo's tighter but spacious list feel
val SuggestionItemHeight = 56.dp
val SearchFilterHeight = 48.dp
val ListThumbnailSize = 52.dp // Slightly larger for better detail
val SmallGridThumbnailHeight = 112.dp
val GridThumbnailHeight = 144.dp // Standard large grid item
val AlbumThumbnailSize = 160.dp

val ThumbnailCornerRadius = AppleRadius.medium // Standardized to 12.dp per guide

val PlayerHorizontalPadding = 32.dp

val NavigationBarAnimationSpec = spring<Dp>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessLow
)

val BottomSheetAnimationSpec = spring<Dp>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow
)

val BottomSheetSoftAnimationSpec = spring<Dp>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessLow
)
