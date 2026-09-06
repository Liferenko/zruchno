## ADDED Requirements

### Requirement: Filter list by search query
The launcher SHALL filter the displayed app list live as the user types, matching the query case-insensitively as a substring of the app label. An empty (or whitespace-only) query SHALL show the full list.

#### Scenario: Live filtering
- **WHEN** the user types a query such as "ca"
- **THEN** only apps whose labels contain "ca" remain in the list

#### Scenario: Empty query restores full list
- **WHEN** the user clears the search query
- **THEN** the full, unfiltered app list SHALL be displayed

#### Scenario: No matches
- **WHEN** no installed app label contains the query
- **THEN** the app list SHALL be empty