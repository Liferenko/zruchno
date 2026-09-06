## Context

The repo currently holds a stock Android Compose template (`com.example.a10101010`, Material3, minSdk 34 / target 36). We want a baseline scaffold for our own home-screen launcher, inspired visually by the *minimalist phone* app and by terminal aesthetics (CLI/TUI, Vim). The launcher must render as white-on-black text with no icons, and the same minimal surface should work on both the main screen and the external/cover screen of the Xiaomi Mix Flip 3. This change captures only the visual baseline; behavior like usage-time limits is explicitly out of scope.

## Goals / Non-Goals

**Goals:**
- App registers as a real Android home screen (HOME intent) and replaces the default launcher when selected.
- Deliver a monochrome visual system: pure black background, white text, no icons, minimal noise.
- Render installed apps as a tappable text-only list.
- Use one responsive UI that renders acceptably on both the main display and the small cover display.
- Keep the scaffold minimal and easy to tune up later.

**Non-Goals:**
- Usage-time limits or any "digital wellbeing" behavior from the minimalist phone reference.
- Icon loading/rendering of any kind.
- App search, favorites, gestures, or widgets — future tune-up topics, not part of the baseline.
- Vendor-specific cover-screen SDKs (e.g. Xiaomi) and multi-module architecture.
- Navigation framework and advanced state management.

## Decisions

**D1 — Register the launcher via HOME intent-filter**
`MainActivity` declares `ACTION_MAIN` + `CATEGORY_HOME` + `CATEGORY_DEFAULT` in the manifest, so the system offers it as a home screen. The existing `CATEGORY_LAUNCHER` filter stays so the app is still reachable from the default launcher/gallery during development.
*Alternative considered:* a separate `HomeActivity` distinct from the dockable launcher entry. Rejected — one entry point keeps the baseline simple.

**D2 — Monochrome theme over Material3, not a rewrite**
Keep Material3 but override the `ColorScheme` to a single-palette monochrome (black surfaces, white text/content), disable dynamic color, and set a monospace default text style to evoke the terminal look.
*Alternative considered:* dropping Material3 for hand-rolled light-weight composables. Rejected — Material3 gives us scaffolding (navigation bar handling, insets, accessibility) for free; the visual constraints are enforced at the theme layer, and hand-rolled styling can come later if needed.

**D3 — App list from PackageManager, text-only**
Query `Intent(ACTION_MAIN).addCategory(CATEGORY_LAUNCHER)` via `PackageManager.queryIntentActivities`, filter system-internal apps minimally, sort by display label, and render each entry as a plain `Text` label in a scrollable column. Tapping launches the app. No icons are resolved or loaded.
*Alternative considered:* `launcherapps`/Coil icon loading. Rejected — icons contradict the design; async icon loading adds complexity with no baseline value.

**D4 — One shared, size-agnostic UI for main and cover screens**
Use `BoxWithConstraints` (dependency-free) to observe available width/height instead of assuming a layout. The list simply scrolls and scales text to fit, so the same composable renders on a tall main display and a small cover display without a separate layout. No hard-coded pixel sizes beyond theme defaults.
*Alternative considered:* a dedicated cover-screen layout branch. Rejected — duplication and premature; revisit only if real-device testing on the Mix Flip 3 shows the shared UI is insufficient.

**D5 — Minimal architecture for the baseline**
Single `Activity` + Compose. App-list state held in a thin `ViewModel` (survives config changes/rotation). No navigation library, DI, or module split yet.
*Rationale:* the change is a scaffold; heavier architecture belongs to later tune-up changes once the visual direction is validated on device.

## Risks / Trade-offs

- **Cover screen may not host third-party launchers on the Mix Flip 3** → Rely on size-agnostic UI first and confirm on-device; if the vendor blocks launcher replacement on the cover display, narrow the `external-display` scope to "renders correctly at that aspect when shown" and follow up with vendor SDK research.
- **Becoming the default home is user-managed** → App can't switch itself to default; document the manual setting step; keep the `LAUNCHER` category so the app stays accessible.
- **Monospace default typeface can hurt readability on the tiny cover screen** → Scale factor via `BoxWithConstraints`; keep baseline on a modest default size and tune later.
- **PackageManager queries are slow on some devices** → Baseline queries once per list composition; caching/refresh triggers are a later optimization.

## Migration Plan

1. Add the HOME intent-filter; app becomes selectable as a home screen.
2. Apply the monochrome theme and monospace typography.
3. Add the text-only app list and launch handling.
4. Verify on emulator and, when available, on the Mix Flip 3 (main + cover screen).
5. Rollback is trivial at this stage — reverting the manifest filter/theme returns a stock template.

## Open Questions

- Does Xiaomi's cover-screen implementation on the Mix Flip 3 allow a third-party launcher, and if so, with what aspect/density constraints?
- Add `androidx.window` (WindowManager) now, or keep `BoxWithConstraints` until real-device metrics are known?
- Should the launcher hide itself from its own app list? (Decide during implementation.)