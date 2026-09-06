## ADDED Requirements

### Requirement: Shared surface on external display
The same launcher surface used on the main screen SHALL render on the device's external (cover) display without a separate layout implementation.

#### Scenario: Cover display renders launcher
- **WHEN** the launcher is shown on the external/cover display
- **THEN** the shared monochrome home surface SHALL be displayed

### Requirement: Size-agnostic layout
The launcher layout SHALL adapt to the available screen dimensions and aspect ratio rather than assuming a specific size, so the same composables fit both a tall main display and the compact cover display.

#### Scenario: Narrow cover display
- **WHEN** the launcher is rendered in a narrow/small viewport
- **THEN** the content SHALL remain usable and fully scrollable

#### Scenario: Wide main display
- **WHEN** the launcher is rendered on the full main display
- **THEN** the content SHALL use the wider space without breaking the monochrome design

### Requirement: Theme preserved on cover display
The white-on-black monochrome theme SHALL be preserved on the external/cover display.

#### Scenario: Theme consistency
- **WHEN** the launcher renders on the cover display
- **THEN** the background SHALL remain black and content white