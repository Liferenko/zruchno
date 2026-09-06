## Purpose

The app acts as a real Android home screen: it registers as a launcher via the HOME intent, renders its home surface, and stays reachable from other launchers during development.

## Requirements

### Requirement: App registers as a home screen
The application SHALL declare a `HOME` intent-filter (`android.intent.action.MAIN` + `android.intent.category.HOME` + `android.intent.category.DEFAULT`) so the system offers it as a selectable launcher.

#### Scenario: App appears in home picker
- **WHEN** a user presses HOME on a device where this app is installed
- **THEN** the system SHALL include this app in the home-app chooser

#### Scenario: App selected as default home
- **WHEN** a user selects this app as the default home screen
- **THEN** the system SHALL route the HOME intent to this app

### Requirement: App remains launcher-accessible
The application SHALL keep its `CATEGORY_LAUNCHER` entry so it can still be opened from another launcher during development.

#### Scenario: App opened from app drawer
- **WHEN** a user opens this app from another launcher's app drawer
- **THEN** the launcher surface SHALL render

### Requirement: Launcher surface renders on HOME
The application SHALL render its home surface to appear before the user opens another app from it, whenever it is the active home screen.

#### Scenario: Launcher shown on cold start
- **WHEN** the app acts as home and the user requests the home screen
- **THEN** the launcher surface SHALL be visible and interactive