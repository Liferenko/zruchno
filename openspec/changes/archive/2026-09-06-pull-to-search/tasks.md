## 1. Search Reveal Gesture

- [x] 1.1 Wrap the app list in Material3 `PullToRefreshBox` (experimental opt-in), suppressed indicator, `onRefresh` reveals search
- [x] 1.2 Render the search field as an animated header above the list (`AnimatedVisibility`)
- [x] 1.3 Wire the field to the query value and change callback, single-line, monochrome theme defaults
- [x] 1.4 On reveal: request focus (`FocusRequester`) + open soft keyboard; add `BackHandler` that collapses and hides the keyboard
- [x] 1.5 Add a trailing "×" clear control that clears the query and collapses the field

## 2. Live Filtering

- [x] 2.1 Add `query: StateFlow<String>` and `onQueryChange`/`clearSearch` to `HomeViewModel`
- [x] 2.2 Add pure `filterApps(apps, query)` helper (case-insensitive substring; blank → full list)
- [x] 2.3 Expose `filteredApps` derived via `combine(apps, query)`
- [x] 2.4 Update `HomeRoute` to collect `filteredApps` and `query` and pass callbacks through `HomeScreen`

## 3. Verification

- [x] 3.1 Unit test `filterApps` (matching, case-insensitivity, blank query, no matches)
- [x] 3.2 `./gradlew assembleDebug` builds
- [x] 3.3 `./gradlew testDebugUnitTest` passes
- [x] 3.4 Manual (emulator): pull down → search + keyboard appear; typing filters; clear/back restores the full list