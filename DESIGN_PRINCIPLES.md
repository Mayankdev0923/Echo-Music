# Akai Design Principles (FlamingoSank Optimized)

This document outlines the design principles, standardized spacing, typography, and UI components for the Akai project, inspired by the Flamingo Design Guide and Apple Music.

## 1. Core Principles
- **Motion as First-Class Citizen**: Transitions should feel physics-based. Shared elements (artwork) and layout animations (mini-player expansion) are preferred over traditional screen-replace transitions.
- **Physics-Correct Overscroll**: Lists use a custom spring simulation with variable resistance, mimicking the rubber-band feel of iOS.
- **Contextual Visuals**: Backgrounds adapt to content (e.g., dynamic color extraction from album art).
- **Glassmorphism**: Consistent use of frosted glass effects for overlays, headers, and floating components.

## 2. Standardized Spacing & Padding (`ApplePadding`)
Consistent spacing ensures a breathable and organized layout.
- **XXS**: 4.dp (Tight groupings)
- **XS**: 8.dp (Internal component padding)
- **SM**: 12.dp (List item vertical padding - Guide recommends ~14dp, we use 12-16dp range)
- **MD**: 16.dp (Standard screen margin / horizontal padding)
- **LG**: 24.dp (Section separation)
- **XL**: 32.dp (Major segment spacing)
- **XXL**: 48.dp (Bottom padding for mini-player clearance)

## 3. Corner Radius (`AppleRadius`)
- **Small**: 8.dp (Buttons, small cards)
- **Medium**: 12.dp (Standard thumbnails, list items)
- **Large**: 20.dp (Main cards, player view containers)
- **ExtraLarge**: 28.dp (Immersive headers, large floating components)
- **Circle**: CircleShape (Avatar, specific action buttons)

## 4. Typography
- **Headlines**: ExtraBold weight, large font sizes (32sp+), tight letter spacing (-0.03em).
- **Titles**: Bold/Medium weight, 17-20sp.
- **Body**: 15sp standard, 13sp for secondary metadata.
- **No Dividers**: Use breathing room (padding) rather than horizontal lines to separate list items.

## 5. Glass Effects (`appleGlass`)
Akai uses a multi-layered glass effect to achieve depth while maintaining performance.
- **Standard Glass**: High depth, includes shadow, glass-edge border, and dual-layer translucency.
- **Light Glass**: Optimized for lists and high-density screens. Skips shadows to reduce draw calls.
- **Frosted Glass**: API 31+ native blur effect for top-tier immersion.

## 6. Optimization Checklist
- [ ] Use `key = { item.id }` in all `LazyColumn` and `LazyVerticalGrid` items.
- [ ] Annotate domain models with `@Stable` or `@Immutable`.
- [ ] Perform heavy work (scanning, palette extraction) on `Dispatchers.IO` or `Dispatchers.Default`.
- [ ] Limit thumbnail sizes to 64-128dp to preserve memory.
