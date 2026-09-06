## ADDED Requirements

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
The launcher SHALL sort the app list by display label in ascending order.

#### Scenario: Sorted list
- **WHEN** the app list is displayed
- **THEN** entries SHALL be ordered alphabetically by their display name

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