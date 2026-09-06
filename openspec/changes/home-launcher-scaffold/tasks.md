## 1. Launcher Registration

- [x] 1.1 Add `HOME` + `DEFAULT` intent-filter to `MainActivity` in `AndroidManifest.xml`, keeping the existing `CATEGORY_LAUNCHER` filter
- [ ] 1.2 Verify the manifest exposes the activity to both the home picker and the app drawer (manual: press HOME, launcher appears in chooser)

## 2. Monochrome Theme

- [x] 2.1 Replace the template `Color.kt` palette with the monochrome palette (black background, white foreground, onSurface variants)
- [x] 2.2 Update `Theme.kt` to apply the monochrome `ColorScheme` and disable dynamic color
- [x] 2.3 Update `Type.kt` to use a monospace default font family
- [x] 2.4 Set a launcher-appropriate app label in `strings.xml` and reference it from the manifest

## 3. App List

- [x] 3.1 Add an `AppInfo` model (packageName, display label)
- [x] 3.2 Add `HomeViewModel` that queries `PackageManager.queryIntentActivities` (launcher intents), removes non-app entries, sorts alphabetically by label
- [x] 3.3 Implement the home surface: scrollable list of plain `Text` app labels (no icons, no imagery)
- [x] 3.4 Wire label taps to launch the corresponding app via its package launch intent

## 4. External / Cover Display Support

- [x] 4.1 Make the home surface size-agnostic using `BoxWithConstraints` (no hard-coded layout dimensions) so compact cover displays scroll correctly
- [x] 4.2 Confirm the monochrome theme renders identically at main-display and cover-display sizes (no color or chrome divergence)

## 5. Foldable Device Metadata

- [x] 5.1 Add a `README.md` documenting the Xiaomi Mix Flip 3 target, cover-screen support, and the `foldable-device` orientation topic
- [x] 5.2 Note the `foldable-device` GitHub topic in repo metadata/README so the repo is discoverable under that category

## 6. Verification

- [x] 6.1 `./gradlew assembleDebug` builds successfully
- [x] 6.2 Unit tests pass (`./gradlew testDebugUnitTest`)
- [ ] 6.3 Manual smoke test: set launcher as default home; app labels render as white-on-black text; tapping a label opens that app; long lists scroll