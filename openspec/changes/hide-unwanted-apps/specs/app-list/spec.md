## MODIFIED Requirements

### Requirement: Alphabetical ordering
The launcher SHALL sort the app list by display label in ascending order. Apps hidden by the user SHALL be grouped after all visible apps, also ordered alphabetically.

#### Scenario: Sorted list
- **WHEN** the app list is displayed
- **THEN** entries SHALL be ordered alphabetically by their display name

#### Scenario: Hidden apps at the bottom
- **WHEN** some apps are hidden
- **THEN** the hidden apps SHALL appear after all visible apps, in alphabetical order

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