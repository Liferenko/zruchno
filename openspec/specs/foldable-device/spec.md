## Purpose

The project is oriented at foldable/flip devices (target: Xiaomi Mix Flip 3 and its cover screen) and declares itself as such in its public metadata, mirroring the `foldable-device` GitHub topic convention.

## Requirements

### Requirement: Project declared as foldable-oriented
The project SHALL be explicitly identified as oriented to foldable/flip devices in its public metadata, mirroring the `foldable-device` topic convention used on GitHub.

#### Scenario: Metadata identifies foldable support
- **WHEN** the repository is published or inspected
- **THEN** its metadata SHALL declare it as oriented to foldable devices (e.g. via the `foldable-device` topic)

### Requirement: Foldable cover-screen context documented
The project SHALL document support for the Xiaomi Mix Flip 3 and its cover screen in its README so contributors understand the device target.

#### Scenario: README documents target device
- **WHEN** a developer reads the project README
- **THEN** the README SHALL mention the Xiaomi Mix Flip 3 as the target foldable device and its cover screen