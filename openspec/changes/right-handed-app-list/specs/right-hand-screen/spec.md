## ADDED Requirements

### Requirement: Right-hand screen entered via corner toggle
The launcher SHALL provide a right-hand screen reachable from a corner text toggle. While on the right-hand screen, the same corner toggle SHALL return to the normal home screen, and the back gesture SHALL also return.

#### Scenario: Enter right-hand screen
- **WHEN** the user taps the corner toggle on the normal home screen
- **THEN** the right-hand screen SHALL be displayed

#### Scenario: Exit right-hand screen via toggle
- **WHEN** the user taps the corner toggle while on the right-hand screen
- **THEN** the normal home screen SHALL be displayed

#### Scenario: Exit right-hand screen via back
- **WHEN** the user presses back while on the right-hand screen
- **THEN** the normal home screen SHALL be displayed

### Requirement: Full-screen right-anchored app list
The right-hand screen SHALL render the app list on the full screen (no border), but the entries SHALL be right-anchored — pushed toward the right edge via a wide left padding — and anchored to the bottom so the first list entries sit within right-thumb reach. Its geometry SHALL match the shared surface and clear the system bars.

#### Scenario: List anchored bottom-right
- **WHEN** the right-hand screen is displayed
- **THEN** the app list SHALL appear full-width, right-anchored, and anchored to the bottom of the surface
- **AND** the first (visible) apps SHALL show near the bottom-right, closest to the thumb

### Requirement: Hidden apps above visible apps
On the right-hand screen, hidden apps SHALL be grouped at the top of the list and visible apps at the bottom, closest to the thumb. Within each group apps SHALL remain alphabetical. This ordering applies to the right-hand screen only; the normal home screen keeps visible-then-hidden ordering.

#### Scenario: Hidden apps on top
- **WHEN** the user opens the right-hand screen and some apps are hidden
- **THEN** the hidden apps SHALL appear at the top of the list
- **AND** the visible apps SHALL appear below them, nearest the bottom thumb zone

#### Scenario: Groups alphabetical
- **WHEN** the right-hand screen groups apps
- **THEN** each group SHALL be ordered alphabetically by display name

### Requirement: Shared state and interactions
The right-hand screen SHALL reuse the same hidden-package set, live search filtering, tap-to-launch, long-press hide/unhide/select menu, and selection-mode bulk actions as the normal home screen, so changes made on either screen are reflected on both.

#### Scenario: Hide change reflects on both screens
- **WHEN** the user hides an app on either screen
- **THEN** the app SHALL be hidden in the same persistent state on both screens

#### Scenario: Interactions work identically
- **WHEN** the user taps, long-presses, or selects apps on the right-hand screen
- **THEN** the same behaviors SHALL occur as on the normal home screen

### Requirement: Search revealed by pull-up
On the right-hand screen, a drag upward within the list past a threshold SHALL reveal the shared search field pinned at the bottom of the screen (thumb-reach). Clearing, ×, or back SHALL collapse the search field and restore the full list. The list SHALL remain bottom-anchored while searching — results run bottom-up exactly like the non-hidden apps — and SHALL be kept above the soft keyboard.

#### Scenario: Pull-up reveals search
- **WHEN** the user drags upward within the list past the threshold while search is hidden
- **THEN** the search field SHALL appear at the bottom of the screen

#### Scenario: Search filters the list
- **WHEN** the user types in the revealed search field
- **THEN** the list SHALL filter live to matching apps, kept bottom-up and above the soft keyboard

#### Scenario: Collapse restores full list
- **WHEN** the user clears the query, taps ×, or presses back while search is open
- **THEN** the search field SHALL hide and the full bottom-anchored list SHALL restore