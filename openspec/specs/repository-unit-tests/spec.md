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

#### Scenario: Get recordings list includes only aac files
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

### Requirement: Test metadata retriever lifecycle
The repository tests SHALL verify retriever cleanup and multiple extractions.

#### Scenario: Release after each file
- **WHEN** `getRecordingsList()` processes multiple files
- **THEN** `setDataSource()`, `extractDuration()`, and `release()` SHALL be called for each file

#### Scenario: Release when extraction fails
- **WHEN** `extractDuration()` throws an exception
- **THEN** `release()` SHALL still be called

#### Scenario: Release when data source fails
- **WHEN** `setDataSource()` throws an exception
- **THEN** `release()` SHALL still be called
- **AND** the recording duration SHALL be `"00:00:00"`

#### Scenario: Null duration
- **WHEN** `extractDuration()` returns null
- **THEN** the recording duration SHALL be `"00:00:00"`

#### Scenario: Duration formatting
- **WHEN** `extractDuration()` returns 65000ms
- **THEN** the recording duration SHALL be `"00:01:05"`

### Requirement: Test renameRecording behavior
The repository SHALL rename recording files while validating names and preserving content.

#### Scenario: Rename valid recording
- **WHEN** `renameRecording(filePath, newName)` is called with a valid new name
- **THEN** the file SHALL be renamed in the same directory
- **AND** the returned path SHALL end with `.aac`

#### Scenario: Rename preserves file content
- **WHEN** `renameRecording(filePath, newName)` succeeds
- **THEN** the renamed file SHALL contain the same bytes as the original file

#### Scenario: Rename appends extension
- **WHEN** `renameRecording(filePath, "Novo nome")` is called
- **THEN** the resulting file name SHALL be `Novo nome.aac`

#### Scenario: Rename does not duplicate extension
- **WHEN** `renameRecording(filePath, "Novo nome.aac")` is called
- **THEN** the resulting file name SHALL be `Novo nome.aac`
- **AND** it SHALL NOT be `Novo nome.aac.aac`

#### Scenario: Rename to same name is a no-op
- **WHEN** the normalized new name equals the current name
- **THEN** the original path SHALL be returned
- **AND** the file SHALL remain unchanged

#### Scenario: Rename with empty name throws IOException
- **WHEN** `renameRecording(filePath, "")` is called
- **THEN** an `IOException` SHALL be thrown

#### Scenario: Rename with invalid characters throws IOException
- **WHEN** `renameRecording(filePath, "nome/invalido")` is called
- **THEN** an `IOException` SHALL be thrown

#### Scenario: Rename to existing name throws IOException
- **WHEN** the target file already exists
- **THEN** an `IOException` SHALL be thrown

#### Scenario: Rename non-existent file throws IOException
- **WHEN** `renameRecording(filePath, newName)` is called and the source file does not exist
- **THEN** an `IOException` SHALL be thrown

### Requirement: Test recording timestamp and ordering
The repository tests SHALL verify that recordings use `lastModified()` and are ordered newest first.

#### Scenario: Timestamp comes from last modified
- **WHEN** `getRecordingsList()` processes a file with a known `lastModified()` value
- **THEN** the returned model SHALL contain that value in `createdAtMillis`

#### Scenario: Newest recording comes first
- **WHEN** `getRecordingsList()` processes files with different `lastModified()` values
- **THEN** the returned list SHALL be ordered by `createdAtMillis` descending

#### Scenario: Equal timestamps are ordered by path
- **WHEN** two files have the same `lastModified()` value
- **THEN** the returned list SHALL be ordered by `path` ascending

### Requirement: Test recording synchronization
The repository tests SHALL verify that files are synchronized with the store.

#### Scenario: New file is synced
- **WHEN** `getRecordingsList()` finds an `.aac` file
- **THEN** `RecordingTagStore.syncRecordings()` SHALL receive its metadata

#### Scenario: Existing file metadata is synced
- **WHEN** a file's duration or timestamp changes
- **THEN** the synchronized metadata SHALL contain the new values

#### Scenario: Missing file is removed
- **WHEN** a stored recording has no corresponding file
- **THEN** synchronization SHALL remove it

#### Scenario: Tags are returned with recordings
- **WHEN** `getRecordingsList()` returns a recording with tags
- **THEN** the model SHALL contain those tags

### Requirement: Test tag persistence
The repository tests SHALL verify tag creation, update, listing and deletion.

#### Scenario: Create tag persists normalized tag
- **WHEN** `createTag("  Trabalho  ", "#29b6f6")` is called
- **THEN** the store SHALL receive `Trabalho` with color `#29B6F6`

#### Scenario: Duplicate tag name fails
- **WHEN** `createTag()` is called with an existing name
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: Duplicate tag name fails case-insensitively
- **WHEN** `createTag()` is called with a name that differs only by case from an existing tag
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: Invalid color fails
- **WHEN** `createTag()` is called with an invalid color
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: List tags returns stored tags
- **WHEN** `getTags()` is called
- **THEN** all stored tags SHALL be returned

#### Scenario: Update tag persists changes
- **WHEN** `updateTag()` is called with valid values
- **THEN** the store SHALL contain the updated tag

#### Scenario: Delete tag removes it
- **WHEN** `deleteTag()` is called
- **THEN** the store SHALL delete the tag

### Requirement: Test recording-tag association
The repository tests SHALL verify tag replacement.

#### Scenario: Replace tags on recording
- **WHEN** `setRecordingTags(path, tagNames)` is called
- **THEN** the store SHALL receive the complete association set

#### Scenario: Clear tags on recording
- **WHEN** `setRecordingTags(path, emptySet())` is called
- **THEN** the store SHALL remove all associations

#### Scenario: Unknown tag keeps associations unchanged
- **WHEN** `setRecordingTags()` receives an unknown tag
- **THEN** the operation SHALL fail
- **AND** the existing associations SHALL remain unchanged

#### Scenario: Tag names are normalized before replacement
- **WHEN** `setRecordingTags()` receives a name with surrounding whitespace
- **THEN** the store SHALL receive the trimmed name

### Requirement: Test tag filtering
The repository tests SHALL verify filtering by one or more tags.

#### Scenario: Filter by one tag
- **WHEN** `getRecordingsList(setOf("Trabalho"))` is called
- **THEN** only recordings associated with `Trabalho` SHALL be returned

#### Scenario: Filter by multiple tags requires all
- **WHEN** `getRecordingsList(setOf("Trabalho", "Importante"))` is called
- **THEN** only recordings associated with both tags SHALL be returned

#### Scenario: Filter by unknown tag returns empty
- **WHEN** `getRecordingsList(setOf("Inexistente"))` is called
- **THEN** an empty list SHALL be returned

#### Scenario: Filter by tags without resyncing files
- **WHEN** `getRecordingsByTags()` is called
- **THEN** the store SHALL not receive a new synchronization call

### Requirement: Test suspend recording operations
The repository tests SHALL invoke database-backed recording operations from coroutines.

#### Scenario: Save persists recording
- **WHEN** `stopAndSave()` completes
- **THEN** the store SHALL receive the saved recording

#### Scenario: Rename updates path
- **WHEN** `renameRecording()` succeeds
- **THEN** the store SHALL receive the old and new paths

#### Scenario: Delete removes recording
- **WHEN** `deleteRecording()` succeeds
- **THEN** the store SHALL receive the deleted path
