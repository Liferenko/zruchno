## Purpose

The launcher enumerates installed launchable apps and presents them as plain text labels — no icons, no imagery — sorted alphabetically and launchable by tapping.

## Requirements

### Requirement: Enumerate launchable apps
The launcher SHALL query the system for installed apps that declare a launcher intent (`ACTION_MAIN` + `CATEGORY_LAUNCHER`) and include them in the app list.

#### Scenario: Launcher apps present
- **WHEN** the launcher builds its list
- **THEN** every installed launcher-capable app SHALL appear in the list

#### Scenario: App not launcher-capable
- **WHEN** an installed app does not declare a launcher intent
- **THEN** the app SHALL NOT appear in the list

### Requirement: Text-only labels with no icons
The launcher SHALL render each app as a plain text label. The launcher SHALL NOT load or display app icons anywhere in the app list.

#### Scenario: List rendering
- **WHEN** the app list is displayed
- **THEN** each entry SHALL show only the app's display name as text and no icon image

### Requirement: Alphabetical ordering
The launcher SHALL sort the app list by display label in ascending order. Apps hidden by the user SHALL be grouped after all visible apps, also ordered alphabetically.

#### Scenario: Sorted list
- **WHEN** the app list is displayed
- **THEN** entries SHALL be ordered alphabetically by their display name

#### Scenario: Hidden apps at the bottom
- **WHEN** some apps are hidden
- **THEN** the hidden apps SHALL appear after all visible apps, in alphabetical order

### Requirement: Launch selected app
Tapping an app label SHALL launch the corresponding app.

#### Scenario: Launch on tap
- **WHEN** a user taps an app label
- **THEN** the device SHALL start that app's main activity

### Requirement: Scrollable list
The app list SHALL scroll when the number of apps exceeds the visible area.

#### Scenario: Many apps
- **WHEN** the app list is longer than the screen viewport
- **THEN** the list SHALL be scrollable to reach every entry

### Requirement: Filter list by search query
The launcher SHALL filter the displayed app list live as the user types, matching the query case-insensitively as a substring of the app label. An empty (or whitespace-only) query SHALL show the full list. Hidden apps SHALL continue to appear in search results with their normal appearance, exactly as before being hidden.

#### Scenario: Live filtering
- **WHEN** the user types a query such as "ca"
- **THEN** only apps whose labels contain "ca" remain in the list

#### Scenario: Empty query restores full list
- **WHEN** the user clears the search query
- **THEN** the full, unfiltered app list SHALL be displayed

#### Scenario: No matches
- **WHEN** no installed app label contains the query
- **THEN** the app list SHALL be empty

#### Scenario: Hidden apps in search results
- **WHEN** the user types a query that matches a hidden app's label
- **THEN** the hidden app SHALL appear in the search results with its normal appearance, as before hiding