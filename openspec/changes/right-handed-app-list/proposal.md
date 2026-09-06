## Why

The launcher is a full-screen, top-anchored list, so on a large phone the most-used (non-hidden) apps sit far from a right thumb. A second surface acting as a proof of concept helps the user reach non-hidden apps one-handedly: the list is reproduced in a bottom-right aligned region where the visible apps sit directly under the thumb, and hidden apps are pushed above, out of the way.

## What Changes

- Add a second screen (proof of concept) that reproduces the home app list in a **bottom-right aligned region** of the shared surface.
- **Invert the hidden/visible grouping**: hidden apps are listed first (top), and visible apps last (bottom, closest to the thumb). Within each group apps remain alphabetical.
- Reuse the existing ViewModel, shared hidden-package state, live search filtering, and the long-press hide/unhide/select menu so both screens stay consistent.
- **Corner text toggle** switches between the normal home screen and the right-hand screen; the toggle or back exits back to the normal home screen.
- **Search** on this screen is pulled up from the bottom edge (provisional interaction — "pulled down-to-up" — to be validated on device).
- **BREAKING**: none. The normal home screen and its ordering are unchanged.

## Capabilities

### New Capabilities
- `right-hand-screen`: The bottom-right aligned app-list surface for right-handed one-hand use, including inverted hidden ordering, bottom-pulled search reveal, and the corner toggle that enters/exits it.

### Modified Capabilities
<!-- Existing capabilities whose REQUIREMENTS are changing. Leave empty if only a new capability is added. -->

## Impact

- `ui`: new `RightHandScreen` composable, a corner toggle, and route branching in `HomeRoute` (`HomeScreen.kt`).
- `HomeViewModel`: reuse `apps`, `hiddenPackages`, `filteredApps`; add a pure inverted-ordering helper (`hidden first, then visible`) with unit tests.
- Ordering/search semantics scoped to the new screen; main-screen behavior unchanged.
- No new dependencies.