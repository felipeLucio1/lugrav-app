## ADDED Requirements

### Requirement: Rename action in context menu
The system SHALL provide a Rename action for the selected recording.

#### Scenario: Rename option is shown
- **WHEN** a recording is selected
- **THEN** the context menu SHALL include Share, Rename, and Delete actions
- **AND** the Rename label SHALL come from a dedicated string resource

### Requirement: Rename dialog
The system SHALL open a rename dialog with the current name selected.

#### Scenario: Dialog opens with current name selected
- **WHEN** user selects Rename from the context menu
- **THEN** `RenameRecordingDialog` SHALL be displayed
- **AND** the text field SHALL contain the current file name without `.aac`
- **AND** the current name SHALL be selected in the text field

#### Scenario: Cancel dismisses dialog
- **WHEN** user taps Cancelar
- **THEN** the dialog SHALL be dismissed
- **AND** the file name SHALL remain unchanged
- **AND** selection mode SHALL remain active

### Requirement: Rename validation
The system SHALL disable the Save button when the new name is empty, unchanged, or invalid.

#### Scenario: Empty name disables Save
- **WHEN** the text field is empty or contains only whitespace
- **THEN** the Save button SHALL be disabled

#### Scenario: Unchanged name disables Save
- **WHEN** the normalized new name is equal to the current name
- **THEN** the Save button SHALL be disabled

#### Scenario: Invalid name disables Save
- **WHEN** the new name contains `/`, `\`, `:`, `*`, `?`, `"`, `<`, `>`, `|`, control characters, is `.`/`..`, starts/ends with `.`, or exceeds 100 characters
- **THEN** the Save button SHALL be disabled
- **AND** an invalid-name supporting message SHALL be displayed when the field is not empty and the name differs from the current name

#### Scenario: Valid name enables Save
- **WHEN** the new name is non-empty, different from the current name, and satisfies all name rules
- **THEN** the Save button SHALL be enabled

### Requirement: Rename persistence
The system SHALL rename the selected recording file and reload the list.

#### Scenario: Save renames recording
- **WHEN** user enters a valid new name and taps Salvar
- **THEN** the file SHALL be renamed in the same directory with the `.aac` extension
- **AND** the file content SHALL be preserved
- **AND** the recordings list SHALL be reloaded
- **AND** selection mode SHALL be cleared
- **AND** a success snackbar SHALL be displayed

#### Scenario: Duplicate name is rejected
- **WHEN** a file with the target name already exists
- **THEN** the rename SHALL fail
- **AND** an error snackbar SHALL be displayed
- **AND** selection mode SHALL remain active

### Requirement: Rename playback interaction
The system SHALL keep playback state consistent when renaming the currently playing recording.

#### Scenario: Renaming current playing file stops playback
- **WHEN** user renames the recording that is currently playing
- **THEN** playback SHALL be stopped before the rename
- **AND** `isPlaying` SHALL be false
- **AND** `currentPlayingPath` SHALL be null

#### Scenario: Renaming another recording does not stop playback
- **WHEN** user renames a recording different from the one currently playing
- **THEN** the current playback SHALL continue

### Requirement: Date display after rename
The system SHALL preserve a useful date display for renamed recordings.

#### Scenario: Renamed recording displays date from last modified
- **WHEN** a recording file name does not match `lugrav_yyyyMMdd_HHmmss`
- **THEN** the card SHALL display the date derived from `File.lastModified()`
