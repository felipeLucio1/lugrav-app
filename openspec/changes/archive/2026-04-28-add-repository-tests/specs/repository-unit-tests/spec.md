## ADDED Requirements

### Requirement: Test startRecording behavior
The repository SHALL correctly initialize and start audio recording with proper parameters.

#### Scenario: Start recording creates temp file in cache directory
- **WHEN** `startRecording()` is called
- **THEN** a temp file SHALL be created in the cache directory with `.aac` extension

#### Scenario: Start recording sets up audio recorder with correct parameters
- **WHEN** `startRecording()` is called
- **THEN** `audioRecorder.setup()` SHALL be called with parameters: MIC audio source, AAC_ADTS output format, AAC encoder, 44100Hz sampling rate, 128000 bitrate, 2 channels, and temp file path

#### Scenario: Start recording prepares and starts recorder
- **WHEN** `startRecording()` is called
- **THEN** `audioRecorder.prepare()` and `audioRecorder.start()` SHALL be called in order

### Requirement: Test stopRecording behavior
The repository SHALL stop recording, read audio bytes, and clean up temp file.

#### Scenario: Stop recording stops and releases audio recorder
- **WHEN** `stopRecording()` is called
- **THEN** `audioRecorder.stop()` and `audioRecorder.release()` SHALL be called in order

#### Scenario: Stop recording reads bytes from temp file
- **WHEN** `stopRecording()` is called and temp file exists
- **THEN** audio bytes SHALL be read from the temp file using FileInputStream

#### Scenario: Stop recording deletes temp file after reading
- **WHEN** `stopRecording()` is called and temp file exists
- **THEN** the temp file SHALL be deleted after reading bytes

#### Scenario: Stop recording with null temp file returns empty byte array
- **WHEN** `stopRecording()` is called and `tempFile` is null
- **THEN** an empty byte array SHALL be returned

### Requirement: Test stopAndSave behavior
The repository SHALL save recorded audio to external storage with proper naming.

#### Scenario: Stop and save creates app folder if not exists
- **WHEN** `stopAndSave()` is called and app folder doesn't exist
- **THEN** `fileProvider.getExternalFilesDir()` SHALL be called and app folder SHALL be created with `mkdirs()`

#### Scenario: Stop and save generates file name with correct pattern
- **WHEN** `stopAndSave()` is called
- **THEN** the file name SHALL follow pattern `lugrav_YYYYMMDD_HHmmss.aac`

#### Scenario: Stop and save writes audio bytes to file
- **WHEN** `stopAndSave()` is called
- **THEN** audio bytes from `stopRecording()` SHALL be written to the output file

#### Scenario: Stop and save returns absolute path of saved file
- **WHEN** `stopAndSave()` is called
- **THEN** the absolute path of the saved file SHALL be returned

### Requirement: Test getRecordingsList behavior
The repository SHALL list all recorded audio files with their durations.

#### Scenario: Get recordings list returns empty list when folder not exists
- **WHEN** `getRecordingsList()` is called and app folder doesn't exist
- **THEN** an empty list SHALL be returned

#### Scenario: Get recordings list returns audio recording models
- **WHEN** `getRecordingsList()` is called and app folder contains `.aac` files
- **THEN** a list of `AudioRecordingModel` objects SHALL be returned with path and duration

#### Scenario: Get recordings list filters only aac files
- **WHEN** `getRecordingsList()` is called
- **THEN** only files with `.aac` extension SHALL be included in the list

#### Scenario: Get recordings list extracts duration for each file
- **WHEN** `getRecordingsList()` is called
- **THEN** `metadataRetriever.setDataSource()`, `extractDuration()`, and `release()` SHALL be called for each file

### Requirement: Test playAudio behavior
The repository SHALL play audio and handle playback completion.

#### Scenario: Play audio stops current playback before playing
- **WHEN** `playAudio(filePath)` is called
- **THEN** `audioPlayer.stop()` SHALL be called before setting new data source

#### Scenario: Play audio sets data source to file path
- **WHEN** `playAudio(filePath)` is called
- **THEN** `audioPlayer.setDataSource(filePath)` SHALL be called with the provided path

#### Scenario: Play audio prepares and starts player
- **WHEN** `playAudio(filePath)` is called
- **THEN** `audioPlayer.prepare()` and `audioPlayer.start()` SHALL be called in order

#### Scenario: Play audio sets completion listener
- **WHEN** `playAudio(filePath)` is called with onCompletion callback
- **THEN** `audioPlayer.setOnCompletionListener()` SHALL be called with the callback

### Requirement: Test pauseAudio behavior
The repository SHALL pause audio playback.

#### Scenario: Pause audio calls pause on audio player
- **WHEN** `pauseAudio()` is called
- **THEN** `audioPlayer.pause()` SHALL be called

### Requirement: Test resumeAudio behavior
The repository SHALL resume audio playback.

#### Scenario: Resume audio calls start on audio player
- **WHEN** `resumeAudio()` is called
- **THEN** `audioPlayer.start()` SHALL be called

### Requirement: Test stopAudio behavior
The repository SHALL stop audio playback and release resources.

#### Scenario: Stop audio stops and releases audio player
- **WHEN** `stopAudio()` is called
- **THEN** `audioPlayer.stop()` and `audioPlayer.release()` SHALL be called in order

### Requirement: Test deleteRecording behavior
The repository SHALL stop playback and delete the recording file.

#### Scenario: Delete recording stops audio playback first
- **WHEN** `deleteRecording(filePath)` is called
- **THEN** `stopAudio()` SHALL be called before attempting deletion

#### Scenario: Delete recording deletes the file
- **WHEN** `deleteRecording(filePath)` is called and file exists
- **THEN** the file SHALL be deleted

#### Scenario: Delete recording throws IOException when file not exists
- **WHEN** `deleteRecording(filePath)` is called and file doesn't exist
- **THEN** an `IOException` with message "File does not exist: [filePath]" SHALL be thrown

#### Scenario: Delete recording throws IOException when delete fails
- **WHEN** `deleteRecording(filePath)` is called and `file.delete()` returns false
- **THEN** an `IOException` with message "Failed to delete file: [filePath]" SHALL be thrown

### Requirement: Test getCurrentPosition and getDuration
The repository SHALL return current playback position and duration.

#### Scenario: Get current position returns position from player
- **WHEN** `getCurrentPosition()` is called
- **THEN** the value from `audioPlayer.getCurrentPosition()` SHALL be returned

#### Scenario: Get duration returns duration from player
- **WHEN** `getDuration()` is called
- **THEN** the value from `audioPlayer.getDuration()` SHALL be returned

### Requirement: Test error handling
The repository SHALL handle errors gracefully.

#### Scenario: Get audio duration failure returns default 00:00:00
- **WHEN** `getAudioDuration(filePath)` is called and an exception occurs
- **THEN** the string "00:00:00" SHALL be returned

#### Scenario: Get recordings list with null file array returns empty list
- **WHEN** `getRecordingsList()` is called and `listFiles()` returns null
- **THEN** an empty list SHALL be returned
