## Context

The launcher lists every installed app as a plain white text label in a `LazyColumn`, ordered alphabetically, driven by `HomeViewModel` (`apps`, `query`, `filteredApps`). Apps launch on tap. There is currently no per-app management.

Users want to remove distracting apps from the home list without uninstalling them: long-press an app label, choose **hide**, and the app is demoted to the bottom of the list and dimmed. Hidden apps must stay reachable — specifically they must still surface in search results exactly as before.

## Goals / Non-Goals

**Goals:**
- Long-press on an app label opens a small options window (context menu).
- A **Hide** option demotes the app to the bottom of the alphabetical home list.
- Hidden labels render at 30% opacity of the primary (white) text color.
- Hidden apps appear in search results unchanged: normal opacity, alphabetical position, still filterable.
- Hidden state survives app restarts.

**Non-Goals:**
- Unhide/show option (single option for now; listed as open question).
- Reordering, dragging, pinning, or manual sorting.
- Hiding apps from search results.
- Hiding apps from the system launcher — this stays purely cosmetic within our launcher.

## Decisions

### D1: Hidden state as a persistent `Set<String>` of package names
Hidden identity lives in a `MutableStateFlow<Set<String>>` loaded from `SharedPreferences` (string set) on ViewModel init and written back on each change.

- **Why not** add `isHidden` to `AppInfo`? `AppInfo` is rebuilt from `PackageManager` on every refresh; persistence would be wiped. Keeping hidden state separate survives refreshes and keeps `AppInfo` a pure query result.
- Alternative (Room/datastore) — overkill for a package-name set; `SharedPreferences` is synchronous and already available.

### D2: Home ordering computed in the ViewModel via a pure function
New pure top-level function `orderHomeApps(apps, hiddenPackages): List<AppInfo>` partitions apps into visible (alphabetical) followed by hidden (alphabetical). Exposed as `homeApps: StateFlow<List<AppInfo>>` = `combine(apps, hiddenPackages)`.

- `filteredApps` stays untouched: search results keep the original filter → hidden apps show normally.
- UI rule: `query.isBlank() → homeApps` (dim hidden), `query non-blank → filteredApps` (normal).

### D3: Options window = Material3 `DropdownMenu` anchored to the long-pressed label
`DropdownMenu` gives outside-tap/back dismissal and focus handling for free, and renders as a small monochrome panel (container color = background, thin white outline, "hide" as a text item).

- Alternative: fully custom overlay — more gesture/dismiss code for no benefit here.
- Visual consistency with the terminal aesthetic: single option row, text-only.

### D4: Long-press via `combinedClickable`
Rows switch from `Modifier.clickable` to `Modifier.combinedClickable(onClick = launch, onLongClick = openMenu)` (`@OptIn(ExperimentalFoundationApi)`).

### D5: 30% dim via alpha on the label color
Hidden rows use `MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)` (white at 30%).

### D6: Selection mode is UI-local state
Selection mode and the selected set live in `HomeScreen` (`selectionMode: Boolean`, `selected: Set<String>`), not the ViewModel — selection is transient UI state with no persistence.

### D7: Bulk actions act on package-name subsets via a single ViewModel action
`setHidden(packages: Set<String>, hidden: Boolean)` adds/removes many packages at once and persists once, avoiding per-app `apply()` churn with 80+ apps.

### D8: Markers as monospace text glyphs
In selection mode each row gains a leading marker slot: unselected `·`, selected `»`. Matches the terminal aesthetic; no icon assets.

### D9: Bulk-action bar rendered below the list
A bar at the bottom of the screen shows `N selected` plus **hide** / **unhide** / **×** controls — thumb-reachable on a tall/cover screen per the CAB → bottom-bar guidance. Each action applies to the applicable subset of the selection (hide → visible selected, unhide → hidden selected); empty subset is a no-op. Back or **×** exits selection mode without changes.

## Risks / Trade-offs

- [Hidden apps are not unhide-able until an option exists] → Future "show" option in the same menu; search still surfaces them so they are never lost.
- [Long-press conflicts with list scrolling touch slop] → Native `combinedClickable` already separates tap/long-press from drag correctly.
- [SharedPreferences set mutation] → Always copy the set before mutating; write back with `apply()`.
- [DropdownMenu default surface colors may break monochrome] → Explicit `containerColor`/`shape` override to preserve the theme.
- [Selection state lost on configuration change] → Accepted for now (UI-local `remember`); restore-selection on rotation can be added later.

## Migration Plan

- Add ViewModel state + pure ordering helpers with unit tests.
- Wire UI (combinedClickable, DropdownMenu, dimmed rendering).
- Verify on emulator: hide → app moves to bottom dimmed; search → app appears normally; restart → state persists.

## Open Questions

- Should a future menu add **show/unhide** (toggle) so users can restore apps without search? — Resolved: the same long-press menu offers "hide"/"unhide" based on current state, so hidden apps can be restored from both the home list and search results.
- Ordering of hidden apps at the bottom: alphabetical (chosen) vs most-recently-hidden first vs original order?