## ADDED Requirements

### Requirement: Recording creation timestamp
The system SHALL populate each recording with a timestamp based on the file's `lastModified()` value.

#### Scenario: Valid file timestamp is used
- **WHEN** `getRecordingsList()` processes an existing file
- **THEN** `AudioRecordingModel.createdAtMillis` SHALL equal `File.lastModified()`

#### Scenario: Invalid file timestamp is normalized
- **WHEN** `File.lastModified()` returns `0` or a negative value
- **THEN** `AudioRecordingModel.createdAtMillis` SHALL be `0L`

### Requirement: Recordings sorted by creation timestamp
The system SHALL return recordings ordered by creation timestamp, newest first.

#### Scenario: Newest recording appears first
- **WHEN** `getRecordingsList()` returns multiple recordings
- **THEN** they SHALL be ordered by `createdAtMillis` descending

#### Scenario: Equal timestamps have deterministic order
- **WHEN** two recordings have the same `createdAtMillis`
- **THEN** they SHALL be ordered by `path` ascending

#### Scenario: Unknown timestamps appear last
- **WHEN** a recording has `createdAtMillis` equal to `0L`
- **THEN** it SHALL appear after recordings with positive timestamps

### Requirement: Recording card displays creation date and time
The recording card SHALL display the recording timestamp provided by the model.

#### Scenario: Valid timestamp is displayed
- **WHEN** a recording has `createdAtMillis` greater than `0`
- **THEN** the card SHALL display the date and time formatted as `dd MMM yyyy · HH:mm`

#### Scenario: Unknown timestamp omits date
- **WHEN** a recording has `createdAtMillis` equal to `0L`
- **THEN** the card SHALL display only the duration

### Requirement: Recording model exposes tags
The `AudioRecordingModel` SHALL expose the tags associated with the recording.

#### Scenario: Recording with tags
- **WHEN** a recording has associated tags
- **THEN** `AudioRecordingModel.tags` SHALL contain those tags

#### Scenario: Recording without tags
- **WHEN** a recording has no associated tags
- **THEN** `AudioRecordingModel.tags` SHALL be empty

#### Scenario: Existing constructors remain valid
- **WHEN** a caller creates `AudioRecordingModel` without tags
- **THEN** `tags` SHALL default to an empty list

### Requirement: Existing ordering is preserved
The recordings list SHALL continue to be ordered by `createdAtMillis` descending with `path` ascending as tie-breaker.

#### Scenario: Tags do not change ordering
- **WHEN** recordings with tags are listed
- **THEN** they SHALL be ordered by `createdAtMillis` descending
- **AND** equal timestamps SHALL be ordered by `path` ascending

### Requirement: Tag carousel
The recordings screen SHALL display a horizontally scrollable tag carousel above the recordings list.

#### Scenario: New tag action is always available
- **WHEN** the recordings screen is displayed
- **THEN** the carousel SHALL include a "Nova tag" action as its first item

#### Scenario: Existing tags are displayed
- **WHEN** tags exist
- **THEN** the carousel SHALL display one badge per tag in the alphabetical order returned by the ViewModel

#### Scenario: Selected filters are visually indicated
- **WHEN** a tag is selected as a filter
- **THEN** its badge SHALL display the selected visual state

#### Scenario: Tapping a tag toggles its filter
- **WHEN** user taps a tag badge
- **THEN** that tag SHALL be added to or removed from the active filters
- **AND** the recordings list SHALL be reloaded

#### Scenario: Tapping new tag opens the dialog
- **WHEN** user taps the "Nova tag" action
- **THEN** `NewTagDialog` SHALL be displayed

### Requirement: Tag filtering in the recordings list
The recordings screen SHALL filter recordings by the selected tags.

#### Scenario: No filter shows all recordings
- **WHEN** no tag filter is selected
- **THEN** all recordings SHALL be displayed

#### Scenario: One tag filters the list
- **WHEN** one tag filter is selected
- **THEN** only recordings associated with that tag SHALL be displayed

#### Scenario: Multiple tags require all tags
- **WHEN** multiple tag filters are selected
- **THEN** only recordings associated with every selected tag SHALL be displayed

#### Scenario: Removing a selected tag reloads the list
- **WHEN** user deselects a tag filter
- **THEN** the recordings list SHALL be reloaded without that filter

#### Scenario: Deleting a selected tag removes its filter
- **WHEN** a tag that is currently selected as a filter is deleted
- **THEN** it SHALL be removed from the active filters
- **AND** the recordings list SHALL be reloaded

### Requirement: New tag dialog
The system SHALL provide a dialog to create a tag with a name and a color.

#### Scenario: Valid tag is saved
- **WHEN** user enters a valid name and confirms
- **THEN** the tag SHALL be created with the selected color or the default color when none is selected
- **AND** the dialog SHALL close

#### Scenario: Invalid name disables save
- **WHEN** the normalized name is empty, longer than 30 characters, contains control characters, or duplicates an existing tag case-insensitively
- **THEN** the save action SHALL be disabled
- **AND** an error message SHALL be displayed when the field is not empty

#### Scenario: Name is normalized before saving
- **WHEN** user enters a name with leading or trailing whitespace
- **THEN** the normalized trimmed name SHALL be used

#### Scenario: Color can be selected
- **WHEN** user taps a color option
- **THEN** that color SHALL be visually marked as selected
- **AND** it SHALL be used when the tag is saved

#### Scenario: Default color is used
- **WHEN** user does not select a color
- **THEN** the default tag color SHALL be used

#### Scenario: Cancel closes without creating
- **WHEN** user cancels the dialog
- **THEN** no tag SHALL be created

### Requirement: Recording card displays tags
The recording card SHALL display the tags associated with each recording.

#### Scenario: Tags are displayed as badges
- **WHEN** a recording has associated tags
- **THEN** the card SHALL display a badge for each tag below the recording metadata

#### Scenario: Untagged recording omits badges
- **WHEN** a recording has no associated tags
- **THEN** the card SHALL not display a tag badge area

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
