## 1. Shared Components Refactor

- [x] 1.1 Extract `AppSearchField` composable (transparent monochrome search TextField with × clear) from `HomeScreen`, and refactor `HomeScreen` to use it
- [x] 1.2 Extract `AppOptionsMenu` composable (dropdown with hide/unhide + select) from the list-row rendering, and refactor `HomeScreen` to use it

## 2. Inverted Ordering in ViewModel

- [x] 2.1 Add pure helper `orderHandApps(apps, hiddenPackages)` returning hidden apps first, then visible apps (each group alphabetical)
- [x] 2.2 Add `handApps: StateFlow<List<AppInfo>>` to `HomeViewModel` combining `apps` + `hiddenPackages`
- [x] 2.3 Unit tests: `orderHandApps` groups hidden above visible and keeps each group alphabetical; `./gradlew testDebugUnitTest` passes

## 3. RightHandScreen UI

- [x] 3.1 Create `RightHandScreen.kt`: `BoxWithConstraints` with black background + `systemBarsPadding()`, full-screen list matching the home surface (no border), using `AppSearchField` and `AppOptionsMenu`
- [x] 3.2 List renders bottom-anchored via `reverseLayout` + `orderHomeApps` (visible group at the bottom, hidden above), tap-to-launch, hidden labels dimmed to 15% alpha
- [x] 3.3 Add pull-up search detection (~64dp threshold) via `NestedScrollConnection` onPostScroll that reveals `AppSearchField` pinned at the top of the screen; clear/×/back collapse it and restore the full list
- [x] 3.4 Confirm the pull-up detector only consumes gestures when the list is at its end (content sits inside the safe area), so the system bottom-swipe is not fought

## 4. Navigation

- [x] 4.1 Add `rightHand` `rememberSaveable` state to `HomeRoute`; add a bottom-right corner text toggle (`rh` on home screen, `hm` on right-hand screen) that flips it
- [x] 4.2 Hide the corner toggle while search is open or selection mode is active
- [x] 4.3 `BackHandler`: on the right-hand screen back first collapses search, then returns to the home screen

## 5. Selection Mode Parity

- [x] 5.1 Selection mode on the right-hand screen: `·`/`»` markers, tap toggles selection, bulk bar (`N selected`, hide / unhide / ×) at the bottom
- [x] 5.2 Back/× exits selection mode without applying changes

## 6. Verification

- [x] 6.1 `./gradlew assembleDebug testDebugUnitTest` green (extraction must not change existing behavior)
- [ ] 6.2 Manual (emulator): corner toggle enters/exits; right-hand list shows hidden apps above visible (bottom-anchored); hiding an app on one screen reflects on the other
- [ ] 6.3 Manual (emulator): pull-up in the list reveals search, typing filters the list, ×/back restores the full list
- [ ] 6.4 Manual (emulator): corner toggle is tappable and not blocked by the system bars; pull-up drag does not trigger the system gesture
- [ ] 6.5 Manual (device, Mi Mix Flip 3): right-hand list within thumb reach; list geometry (full-screen) and pull threshold feel right; tuning documented