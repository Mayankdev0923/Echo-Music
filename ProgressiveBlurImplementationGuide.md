# Progressive Blur Navbar Implementation Guide (Android/Kotlin)

## Overview
This guide explains how to implement the iOS-style progressive blur navbar effect in Android using Jetpack Compose and Kotlin, just like the screenshot shows.

---

## Key Concepts

### 1. **Scroll Position Tracking**
Monitor the LazyColumn scroll state to know how far the user has scrolled:

```kotlin
val lazyListState = rememberLazyListState()
val scrollOffset = remember { 
    derivedStateOf { lazyListState.firstVisibleItemScrollOffset } 
}
val firstVisibleIndex = remember { 
    derivedStateOf { lazyListState.firstVisibleItemIndex } 
}
```

### 2. **Progress Calculation**
Convert scroll distance to a 0-1 progress value:

```kotlin
val blurProgress = remember {
    derivedStateOf {
        val totalScroll = (firstVisibleIndex.value * itemHeight) + scrollOffset.value
        min(totalScroll / maxScrollDistance, 1f)
    }
}
```

### 3. **Apply Blur Effect**
Use Compose's built-in `blur()` modifier to apply progressive blur:

```kotlin
Box(
    modifier = Modifier
        .blur(radius = blurProgress * 8.dp)  // Max 8.dp blur
        .alpha(1f - (blurProgress * 0.5f))   // Fade as you blur
)
```

---

## Implementation Steps

### Step 1: Setup Dependencies
Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation "androidx.compose.ui:ui:1.5.4"
    implementation "androidx.compose.material3:material3:1.1.0"
    implementation "androidx.compose.foundation:foundation:1.5.4"
}
```

### Step 2: Create Scroll Tracking Variables

```kotlin
@Composable
fun YourScreen() {
    val lazyListState = rememberLazyListState()
    
    val scrollOffset = remember { 
        derivedStateOf { lazyListState.firstVisibleItemScrollOffset } 
    }
    val firstVisibleIndex = remember { 
        derivedStateOf { lazyListState.firstVisibleItemIndex } 
    }
    
    val blurProgress = remember {
        derivedStateOf {
            val totalScroll = (firstVisibleIndex.value * 300) + scrollOffset.value
            min(totalScroll / 150f, 1f)  // Reaches max blur at 150dp
        }
    }
    
    LazyColumn(state = lazyListState) {
        item {
            ProgressiveBlurNavbar(blurProgress = blurProgress.value)
        }
        // ... rest of content
    }
}
```

### Step 3: Implement the Navbar Component

```kotlin
@Composable
fun ProgressiveBlurNavbar(blurProgress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        // Navbar title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White.copy(alpha = 1f - (blurProgress * 0.2f)))
        ) {
            Text(
                text = "Records",
                modifier = Modifier.alpha(1f - (blurProgress * 0.25f))
            )
        }
        
        // Blurred content area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .blur(radius = blurProgress * 8.dp)
                .alpha(1f - (blurProgress * 0.5f))
        ) {
            // Your navbar content (album info, playlist, etc.)
        }
    }
}
```

---

## Effect Breakdown

| Scroll Progress | Blur Radius | Alpha | Effect |
|---|---|---|---|
| 0% (No scroll) | 0.0 dp | 1.0 | Sharp, fully visible |
| 25% | 2.0 dp | 0.875 | Slightly blurred, mostly visible |
| 50% | 4.0 dp | 0.75 | Medium blur, semi-transparent |
| 75% | 6.0 dp | 0.625 | Heavy blur, fading |
| 100% | 8.0 dp | 0.5 | Maximum blur, half transparent |

---

## Customization Options

### Adjust Blur Intensity
```kotlin
val blurRadius = blurProgress * 12.dp  // Increase from 8.dp for stronger blur
```

### Change Blur Trigger Distance
```kotlin
min(totalScroll / 100f, 1f)  // Blur faster (shorter distance)
min(totalScroll / 200f, 1f)  // Blur slower (longer distance)
```

### Modify Fade Effect
```kotlin
.alpha(1f - (blurProgress * 0.7f))  // Fade more aggressively
.alpha(1f - (blurProgress * 0.3f))  // Fade less
```

---

## Advanced: Using RenderScript for Real Blur

For production apps, you might want actual blur rendering instead of visual blur:

```kotlin
fun applyBlur(bitmap: Bitmap, radius: Float, context: Context): Bitmap {
    val rs = RenderScript.create(context)
    val input = Allocation.createFromBitmap(rs, bitmap)
    val output = Allocation.createTyped(rs, input.type)
    
    ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)).apply {
        setRadius(radius)
        setInput(input)
        forEach(output)
    }
    
    output.copyTo(bitmap)
    rs.destroy()
    return bitmap
}
```

However, for most cases, the `blur()` modifier in Compose is sufficient and performs better.

---

## Performance Tips

1. **Use `derivedStateOf`** to avoid unnecessary recompositions
2. **Limit blur radius** (8-12.dp max) for better performance
3. **Use `LazyColumn`** for efficient rendering of large lists
4. **Test on actual devices**, not just emulator

---

## Common Issues & Solutions

### Issue: Blur not showing
**Solution:** Ensure you're using `Material3` theme and `Compose 1.5+`

### Issue: Performance lag during scroll
**Solution:** Reduce blur radius or use `graphicsLayer` instead of individual modifiers

### Issue: Navbar disappears too quickly
**Solution:** Increase the `maxScrollDistance` value in blur progress calculation

---

## Complete Working Example

See the provided `AdvancedProgressiveBlurNavbar.kt` file for a complete, production-ready implementation with:
- Proper state management
- Album cover cards
- Smooth animations
- Material3 design system integration

---

## iOS vs Android Differences

| Feature | iOS | Android/Kotlin |
|---|---|---|
| Blur API | `UIVisualEffectView` | `Modifier.blur()` |
| State Tracking | `UIScrollViewDelegate` | `LazyListState` |
| Performance | Hardware-accelerated | GPU-optimized (Compose) |
| Customization | Limited blur styles | Full control over blur radius & alpha |

---

## Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [LazyColumn State Management](https://developer.android.com/reference/kotlin/androidx/compose/foundation/lazy/LazyListState)
- [Compose Blur Modifier](https://developer.android.com/reference/kotlin/androidx/compose/ui/draw/package-summary#blur(androidx.compose.ui.Modifier,androidx.compose.ui.unit.Dp,androidx.compose.ui.unit.Dp,kotlin.Boolean))
