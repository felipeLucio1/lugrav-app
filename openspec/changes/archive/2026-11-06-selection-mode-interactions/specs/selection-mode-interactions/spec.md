## ADDED Requirements

### Requirement: Enter selection mode
The system SHALL enter selection mode when the user long presses a recording card.

#### Scenario: Long press selects recording
- **WHEN** user long presses a RecordingCard
- **THEN** selectedFilePath SHALL be set to the pressed recording path
- **AND** contextual AppBar mode SHALL be active

### Requirement: Change selection to another card
The system SHALL change the selected card when the user taps another card during selection mode.

#### Scenario: Tap another card changes selection
- **WHEN** selection mode is active and user taps a different RecordingCard
- **THEN** selectedFilePath SHALL be updated to the tapped card path
- **AND** no playback SHALL start
- **AND** RecordingBottomSheet SHALL NOT open

### Requirement: Exit selection mode via selected card
The system SHALL exit selection mode when the user taps the already selected card.

#### Scenario: Tap selected card clears selection
- **WHEN** selection mode is active and user taps the selected RecordingCard
- **THEN** selectedFilePath SHALL be null
- **AND** contextual AppBar mode SHALL be deactivated

### Requirement: Exit selection mode via back arrow
The system SHALL exit selection mode when the user taps the back arrow in the AppBar.

#### Scenario: Back arrow clears selection
- **WHEN** selection mode is active and user taps ArrowBack
- **THEN** selectedFilePath SHALL be null

### Requirement: Exit selection mode via system back
The system SHALL exit selection mode when the user presses the system back button.

#### Scenario: System back clears selection
- **WHEN** selection mode is active and user presses the system back button
- **THEN** selectedFilePath SHALL be null
- **AND** the app SHALL remain on the same screen

### Requirement: No playback during selection mode
The system SHALL NOT start playback or modify playback state while selection mode is active.

#### Scenario: Playback state unchanged in selection mode
- **WHEN** selection mode is active and user taps a RecordingCard
- **THEN** isPlaying, currentPlayingPath, and selectedRecording SHALL NOT change as a result of playback
