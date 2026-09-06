## Why

The launcher renders every installed app as a text row, and with many apps installed (100+) scrolling to find one is slow — worst on the small cover display where a permanent search bar would waste scarce space. A pull-down gesture reveals a search field on demand, adding zero permanent UI footprint.

## What Changes

- Pulling the app list down reveals a search input pinned **above** the apps list.
- Revealing the field focuses it and opens the soft keyboard so typing can start immediately.
- Typing filters the list **live**: case-insensitive substring match against app names. An empty query shows the full list.
- The search can be collapsed (back gesture or clear control), which clears the query, hides the field, restores the full list, and dismisses the keyboard.
- The search field follows the monochrome theme — no icons or decorative chrome.
- **Out of scope**: fuzzy/predictive search, search history, app actions (uninstall/open info), and favorites.

## Capabilities

### New Capabilities
- `pull-search-reveal`: The pull-down gesture on the shared home surface reveals the search field above the app list and opens the keyboard for typing.

### Modified Capabilities
- `app-list`: Add live filtering by the search query — the displayed list reacts to what the user types.

## Impact

- `HomeViewModel.kt`: new `query` state flow + derived filtered list (`filterApps`: case-insensitive substring).
- `HomeScreen.kt`: pull-gesture wrapper (Material3 `PullToRefreshBox`, already available via the Compose BOM), animated search header, focus/keyboard control, back-handling.
- New unit test for the `filterApps` helper.
- One shared surface for main + cover displays (unchanged architecture).