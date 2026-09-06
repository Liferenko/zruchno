## ADDED Requirements

### Requirement: Long-press opens options window
Long-pressing an app label SHALL open a small options window from which the user can choose actions for that app. The window SHALL currently offer a **Hide** option.

#### Scenario: Long-press on app label
- **WHEN** the user long-presses an app label in the app list
- **THEN** an options window SHALL appear containing a "hide" option

#### Scenario: Dismiss without action
- **WHEN** the user taps outside the options window or presses back
- **THEN** the options window SHALL close without applying any option

### Requirement: Hide demotes app to bottom of list
Choosing **Hide** for an app SHALL remove it from its alphabetical position and place it at the bottom of the app list, after all visible apps, ordered alphabetically among other hidden apps.

#### Scenario: Hide moves app to bottom
- **WHEN** the user chooses the hide option for an app
- **THEN** the app SHALL disappear from its current position in the list
- **AND** the app SHALL appear at the bottom of the app list

#### Scenario: Hidden apps grouped alphabetically
- **WHEN** multiple apps are hidden
- **THEN** the hidden apps SHALL appear grouped at the bottom, ordered alphabetically among themselves

### Requirement: Hidden apps rendered at reduced opacity
Hidden apps SHALL be rendered at 15% opacity of the primary text color so they remain distinguishable from visible apps.

#### Scenario: Hidden app label dimmed
- **WHEN** the app list is displayed
- **THEN** hidden app labels SHALL use 15% of the primary text color opacity

#### Scenario: Hidden app still tappable
- **WHEN** the user taps a hidden app label
- **THEN** the app SHALL launch normally

### Requirement: Hidden state persists
The set of hidden apps SHALL persist across launcher restarts.

#### Scenario: Restart preserves hidden apps
- **WHEN** the launcher is restarted
- **THEN** the previously hidden apps SHALL remain hidden and stay at the bottom of the list

### Requirement: Unhide restores app to visible list
The options window SHALL offer **Unhide** in place of **Hide** when opened for an already-hidden app. Choosing it SHALL remove the app from the hidden group and restore it to its alphabetical position among the visible apps at full opacity.

#### Scenario: Unhide option shown for hidden app
- **WHEN** the user long-presses an already-hidden app label
- **THEN** the options window SHALL contain an "unhide" option in place of "hide"

#### Scenario: Unhide restores position and appearance
- **WHEN** the user chooses the unhide option for a hidden app
- **THEN** the app SHALL return to its alphabetical position among the visible apps
- **AND** the app label SHALL render at the full primary text color

#### Scenario: Unhide from search results
- **WHEN** the user chooses the unhide option for a hidden app shown in search results
- **THEN** the app SHALL be unhidden and display normally in the home list

### Requirement: Select mode for bulk actions
Long-pressing any app SHALL offer a **Select** option that enters a selection mode. In selection mode a selected indicator SHALL appear on the left side of each selected app, tapping additional apps SHALL toggle their selection, and a bulk-action bar SHALL show the selected count with bulk hide/unhide controls. Selection mode SHALL end without applying changes when the user presses the exit control or back.

#### Scenario: Enter selection mode
- **WHEN** the user long-presses an app and chooses the select option
- **THEN** the app SHALL enter selection mode
- **AND** a selected indicator SHALL appear on the left side of the long-pressed app

#### Scenario: Selecting more apps
- **WHEN** the user taps additional apps while in selection mode
- **THEN** each tapped app SHALL toggle its selected state and show/hide the selected indicator accordingly

#### Scenario: Exit without changes
- **WHEN** the user presses the exit control or back while in selection mode
- **THEN** selection mode SHALL end without applying any bulk action

### Requirement: Bulk hide and unhide
The bulk-action bar in selection mode SHALL offer **hide** and **unhide** actions. Choosing **hide** SHALL hide every selected visible app. Choosing **unhide** SHALL unhide every selected hidden app. After the action, applied apps SHALL leave the selection and selection mode SHALL end when nothing remains selected.

#### Scenario: Bulk unhide hidden apps
- **WHEN** the user has selected 10 hidden apps and presses the unhide action
- **THEN** all 10 apps SHALL become visible again
- **AND** the apps SHALL return to their alphabetical positions at full primary text color

#### Scenario: Bulk hide visible apps
- **WHEN** the user has selected several visible apps and presses the hide action
- **THEN** the selected apps SHALL move to the bottom of the list at 15% opacity