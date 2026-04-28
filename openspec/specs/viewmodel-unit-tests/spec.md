## ADDED Requirements

### Requirement: Test audio recording state
The ViewModel SHALL set `isRecording` to true when `startRecording()` is called.

#### Scenario: Start recording sets isRecording to true
- **WHEN** `startRecording()` is called
- **THEN** `isRecording` state SHALL be true

#### Scenario: Recording time increments during recording
- **WHEN** recording is active and time passes
- **THEN** `recordingTimeSeconds` SHALL increment each second

#### Scenario: Failed recording sets error state
- **WHEN** `startRecording()` fails with exception
- **THEN** `recordingSuccess` SHALL contain a failure Result

### Requirement: Test audio playback state
The ViewModel SHALL manage playback state correctly when playing, pausing, resuming, and stopping audio.

#### Scenario: Play audio sets isPlaying to true
- **WHEN** `playAudio(filePath)` is called
- **THEN** `isPlaying` SHALL be true and `currentPlayingPath` SHALL be the file path

#### Scenario: Stop audio resets playing state
- **WHEN** `stopAudio()` is called while audio is playing
- **THEN** `isPlaying` SHALL be false, `currentPlayingPath` SHALL be null, and `playbackTimeFormatted` SHALL be "00:00:00"

#### Scenario: Pause audio sets isPlaying to false
- **WHEN** `pauseAudio()` is called while audio is playing
- **THEN** `isPlaying` SHALL be false

#### Scenario: Resume audio sets isPlaying to true
- **WHEN** `resumeAudio()` is called after pausing
- **THEN** `isPlaying` SHALL be true

#### Scenario: Playback completion resets state
- **WHEN** audio playback completes (onCompletion callback)
- **THEN** `isPlaying` SHALL be false and `currentPlayingPath` SHALL be null

### Requirement: Test toggle play/pause behavior
The ViewModel SHALL toggle between play and pause correctly based on current state and audio path.

#### Scenario: Toggle play/pause when same audio is paused
- **WHEN** `togglePlayPause(filePath)` is called with same audio that is paused at position > 0
- **THEN** audio SHALL resume

#### Scenario: Toggle play/pause when different audio is playing
- **WHEN** `togglePlayPause(newFilePath)` is called while different audio is playing
- **THEN** current audio SHALL stop and new audio SHALL play

#### Scenario: Toggle play/pause when no audio is playing
- **WHEN** `togglePlayPause(filePath)` is called and no audio is playing
- **THEN** audio SHALL start playing

### Requirement: Test audio deletion behavior
The ViewModel SHALL stop playback and reload the recordings list when deleting an audio file.

#### Scenario: Delete playing audio stops playback
- **WHEN** `deleteRecording(filePath)` is called while that audio is playing
- **THEN** `repository.stopAudio()` SHALL be called, `isPlaying` SHALL be false, and `currentPlayingPath` SHALL be null

#### Scenario: Delete audio reloads recordings list
- **WHEN** `deleteRecording(filePath)` completes successfully
- **THEN** `loadRecordings()` SHALL be called and `deleteResult` SHALL be success

#### Scenario: Failed deletion sets error state
- **WHEN** `deleteRecording(filePath)` fails with exception
- **THEN** `deleteResult` SHALL contain a failure Result

### Requirement: Test recording and playback interaction
The ViewModel SHALL stop audio playback when starting a new recording.

#### Scenario: Start recording stops current playback
- **WHEN** `startRecording()` is called while audio is playing
- **THEN** `repository.stopAudio()` SHALL be called

#### Scenario: Play other audio stops current playback
- **WHEN** `playAudio(newPath)` is called while another audio is playing
- **THEN** current audio SHALL stop before playing new audio

### Requirement: Test finalize recording behavior
The ViewModel SHALL reset recording state and reload list when finalizing a recording.

#### Scenario: Finalize recording resets state and reloads list
- **WHEN** `finalizeRecording()` completes successfully
- **THEN** `isRecording` SHALL be false, `recordingTimeSeconds` SHALL be 0, `recordingSuccess` SHALL be success, and `loadRecordings()` SHALL be called

#### Scenario: Failed finalize resets state and sets error
- **WHEN** `finalizeRecording()` fails with exception
- **THEN** `isRecording` SHALL be false, `recordingTimeSeconds` SHALL be 0, and `recordingSuccess` SHALL contain a failure Result

### Requirement: Test ViewModel cleanup
The ViewModel SHALL stop audio playback when cleared.

#### Scenario: onCleared stops audio
- **WHEN** `onCleared()` is called
- **THEN** `stopAudio()` SHALL be called

### Requirement: Test clear selected audio
The ViewModel SHALL clear the selected audio path when requested.

#### Scenario: Clear selected audio sets path to null
- **WHEN** `clearSelectedAudio()` is called
- **THEN** `selectedAudioPath` SHALL be null

### Requirement: Test clear delete result
The ViewModel SHALL allow clearing the delete result state.

#### Scenario: Clear delete result sets to null
- **WHEN** `clearDeleteResult()` is called
- **THEN** `deleteResult` SHALL be null
