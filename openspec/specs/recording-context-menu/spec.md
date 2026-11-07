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
- **AND** tapping it SHALL open a dropdown with Share, Rename, and Delete actions

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
