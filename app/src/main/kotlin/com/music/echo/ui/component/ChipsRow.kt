package iad1tya.echo.music.ui.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import iad1tya.echo.music.R
import iad1tya.echo.music.ui.screens.OptionStats

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

/**
 * Premium Apple-style Glassmorphic/Pink Chip Item
 */
@Composable
fun ChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = (colorScheme.background.red + colorScheme.background.green + colorScheme.background.blue) < 1.5f

    val backgroundColor = if (isSelected) {
        Color(0xFFFF2D55).copy(alpha = if (isDark) 0.35f else 0.25f)
    } else {
        if (isDark) {
            Color(0xFF1C1C1E).copy(alpha = 0.75f)
        } else {
            Color(0xFFFFFFFF).copy(alpha = 0.85f)
        }
    }

    val borderColor = if (isSelected) {
        Color(0xFFFF2D55).copy(alpha = 0.5f)
    } else {
        if (isDark) {
            Color(0xFFFFFFFF).copy(alpha = 0.16f)
        } else {
            Color(0xFF000000).copy(alpha = 0.12f)
        }
    }

    val textColor = if (isSelected) {
        if (isDark) Color(0xFFFF85A1) else Color(0xFFD81B60)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.06f),
                spotColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.14f)
            )
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .border(
                width = 0.5.dp,
                color = borderColor,
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = textColor
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = textColor
        )
    }
}

@Composable
fun <E> ChipsRow(
    chips: List<Pair<E, String>>,
    currentValue: E,
    onValueUpdate: (E) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    var prevSelected by remember { mutableStateOf<E?>(null) }
    val lastSelected = remember(currentValue) {
        val old = prevSelected
        if (currentValue != old && currentValue != null) {
            prevSelected = currentValue
        }
        old
    }

    val orderedChips = remember(chips, currentValue, lastSelected) {
        val currentItem = chips.firstOrNull { it.first == currentValue }
        val previousItem = if (lastSelected != null && lastSelected != currentValue) {
            chips.firstOrNull { it.first == lastSelected }
        } else null

        val remaining = chips.filter { it.first != currentValue && it.first != lastSelected }

        buildList {
            if (currentItem != null) add(currentItem)
            if (previousItem != null) add(previousItem)
            addAll(remaining)
        }
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(currentValue) {
        if (currentValue != null) {
            lazyListState.animateScrollToItem(0)
        }
    }

    LazyRow(
        state = lazyListState,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 12.dp) // Standard gap below the chips row
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = orderedChips,
            key = { it.second }
        ) { (value, label) ->
            val isSelected = currentValue == value
            ChipItem(
                label = label,
                isSelected = isSelected,
                onClick = { onValueUpdate(value) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun <Int> ChoiceChipsRow(
    chips: List<Pair<Int, String>>,
    options: List<Pair<OptionStats, String>>,
    selectedOption: OptionStats,
    onSelectionChange: (OptionStats) -> Unit,
    currentValue: Int,
    onValueUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    var expandIconDegree by remember { mutableFloatStateOf(0f) }
    val rotationAnimation by animateFloatAsState(
        targetValue = expandIconDegree,
        animationSpec = tween(durationMillis = 400),
        label = "rotation",
    )

    var prevSelected by remember { mutableStateOf<Int?>(null) }
    val lastSelected = remember(currentValue) {
        val old = prevSelected
        if (currentValue != old && currentValue != null) {
            prevSelected = currentValue
        }
        old
    }

    val orderedChips = remember(chips, currentValue, lastSelected) {
        val currentItem = chips.firstOrNull { it.first == currentValue }
        val previousItem = if (lastSelected != null && lastSelected != currentValue) {
            chips.firstOrNull { it.first == lastSelected }
        } else null

        val remaining = chips.filter { it.first != currentValue && it.first != lastSelected }

        buildList {
            if (currentItem != null) add(currentItem)
            if (previousItem != null) add(previousItem)
            addAll(remaining)
        }
    }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(currentValue) {
        if (currentValue != null) {
            lazyListState.animateScrollToItem(2) // Scroll to make the selected items fully visible next to the dropdown
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp) // Standard gap below the choice chips row
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item(key = "filter_dropdown") {
                Box(contentAlignment = Alignment.Center) {
                    val colorScheme = MaterialTheme.colorScheme
                    val isDark = (colorScheme.background.red + colorScheme.background.green + colorScheme.background.blue) < 1.5f

                    Row(
                        modifier = Modifier
                            .shadow(
                                elevation = 2.dp,
                                shape = CircleShape,
                                clip = false,
                                ambientColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.06f),
                                spotColor = if (isDark) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.14f)
                            )
                            .background(
                                color = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.75f) else Color(0xFFFFFFFF).copy(alpha = 0.85f),
                                shape = CircleShape
                            )
                            .border(
                                width = 0.5.dp,
                                color = if (isDark) Color(0xFFFFFFFF).copy(alpha = 0.16f) else Color(0xFF000000).copy(alpha = 0.12f),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .clickable {
                                menuExpanded = !menuExpanded
                                expandIconDegree -= 180
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = when (selectedOption) {
                                OptionStats.WEEKS -> stringResource(id = R.string.weeks)
                                OptionStats.MONTHS -> stringResource(id = R.string.months)
                                OptionStats.YEARS -> stringResource(id = R.string.years)
                                OptionStats.CONTINUOUS -> stringResource(id = R.string.continuous)
                            },
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(R.drawable.expand_more),
                            contentDescription = null,
                            modifier = Modifier
                                .graphicsLayer(rotationZ = rotationAnimation)
                                .size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                            expandIconDegree += 180
                        },
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option.second) },
                                onClick = {
                                    onSelectionChange(option.first)
                                    expandIconDegree += 180
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                }
            }

            item(key = "divider") {
                Box(
                    Modifier
                        .height(32.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    VerticalDivider()
                }
            }

            items(
                items = orderedChips,
                key = { it.second }
            ) { (value, label) ->
                val isSelected = currentValue == value
                ChipItem(
                    label = label,
                    isSelected = isSelected,
                    onClick = { onValueUpdate(value) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}
