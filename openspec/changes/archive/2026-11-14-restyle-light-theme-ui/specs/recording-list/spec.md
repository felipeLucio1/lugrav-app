## ADDED Requirements

### Requirement: Recording screen section header
The recordings screen SHALL display a visual section header above the recordings list.

#### Scenario: Section header is displayed
- **WHEN** the recordings screen is displayed
- **THEN** it SHALL show the title “Recentes”
- **AND** it SHALL show the subtitle “Sua biblioteca de sons capturados”

### Requirement: Light top bar styling
The top bar SHALL use the light theme primary color and preserve contextual selection behavior.

#### Scenario: Normal top bar
- **WHEN** no recording is selected
- **THEN** the top bar SHALL display the centered “Lugrav” title
- **AND** it SHALL NOT display leading or trailing actions
- **AND** its background SHALL use `SkyBlue`

#### Scenario: Selection top bar
- **WHEN** a recording is selected
- **THEN** the top bar SHALL preserve the back arrow and context menu
- **AND** the existing Share, Rename, Select tags and Delete actions SHALL remain available

### Requirement: Tag carousel visual styling
The tag carousel SHALL use tonal pills while preserving existing labels and filter behavior.

#### Scenario: New tag action
- **WHEN** the carousel is displayed
- **THEN** the “Nova Tag” action SHALL be rendered as an outlined pill with an add icon
- **AND** tapping it SHALL open the existing new tag dialog

#### Scenario: Tag filter pills
- **WHEN** tags exist
- **THEN** each tag SHALL be rendered as a tonal pill with a colored dot and label
- **AND** selected filters SHALL be visually distinguished
- **AND** tapping a tag SHALL preserve the existing filter toggle behavior

### Requirement: Recording card visual styling
The recording card SHALL use the light theme card surface, typography and spacing while preserving existing content and interactions.

#### Scenario: Card content
- **WHEN** a recording card is rendered
- **THEN** it SHALL display the file name as its title
- **AND** it SHALL display date and duration metadata
- **AND** it SHALL display associated tags when present
- **AND** it SHALL NOT display a “+ Tag” quick action

#### Scenario: Card interaction
- **WHEN** the user taps or long presses a recording card
- **THEN** the existing playback and selection behaviors SHALL be preserved
- **AND** the selected card SHALL use a `SkyBlue` border

### Requirement: Recording FAB visual styling
The recording FAB SHALL use the light theme primary color, tonal shape and existing labels.

#### Scenario: Idle FAB
- **WHEN** recording is not active
- **THEN** the FAB SHALL display the microphone icon and “Gravar” label
- **AND** it SHALL use `SkyBlue` as its container color

#### Scenario: Recording FAB
- **WHEN** recording is active
- **THEN** the FAB SHALL display the stop icon and elapsed time
- **AND** it SHALL use the error color as its container color

### Requirement: Tag color palette
The new tag dialog SHALL offer ten tonal color options and a Slate default color.

#### Scenario: Ten colors are available
- **WHEN** the new tag dialog is displayed
- **THEN** it SHALL display ten color swatches

#### Scenario: Default color
- **WHEN** the user does not select a color
- **THEN** the selected color SHALL be Slate `#475569`

#### Scenario: No tags are created automatically
- **WHEN** the app is launched or the new tag dialog is displayed
- **THEN** no tags SHALL be created, seeded, renamed, associated or migrated
- **AND** only a tag explicitly submitted by the user SHALL be created

### Requirement: Tonal tag badges
Tag badges SHALL use a pastel container and accent-colored text and dot.

#### Scenario: Tag badge with palette color
- **WHEN** a tag with a palette color is rendered
- **THEN** the badge SHALL use its pastel container color
- **AND** its text and dot SHALL use the accent color

#### Scenario: Tag badge with legacy color
- **WHEN** a tag with a color outside the new palette is rendered
- **THEN** the badge SHALL derive a readable tonal style from its stored color
- **AND** the stored tag color SHALL NOT be modified

### Requirement: Dialog and bottom sheet styling
Dialogs and the recording bottom sheet SHALL use the light theme typography, colors and shapes.

#### Scenario: Dialog styling
- **WHEN** a dialog is displayed
- **THEN** its title SHALL use the headline typography
- **AND** its content and actions SHALL use the light theme color scheme

#### Scenario: Bottom sheet styling
- **WHEN** the recording bottom sheet is displayed
- **THEN** it SHALL use the light surface color
- **AND** the playback control SHALL use the primary color
- **AND** existing labels and playback behavior SHALL be preserved
