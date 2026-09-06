## ADDED Requirements

### Requirement: Pull-down reveals search field
The launcher SHALL reveal a search input pinned above the app list when the user pulls the apps list down past the gesture threshold.

#### Scenario: Pull reveals search
- **WHEN** the user pulls down on the app list past the threshold
- **THEN** a search input SHALL appear above the apps list

### Requirement: Keyboard opens on reveal
When the search field is revealed, the launcher SHALL focus the field and open the soft keyboard so typing can begin immediately.

#### Scenario: Autofocus and keyboard
- **WHEN** the search field is revealed
- **THEN** the field SHALL be focused and the soft keyboard SHALL be visible

### Requirement: Collapse restores full list
The launcher SHALL provide a collapse path (back gesture or clear control) that clears the search query, hides the search field, restores the full app list, and hides the soft keyboard.

#### Scenario: Back gesture collapses
- **WHEN** the user triggers back while search is open
- **THEN** the search field SHALL hide, the query SHALL clear, the full list SHALL restore, and the keyboard SHALL hide

#### Scenario: Clear control collapses
- **WHEN** the user activates the clear control while search is open
- **THEN** the query SHALL clear and the field SHALL close

### Requirement: Works on the shared cover-screen surface
The pull-to-search behavior SHALL work identically on the shared home surface rendered on the main and external (cover) displays, without a separate layout.

#### Scenario: Search on cover display
- **WHEN** the launcher is shown on the compact cover display and the user pulls the list down
- **THEN** the search field SHALL appear above the list and remain usable in the narrow viewport