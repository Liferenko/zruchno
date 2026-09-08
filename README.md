# Zruchno

A minimal, terminal-inspired Android home-screen launcher. Text labels instead of icons, white text on black background, no visual noise. Say goodbye to the icons — say hello to names.

## Design

- **Monochrome:** black background, white text everywhere.
- **No icons:** every app is represented by its plain text name.
- **Terminal aesthetic:** monospace typeface, CLI/TUI/Vim vibes.
- Scope mirrors the *minimalist phone* visual language only — no usage-time limits or app-tracking behavior.

## Foldable support

This project is oriented at foldable/flip devices (GitHub topic: [`foldable-device`](https://github.com/topics/foldable-device)).

- **Target device:** Xiaomi Mix Flip 3.
- The same monochrome home surface renders on the **main display** and the **external (cover) display**.
- Layouts are size-agnostic: they adapt to any aspect ratio or viewport, so the compact cover screen works without a separate layout.

## Getting started

```bash
./gradlew assembleDebug
```

### Set as default launcher

**HyperOS / MIUI (Settings):** *Настройки → Приложения → Стандартные приложения → Домашний экран → zruchno*. Alternatively, press the **Home** button once after installing and pick **Always**.

**HyperOS / any Android 10+ (adb):**

```bash
adb shell cmd role add-role-holder android.app.role.HOME com.zruchno
```

To revert to the previous launcher, either change it in Settings or run:

```bash
adb shell cmd role remove-role-holder android.app.role.HOME com.zruchno
```

The debug APK is installed as a normal (non-testOnly) app; for daily driving, install `app/build/outputs/apk/release/app-release.apk` (non-debuggable, signed) instead.

## Out of scope (for now)

- Usage-time limits and digital-wellbeing features
- App icons, search, widgets, gestures, favorites
- Vendor-specific cover-screen SDKs