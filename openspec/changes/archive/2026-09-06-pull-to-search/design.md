## Context

`HomeScreen` currently renders the app list in a `LazyColumn` inside a `BoxWithConstraints` surface (monochrome theme, size-agnostic for main + cover displays). `HomeViewModel` exposes `apps: StateFlow<List<AppInfo>>`. We want a pull-down gesture on that shared surface to reveal a search field above the list and open the keyboard, with live filtering.

## Goals / Non-Goals

**Goals:**
- Pull-down on the list reveals a search field above it, focused, with the soft keyboard open.
- Typing filters the list live (case-insensitive substring on label); empty query = full list.
- Collapse path (back / clear) restores the full list and hides keyboard.
- Keep a single shared surface for both main and cover displays.
- Zero new dependencies.

**Non-Goals:**
- Fuzzy/predictive search, search history, recent/frequent app ranking.
- Search suggestions or deep actions on results.
- A persistent, always-visible search bar.

## Decisions

**D1 — Use Material3 `PullToRefreshBox` for the gesture (experimental API)**
Wrap the list in `PullToRefreshBox` (available in the current Compose BOM, no new dependency) with the default indicator suppressed (`indicator = {}`) to keep the black-and-white surface noise-free. Crossing the pull threshold triggers `onRefresh`, which reveals the search header.
*Alternative considered:* hand-rolled `AnchoredDraggable` for fractional expansion of the header while dragging. Rejected for the baseline — more complexity, and a settle-then-show interaction still matches the stated behavior. Revisit in a tune-up.
*Note:* Material3 1.3 pulls are behind `@OptIn(ExperimentalMaterial3Api::class)` — applied locally, not globally.

**D2 — Search state lives in the ViewModel**
`HomeViewModel` gains a `query: StateFlow<String>` and exposes `filteredApps` derived via `combine(apps, query)` with a pure `filterApps(apps, query)` helper (case-insensitive substring). The composable stays stateless: it receives the filtered list, the query, and callbacks.
*Rationale:* filter logic is unit-testable without Android; survives configuration changes.

**D3 — Reveal/collapse interaction**
Reveal: `AnimatedVisibility` header above the list; a `LaunchedEffect` requests focus via `FocusRequester` and calls `SoftwareKeyboardController.show()`. Collapse: `BackHandler` (active while search is open) plus a trailing "×" clear control clear the query, hide the header, and hide the keyboard.
*Alternative considered:* `ModalBottomSheet`. Rejected — heavier, and the requirement is a field *above* the list, not an overlay.

**D4 — Theme conformance**
The `TextField` uses theme defaults (monochrome `ColorScheme`): black container, white text and placeholder, white cursor. No icon imagery — the clear control is the text glyph "×".
*Note:* the gesture uses an empty indicator, so no Material pull spinner appears.

## Risks / Trade-offs

- **Pull-down vs system notification shade (main display)** → The shade still works by pulling from the top edge/status bar; in-list drag reveals search. Documented behavior; if it feels wrong on device, narrow the drag region later.
- **Soft keyboard hides much of the cover display** → The list is scrollable beneath; collapse path (back) exits quickly. Fine for the baseline, cover-display tuning later.
- **`PullToRefreshBox` is experimental Material3 API** → Opt-in annotation used locally; the BOM pins a version where the API exists; upgrade-safe.
- **Empty search results** → The list simply shows nothing (no "no results" chrome), consistent with minimal-noise design; a message can be added in a tune-up if needed.

## Migration Plan

1. Add `query`/`filteredApps` to the ViewModel with `filterApps` + unit tests.
2. Wrap the list in `PullToRefreshBox`; add the animated search header.
3. Wire focus/keyboard/back/collapse.
4. Verify on emulator (pull → search + keyboard, type filters, clear restores).
5. Rollback: strip the header + wrapper; `filterApps("")` returns the full list.

## Open Questions

- Should pulling farther (long drag) keep the header visible during the drag, or is settle-then-reveal enough? (Baseline: settle-then-reveal.)
- Nice-to-have features like type-ahead ranking by usage — future change.