## Why

The launcher currently shows every installed app with equal visual weight. Users want to declutter the home list by hiding apps they never use or find distracting, without uninstalling them or losing access entirely.

## What Changes

- Add a long-press ("press-and-hold") gesture on app labels that opens an options window (context menu).
- Add a single option for now: **Hide**.
- Selecting **Hide** moves the app out of the normal alphabetical list to the bottom of the app list.
- Hidden apps are rendered at 30% opacity of the primary text color, so they are still reachable but visually de-emphasized.
- Hidden apps continue to appear in search results exactly as they did before hiding (same appearance and position behavior as non-hidden apps in search).

## Capabilities

### New Capabilities
- `app-hiding`: long-press context menu with a hide option, per-app hidden state, and rendering of hidden apps at reduced opacity at the bottom of the list.

### Modified Capabilities
- `app-list`: the list ordering and search-filtering requirements change — hidden apps move to the bottom of the list at reduced opacity, while search results still include them unchanged.

## Impact

- `HomeViewModel`: hidden state per app (e.g., a set of hidden package names persisted via `SharedPreferences`), list ordering logic, and an `onHideApp` action.
- `HomeScreen`: long-press gesture (`combinedClickable`), options window composable, and dimmed rendering for hidden apps.
- App list rendering and order in `LazyColumn`.
- Unit tests for ordering/dim logic and persistence.