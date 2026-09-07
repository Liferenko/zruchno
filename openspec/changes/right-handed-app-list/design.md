## Context

The launcher today is a single full-screen, top-anchored, alphabetical app list (`HomeScreen` in `HomeScreen.kt`), sorted visible-then-hidden by `orderHomeApps` (`HomeViewModel.kt:25`), with pull-down search and per-app/bulk hide. On a large phone this puts frequently used (non-hidden) apps far from a right thumb. This change adds a proof-of-concept second surface that reproduces the list in a bottom-right aligned region with the grouping inverted, so non-hidden apps sit at the bottom near the thumb.

The existing ViewModel (`apps`, `hiddenPackages`, `homeApps`, `filteredApps`, `query`) and SharedPreferences hidden-state are shared — the two screens must stay consistent. The shared surface (`BoxWithConstraints`) already respects system bars via `systemBarsPadding()` (verified against the status bar and gesture/nav bar insets).

## Goals / Non-Goals

**Goals:**
- Add a `RightHandScreen`: the app list rendered full-screen, bottom-anchored, with the grouping inverted so visible apps sit at the bottom (under the thumb) and hidden apps are reachable by scrolling up.
- Corner text toggle switches between the normal home screen and the right-hand screen; back also exits the right-hand screen.
- Reuse shared state (hidden set, query) and interactions (launch on tap, long-press hide/unhide/select, selection-mode bulk bar) so both screens behave identically.
- Search on the right-hand screen is revealed by pulling up (provisional, validated on device).

**Non-Goals:**
- No changes to the normal home screen ordering or interactions.
- No new persistence, no new dependencies, no animations beyond what exists.
- No left-hand variant, no drag-to-reorder, no landscape/LTR-RTL considerations beyond the existing monochrome/compact constraints.

**Note (2026-09-06):** following a 2-hour on-device trial request, the panel PoC was replaced by a full-screen variant (no border) matching the default home layout, with bottom-anchored `reverseLayout` ordering. See D3/D4 revisions below.

## Decisions

### D1: Route-level toggle in `HomeRoute`
`HomeRoute` owns a `rightHand: Boolean` local state (rememberSaveable). `true` renders `RightHandScreen`, `false` renders the existing `HomeScreen`. Both receive the same ViewModel callbacks. Exit paths: corner toggle or `BackHandler`.

- **Why:** minimal change; the ViewModel is already scoped above the screen. No navigation library needed.
- **Alternative considered:** separate `NavHost` page — rejected as overkill for a PoC surface.

### D2: Inverted ordering as a pure function
Add `fun orderHandApps(apps: List<AppInfo>, hiddenPackages: Set<String>): List<AppInfo> = apps.partition { it.packageName in hiddenPackages }.let { (hidden, visible) -> hidden + visible }` next to `orderHomeApps` / `applyHiddenChange`. Expose `handApps: StateFlow<List<AppInfo>>` exactly like `homeApps`.

- **Why:** `_apps` is globally alphabetical, so partition preserves order within each group; pure function keeps the existing unit-test pattern (16 tests) and lets `orderHomeApps` stay untouched.
- **Alternative considered:** a `reverse`/parametrized `orderApps` — rejected; explicit function is clearer and does not risk the main screen.

### D3: Full-screen right-anchored list (was: bottom-right panel)
`RightHandScreen` uses its own `BoxWithConstraints` with `systemBarsPadding()`, background black, and a full-width `Column` (no border, same vertical extent as the home screen). The list cluster is right-aligned: rows are pushed toward the right edge via a large left `contentPadding` (`start = maxWidth * 0.38f`) and a small right padding (`end = maxWidth * 0.04f`), so the entries sit under the right thumb. The list is a `LazyColumn` with `reverseLayout = true` rendering `orderHomeApps(apps, hiddenPackages)` (`visible + hidden`). Because `reverseLayout` anchors index 0 to the bottom, the visible (non-hidden) group appears first at the bottom and hidden apps are reached by scrolling up the reversed list.

- **Why:** `reverseLayout` gives bottom-first anchoring without any custom scroll math; `orderHomeApps` (already in use on home) yields the correct visual order — hidden on top, visible at bottom. Full-width plus an asymmetric left padding reproduces the right-handed thumb-zone of the original panel while staying full-screen (no border) for the 2-hour trial.
- **Geometry note:** `contentPadding` vertical fractions match the home list so row spacing is identical.
- **Provisional:** the left `0.38f` is the right-shift, and the original D3 (0.62w × 0.66h floating panel) was the pre-trial PoC geometry — superseded by the full-screen right-anchored variant per user feedback. The fractions are provisional and tuned on the physical Mi Mix Flip 3.

### D4: Search revealed by pull-up (bottom)
The right-hand screen's reversed list uses a `NestedScrollConnection.onPostScroll` so that a negative `available.y` (finger dragging up, which is one direction beyond the reversed list's natural bottom overscroll) accumulates past a threshold (~64.dp) while search is hidden and reveals the shared, transparent, monochrome search field pinned at the top of the `Column`. Drag is consumed only inside the list, above the system gesture area so Android's bottom-swipe home gesture is not fought.

- **Why:** Material3 `PullToRefreshBox` is pull-DOWN-only; a bottom-up reveal inverts it for thumb grammar. With `reverseLayout` the linger direction that yields an unconsumed positive `dy` at the bottom end is a finger-up drag, matching the "pulled down-to-up" request.
- **Alternative:** reuse `PullToRefreshBox` pull-down — rejected per the "pulled down-to-up" request; conflicts are mitigated by keeping the pull zone inside the screen above the nav-gesture strip.
- **Risk:** gesture-nav interference on the physical device → mitigation: the drag zone starts above the navigation inset and back/× remain available; validation flagged as a manual task.

### D5: Shared interactions without a heavy refactor
Extract the reusable pieces used by both screens into small shared composables kept in `HomeScreen.kt`: `AppSearchField(...)` (the transparent field) and `AppOptionsMenu(app, isHidden, onToggleHidden, onStartSelect)`. `HomeScreen` refactors to use them; `RightHandScreen` (new file, same package) composes them with its own full-screen layout, selection markers, and a bulk bar at the screen's bottom.

- **Why:** duplicates ~30 lines today; extraction keeps the two screens from drifting and keeps `HomeScreen` reviewable. The PoC reuses the exact interaction semantics (selection mode, `applyBulk` logic copied tiny-local).
- **Alternative:** duplicate the whole list UI — rejected, drift-prone.
- Open: exact sharing of the selection/bulk state logic is left to implementation; extraction must not alter existing behavior (unit tests must stay green).

## Risks / Trade-offs

- **Gesture-nav conflict with pull-up search** → [Risk] → [Mitigation]: drag zone restricted to the screen interior above the navigation inset; fallbacks (×, back) always available; manual device task.
- **reverseLayout interaction with selection/bulk bar** → [Mitigation]: bulk bar sits outside the reversed list at the Column bottom, so item positions are unaffected; verified on emulator.
- **PoC geometry may be wrong for real thumbs** → [Mitigation]: D3 constants isolated as `%` values, tuned on the physical Mi Mix Flip 3; intentionally provisional.
- **Extraction regression on the existing screen** → [Mitigation]: full existing test suite + emulator smoke of hide/search/select after refactor.

## Migration Plan

None — additive new surface toggled off by default (`rightHand = false`). Rollback = delete the toggle path. Manual device validation against the Mi Mix Flip 3 at the end.

## Open Questions

- Full-screen list geometry and pull threshold (~64.dp) — provisional, tune on device.
- Pull-up search confirmation (per user, "not sure yet"): keep as D4 until validated; fallback is the corner-toggle or standard pull-down.
- Whether the cover display's small surface should default to the right-hand screen — default no for now.