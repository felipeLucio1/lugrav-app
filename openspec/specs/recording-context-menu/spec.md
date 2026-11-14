## ADDED Requirements

### Requirement: Context menu activation via long press
The system SHALL activate context menu mode when user long presses a recording card in the list.

#### Scenario: Long press selects recording
- **WHEN** user long presses on a RecordingCard
- **THEN** the card SHALL be marked as selected (visual indicator)
- **AND** contextual AppBar mode SHALL be activated

### Requirement: Contextual AppBar mode
The system SHALL display a back arrow (ArrowBack) and a context menu (MoreVert) in the AppBar when a recording is selected.

#### Scenario: Back arrow is shown in selection mode
- **WHEN** a recording is selected
- **THEN** ArrowBack navigation icon SHALL be displayed in LugravTopBar
- **AND** tapping it SHALL clear the selection

#### Scenario: Context menu is shown in selection mode
- **WHEN** a recording is selected
- **THEN** MoreVert action SHALL be displayed in LugravTopBar
- **AND** tapping it SHALL open a dropdown with Share, Rename, Select tags, and Delete actions

### Requirement: Share recording
The system SHALL allow sharing a selected recording via Android share intent.

#### Scenario: Share opens chooser
- **WHEN** user selects Share from the context menu
- **THEN** a share chooser SHALL open with the audio file attached
- **AND** selection mode SHALL be cleared after sharing

### Requirement: Delete recording with confirmation
The system SHALL require confirmation before deleting a recording.

#### Scenario: Delete shows confirmation dialog
- **WHEN** user selects Delete from the context menu
- **THEN** DeleteConfirmationDialog SHALL be displayed

#### Scenario: Confirm delete removes recording
- **WHEN** user confirms deletion
- **THEN** recording SHALL be deleted
- **AND** list SHALL be reloaded
- **AND** selection mode SHALL be cleared

#### Scenario: Cancel delete keeps recording
- **WHEN** user cancels deletion
- **THEN** dialog SHALL be dismissed
- **AND** selection mode SHALL remain active

### Requirement: Exit context menu mode
The system SHALL allow exiting context menu mode.

#### Scenario: Tap selected card clears selection
- **WHEN** user taps an already selected card
- **THEN** selection SHALL be cleared
- **AND** contextual AppBar mode SHALL be deactivated

### Requirement: Normal click opens playback
The system SHALL preserve normal playback behavior for non-selected items.

#### Scenario: Tap non-selected card opens bottom sheet
- **WHEN** user taps a non-selected card
- **THEN** RecordingBottomSheet SHALL open for playback
- **AND** context menu mode SHALL not be activated

### Requirement: Select tags for a recording
The system SHALL allow selecting multiple tags for a recording from the context menu.

#### Scenario: Select tags action opens the dialog
- **WHEN** user selects "Selecionar tags" from the context menu
- **THEN** `SelectTagsDialog` SHALL be displayed for the selected recording

#### Scenario: Dialog shows all available tags
- **WHEN** the dialog opens and tags exist
- **THEN** it SHALL display a scrollable list with a checkbox for each tag
- **AND** tags already associated with the recording SHALL start checked

#### Scenario: Save is disabled without changes
- **WHEN** the selected tag set equals the recording's current tags
- **THEN** the save action SHALL be disabled

#### Scenario: User changes the selection
- **WHEN** user checks or unchecks tags
- **THEN** the save action SHALL be enabled

#### Scenario: Save replaces recording tags
- **WHEN** user saves a changed selection
- **THEN** `setRecordingTags` SHALL replace the recording's associations
- **AND** the dialog SHALL close
- **AND** on success the selection mode SHALL be cleared and a success message SHALL be shown

#### Scenario: Save failure keeps selection mode
- **WHEN** saving tags fails
- **THEN** an error message SHALL be shown
- **AND** the selected recording SHALL remain selected

#### Scenario: Cancel keeps selection mode
- **WHEN** user cancels the dialog
- **THEN** the dialog SHALL close
- **AND** the selected recording SHALL remain selected

#### Scenario: Empty tag list
- **WHEN** the dialog opens and no tags exist
- **THEN** it SHALL display an empty-state message

#### Scenario: Tags are loading
- **WHEN** the dialog opens while tags are loading
- **THEN** it SHALL display a loading indicator
