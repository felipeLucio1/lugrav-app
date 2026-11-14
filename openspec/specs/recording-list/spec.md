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
