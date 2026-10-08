# Pulse Music Design Guidelines

Pulse Music follows a **custom, modern aesthetic** that blends some Material Design principles with unique, iOS-inspired patterns. 

This document is the definitive guide for designing and implementing UI in the Pulse Music codebase. All new UI work and refactors must follow these custom principles rather than strictly adhering to Google's Material Design 3 spec.

---

## 1. Color System & Theming

We use a dynamic color system, but apply it in a custom way to achieve a unique look.

### Dynamic Color & Seed
*   **Dynamic First:** Colors must come from `MaterialTheme.colorScheme`, but are often modified (e.g., using alpha transparency) to create glass-like effects.
*   **Translucency:** A core part of the Pulse Music look is translucent surfaces. For example, cards often use `surfaceVariant.copy(alpha = 0.3f)` rather than solid M3 container colors.

### Semantic Color Roles
Use the correct semantic color roles as defined by our theme:
*   **Primary (`primary` / `onPrimary`):** Used for the most prominent components across the app, active states, and filled buttons.
*   **Surface (`surface` / `onSurface`):** Backgrounds for the app and solid menus.
*   **Translucent Surfaces:** Custom translucent backgrounds (like `surfaceVariant.copy(alpha = 0.3f)`) are heavily used for cards, segmented buttons, and grouped lists to create a softer, layered aesthetic.

---

## 2. Components in Detail

Do NOT strictly force Material 3 components if they break the app's custom aesthetic. Match the existing components found in the app.

### Buttons & Controls
*   **Segmented Controls:** We frequently use custom segmented buttons (e.g., Row with rounded buttons) rather than M3 standard tabs or segmented buttons.
*   **Rounded Shapes:** Elements heavily lean towards large corner radii (`RoundedCornerShape(24.dp)` or `CircleShape`).

### Cards & Surfaces
*   **Custom Cards:** Unlike standard M3 cards (which use solid `surfaceContainer` colors), Pulse Music cards typically use:
    *   *Container:* `surfaceVariant.copy(alpha = 0.3f)`
    *   *Shape:* `RoundedCornerShape(24.dp)` or `28.dp`
    *   *Elevation:* 0.dp (flat, translucent look).
*   Grouped items within cards are a common pattern (similar to iOS Settings).

### Navigation & Headers
*   **Top App Bars:** We often use custom implementations or standard `TopAppBar` rather than `LargeTopAppBar`. Headers are sometimes manually placed over scrolling content with custom fade-in animations rather than using standard M3 `Scaffold` scroll behaviors.
*   **Bottom Navigation Bar:** Custom floating tab bars (`ui/component/floatingtabbar/`) are preferred over standard M3 `NavigationBar`.

### Grouped Settings Lists

Settings rows are grouped into labelled sections, rendered as **one panel per
section** rather than one card per row:

* **Container:** reuse the app's existing `Modifier.liquidGlass` panel
  (`ui/component/GlassEffect.kt`) — the same container as the floating tab bar.
  Shape is `RoundedCornerShape(24.dp)` and elevation is `0.dp`. When the
  liquid-glass preference is off, or the device is below API 31, fall back to a
  translucent `surfaceVariant.copy(alpha = 0.3f)` fill so the panel still reads
  correctly.
* **Dividers:** a hairline (`0.5.dp`) `HorizontalDivider` inset from the row text
  separates consecutive rows inside a section.
* **Section labels:** uppercase, `labelLarge`, `onSurfaceVariant`, using the app's
  `RoundedCornerShape` grouping conventions.
* **Icons — monochrome, not colour-coded:** every resting row icon uses one
  neutral tone. Section identity comes from the label, **not** from a per-section
  accent colour; a colourful icon badge per section reads as Material You and
  fights the minimal aesthetic. A single sparing accent is reserved for the
  active/selected state.
* **Icon tiles:** row icons sit on a translucent `surfaceVariant.copy(alpha = 0.3f)`
  tile, keeping the sharp, minimal iconography rather than soft filled shapes.

---

## 3. Typography

Always use `MaterialTheme.typography` but respect the app's established font weights and sizes, which often lean towards bold, expressive headers and softer body text.

---

## 4. Extending the Design System

Before adding a brand new UI component, always check `ui/component/` to see if an existing one already implements our conventions.

**Key Rule:** When working on UI, **look at the existing screens** (like the original Listen Together or Settings screens) and copy their specific visual style, spacing, and modifier chains. Do NOT refactor existing screens to match standard Material 3 unless explicitly requested. Our custom aesthetic takes precedence over M3 guidelines.
