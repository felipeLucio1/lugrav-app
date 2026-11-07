## ADDED Requirements

### Requirement: Test initial recordings loading state
The ViewModel SHALL expose the initial recordings loading state and clear it when loading finishes.

#### Scenario: Initial loading state is true
- **WHEN** the ViewModel is created
- **THEN** `isLoadingRecordings` SHALL be `true`

#### Scenario: Successful load clears loading state
- **WHEN** `loadRecordings()` completes successfully
- **THEN** `isLoadingRecordings` SHALL be `false`
- **AND** `recordingsList` SHALL contain the loaded recordings

#### Scenario: Failed load clears loading state
- **WHEN** `getRecordingsList()` throws an exception
- **THEN** `isLoadingRecordings` SHALL be `false`
- **AND** the ViewModel SHALL remain usable
