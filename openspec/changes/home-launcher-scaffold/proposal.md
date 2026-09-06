## Why

We want to build our own home-screen launcher for Android inspired by the *minimalist phone* app: text labels instead of icons, minimal visual noise, white text on black background. The project currently has only the stock Compose template, so we need a baseline scaffold — an app that acts as the device home screen and is ready for future tune-up. Target device: Xiaomi Mix Flip 3, where the same minimal white-on-black home also appears on the external (cover) screen.

## What Changes

- Scaffold the app as a real **home-screen launcher** (responds to `ACTION_MAIN` + `CATEGORY_HOME`).
- Introduce a **monochrome design system**: black background, white text, no icons — names everywhere.
- Render a **text-only list of installed apps** that can be launched by tapping the label.
- Prepare the UI for the **external/cover screen** so the same minimal home renders there.
- **Out of scope**: usage-time limits, app-tracking analytics, and any non-visual behavior from the minimalist phone reference. This change only captures the visual baseline.

## Capabilities

### New Capabilities
- `home-launcher`: The app registers as the Android home screen (HOME intent) and renders the launcher surface.
- `app-list`: Enumerates launched installed apps and displays them as plain text labels, tappable to launch.
- `monochrome-design`: The shared visual language — black background, white text, no icons, minimal noise (CLI/TUI style).
- `external-display`: The same monochrome home renders on the foldable's external/cover screen.
- `foldable-device`: The project is oriented at foldable devices (like the Xiaomi Mix Flip 3) and their cover screens; mirrors the `foldable-device` topic used on GitHub repos for this category.

### Modified Capabilities
<!-- No existing capabilities. openspec/specs/ is empty. -->

## Impact

- `app/` module: `AndroidManifest.xml` (HOME intent-filter), `MainActivity`, new launcher UI screens.
- Compose theme: replace template theme (`Color.kt`, `Theme.kt`, `Type.kt`) with the monochrome system.
- App label and launcher config to reflect that the app is a home screen.
- No new dependencies expected beyond the current Compose setup.