## 1. Hidden State in ViewModel

- [x] 1.1 Add `orderHomeApps(apps, hiddenPackages)` pure function (visible alphabetical first, hidden alphabetical after) next to `filterApps`
- [x] 1.2 Store hidden package names in `SharedPreferences` (string set, loaded on init, persisted on change)
- [x] 1.3 Expose `hiddenPackages: StateFlow<Set<String>>` and `homeApps: StateFlow<List<AppInfo>>` (combine of `apps` + `hiddenPackages`)
- [x] 1.4 Add `hideApp(app)` action that updates the hidden set and persists it
- [x] 1.5 Expose the hidden set and `homeApps` to the route via `HomeRoute` parameters

## 2. Options Window (Long-press Menu)

- [x] 2.1 Replace row `Modifier.clickable` with `combinedClickable` (tap = launch, long-press = open options)
- [x] 2.2 Add local menu state (`optionsFor: AppInfo?`) in `HomeScreen`, anchored `DropdownMenu` styled monochrome (background container, white text)
- [x] 2.3 Wire the menu's single "hide" item to `onHideApp` and dismiss the menu
- [x] 2.4 Ensure outside-tap / back closes the menu without action

## 3. Rendering Hidden Apps

- [x] 3.1 Render hidden app labels at 30% opacity of the primary text color (`onBackground.copy(alpha = 0.3f)`) when search is not active
- [x] 3.2 Use `homeApps` when query is blank and `filteredApps` (undimmed) when query is non-blank
- [x] 3.3 Keep hidden apps tappable (launch still works, including in search results)

## 4. Unhide

- [x] 4.1 Change `hideApp` to a `toggleHidden` action that both hides and unhides a package, persisting on every change
- [x] 4.2 Render the menu option as `hide` or `unhide` depending on whether the app is currently hidden
- [x] 4.3 Unit test: unhiding a hidden app returns it to the visible group (alphabetical position)
- [x] 4.4 `./gradlew assembleDebug` builds and `./gradlew testDebugUnitTest` passes
- [x] 4.5 Manual (emulator): long-press hidden app shows "unhide", tap restores it to normal position and opacity

## 5. Verification

- [x] 5.1 Unit tests: `orderHomeApps` ordering (visible then hidden, alphabetical within groups)
- [x] 5.2 Unit tests: `filterApps` still includes hidden apps in search results
- [x] 5.3 `./gradlew assembleDebug` builds
- [x] 5.4 `./gradlew testDebugUnitTest` passes
- [x] 5.5 Manual (emulator): long-press opens menu, hide moves app to bottom dimmed, appears normally in search, persists after restart

## 6. Bulk Actions (Selection Mode)

- [x] 6.1 Add ViewModel `setHidden(packages, hidden)` bulk action (single persist)
- [x] 6.2 Add "select" option to the long-press menu; choosing it enters selection mode and marks the app selected
- [x] 6.3 Render a leading marker (`·` unselected / `»` selected) on each row while in selection mode
- [x] 6.4 Tapping a row in selection mode toggles selection instead of launching
- [x] 6.5 Add a bulk-action bar (`N selected`, hide / unhide / ×) at the bottom of the screen
- [x] 6.6 Bulk hide/unhide apply to the applicable selected subset; exit selection mode when nothing remains selected
- [x] 6.7 Back / × exits selection mode without applying changes
- [x] 6.8 Unit test: `setHiddenMany` bulk semantics via pure helper (add/remove package sets)
- [x] 6.9 `./gradlew assembleDebug` builds and `./gradlew testDebugUnitTest` passes
- [x] 6.10 Manual (emulator): select 10 hidden apps, bulk unhide, verify restored to normal list