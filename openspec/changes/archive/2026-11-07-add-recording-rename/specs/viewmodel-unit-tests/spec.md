## ADDED Requirements

### Requirement: Test rename recording behavior
The ViewModel SHALL expose rename results and keep playback state consistent during rename.

#### Scenario: Successful rename sets success result
- **WHEN** `renameRecording(filePath, newName)` succeeds
- **THEN** `renameResult` SHALL contain a success Result
- **AND** `loadRecordings()` SHALL be called

#### Scenario: Failed rename sets failure result
- **WHEN** `renameRecording(filePath, newName)` fails with an exception
- **THEN** `renameResult` SHALL contain a failure Result

#### Scenario: Clear rename result
- **WHEN** `clearRenameResult()` is called
- **THEN** `renameResult` SHALL be null

#### Scenario: Renaming current playing file stops playback
- **WHEN** `renameRecording(filePath, newName)` is called for the file in `currentPlayingPath`
- **THEN** `repository.stopAudio()` SHALL be called
- **AND** `isPlaying` SHALL be false
- **AND** `currentPlayingPath` SHALL be null

#### Scenario: Renaming another file does not stop playback
- **WHEN** `renameRecording(filePath, newName)` is called for a file different from `currentPlayingPath`
- **THEN** `repository.stopAudio()` SHALL NOT be called
