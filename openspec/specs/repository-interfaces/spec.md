## ADDED Requirements

### Requirement: AudioRecorder interface extraction
The system SHALL extract `AudioRecorder` interface from `MediaRecorder` usage with methods: `setup()`, `prepare()`, `start()`, `stop()`, `release()`.

#### Scenario: AudioRecorderImpl delegates to MediaRecorder
- **WHEN** `AudioRecorderImpl.setup()` is called with audio parameters
- **THEN** `MediaRecorder.setAudioSource()`, `setOutputFormat()`, `setAudioEncoder()`, `setAudioSamplingRate()`, `setAudioEncodingBitRate()`, `setAudioChannels()`, `setOutputFile()` are called

#### Scenario: AudioRecorderImpl creates MediaRecorder conditionally
- **WHEN** SDK version is >= Build.VERSION_CODES.S
- **THEN** `MediaRecorder(context)` constructor is used

#### Scenario: AudioRecorderImpl starts recording
- **WHEN** `AudioRecorderImpl.start()` is called
- **THEN** `MediaRecorder.start()` is called

#### Scenario: AudioRecorderImpl stops recording
- **WHEN** `AudioRecorderImpl.stop()` is called
- **THEN** `MediaRecorder.stop()` and `release()` are called

### Requirement: AudioPlayer interface extraction
The system SHALL extract `AudioPlayer` interface from `MediaPlayer` usage with methods: `setDataSource()`, `prepare()`, `start()`, `pause()`, `stop()`, `release()`, `setOnCompletionListener()`, `getCurrentPosition()`, `getDuration()`.

#### Scenario: AudioPlayerImpl configures MediaPlayer
- **WHEN** `AudioPlayerImpl.setDataSource(path)` is called
- **THEN** `MediaPlayer.setDataSource(path)` is called

#### Scenario: AudioPlayerImpl handles completion
- **WHEN** `AudioPlayerImpl.setOnCompletionListener(listener)` is called and playback completes
- **THEN** the listener callback is invoked and `MediaPlayer.release()` is called

#### Scenario: AudioPlayerImpl pauses and resumes
- **WHEN** `AudioPlayerImpl.pause()` is called
- **THEN** `MediaPlayer.pause()` is called
- **WHEN** `AudioPlayerImpl.start()` is called  
- **THEN** `MediaPlayer.start()` is called

### Requirement: MetadataRetriever interface extraction
The system SHALL extract `MetadataRetriever` interface from `MediaMetadataRetriever` usage with methods: `setDataSource()`, `extractDuration()`, `release()`.

#### Scenario: MetadataRetrieverImpl extracts duration
- **WHEN** `MetadataRetrieverImpl.setDataSource(path)` and `extractDuration()` are called
- **THEN** returns duration in milliseconds from `MediaMetadataRetriever.extractMetadata(METADATA_KEY_DURATION)`

#### Scenario: MetadataRetrieverImpl handles errors
- **WHEN** `extractDuration()` encounters an exception
- **THEN** returns null

### Requirement: FileProvider interface extraction
The system SHALL extract `FileProvider` interface from `Context` usage with methods: `getCacheDir()`, `getExternalFilesDir()`.

#### Scenario: FileProviderImpl returns cache directory
- **WHEN** `FileProviderImpl.getCacheDir()` is called
- **THEN** returns `context.cacheDir`

#### Scenario: FileProviderImpl returns external files directory
- **WHEN** `FileProviderImpl.getExternalFilesDir()` is called
- **THEN** returns `context.getExternalFilesDir(null)`

### Requirement: Repository depends on interfaces
The `AudioRecordingRepository` SHALL depend on `AudioRecorder`, `AudioPlayer`, `MetadataRetriever`, and `FileProvider` interfaces via constructor injection instead of concrete Android classes.

#### Scenario: Repository uses AudioRecorder for recording
- **WHEN** `startRecording()` is called
- **THEN** `audioRecorder.setup()`, `prepare()`, `start()` are called with correct parameters

#### Scenario: Repository uses AudioPlayer for playback
- **WHEN** `playAudio(filePath, onCompletion)` is called
- **THEN** `audioPlayer.setDataSource(filePath)`, `prepare()`, `start()` are called

#### Scenario: Repository uses FileProvider for file operations
- **WHEN** `stopAndSave()` is called
- **THEN** `fileProvider.getExternalFilesDir()` is used to get base directory

#### Scenario: Repository uses MetadataRetriever for duration
- **WHEN** `getAudioDuration(filePath)` is called
- **THEN** `metadataRetriever.setDataSource(filePath)` and `extractDuration()` are called

### Requirement: Business logic preservation
The system SHALL preserve 100% of original business logic including: file naming (`lugrav_YYYYMMdd_HHmmss.aac`), duration formatting (`HH:mm:ss`), error handling in `getAudioDuration()`, and `AudioRecordingModel` creation.

#### Scenario: Stop and save creates correct filename
- **WHEN** `stopAndSave()` is called
- **THEN** file is saved with pattern `lugrav_YYYYMMdd_HHmmss.aac` in `lugrav` folder

#### Scenario: Duration formatting is preserved
- **WHEN** `formatDuration(durationMs)` is called with 65000ms
- **THEN** returns "00:01:05"

#### Scenario: Audio duration handles errors
- **WHEN** `getAudioDuration(filePath)` encounters an exception
- **THEN** returns "00:00:00"

### Requirement: MetadataRetriever lifecycle
The system SHALL create a fresh `MediaMetadataRetriever` for each data source and release it when extraction is complete.

#### Scenario: New retriever per data source
- **WHEN** `MetadataRetrieverImpl.setDataSource(path)` is called
- **THEN** a new `MediaMetadataRetriever` SHALL be created
- **AND** any previous instance SHALL be released

#### Scenario: Release is idempotent
- **WHEN** `MetadataRetrieverImpl.release()` is called
- **THEN** the current `MediaMetadataRetriever` SHALL be released
- **AND** the internal reference SHALL be cleared
- **AND** subsequent `release()` calls SHALL be safe

#### Scenario: Data source failure releases new instance
- **WHEN** `setDataSource()` throws an exception
- **THEN** the newly created `MediaMetadataRetriever` SHALL be released
- **AND** the exception SHALL be propagated

#### Scenario: Duration extraction failure returns null
- **WHEN** `extractDuration()` encounters an exception
- **THEN** it SHALL return null

#### Scenario: Repository releases retriever after extraction
- **WHEN** `getAudioDuration(filePath)` is called
- **THEN** `metadataRetriever.release()` SHALL be called even if `setDataSource()` or `extractDuration()` fails

#### Scenario: Duration extraction is serialized
- **WHEN** multiple duration extractions run concurrently
- **THEN** the repository SHALL serialize access to the `MetadataRetriever`

### Requirement: Repository persists recording metadata
The `AudioRecordingRepository` SHALL persist recording metadata through `RecordingTagStore`.

#### Scenario: Saved recording is persisted
- **WHEN** `stopAndSave()` completes
- **THEN** the repository SHALL store the recording path, duration and timestamp

#### Scenario: Renamed recording updates path
- **WHEN** `renameRecording()` succeeds
- **THEN** the repository SHALL update the stored path
- **AND** tag associations SHALL remain attached to the same recording

#### Scenario: Deleted recording is removed from database
- **WHEN** `deleteRecording()` succeeds
- **THEN** the repository SHALL remove the stored recording

### Requirement: Repository synchronizes files
The `AudioRecordingRepository` SHALL synchronize the database with `.aac` files when listing recordings.

#### Scenario: Listing backfills new files
- **WHEN** `getRecordingsList()` finds a new `.aac` file
- **THEN** it SHALL insert the recording metadata

#### Scenario: Listing removes stale rows
- **WHEN** `getRecordingsList()` finds no file for a stored recording
- **THEN** it SHALL delete the stored recording

#### Scenario: Listing returns tags
- **WHEN** `getRecordingsList()` returns recordings
- **THEN** each model SHALL include its associated tags

### Requirement: Repository recording operations are suspend
Recording operations that touch the database SHALL be `suspend`.

#### Scenario: Save is suspend
- **WHEN** `stopAndSave()` is called
- **THEN** it SHALL be invoked from a coroutine

#### Scenario: Rename is suspend
- **WHEN** `renameRecording()` is called
- **THEN** it SHALL be invoked from a coroutine

#### Scenario: Delete is suspend
- **WHEN** `deleteRecording()` is called
- **THEN** it SHALL be invoked from a coroutine

### Requirement: Repository tag operations
The `AudioRecordingRepository` SHALL expose tag operations.

#### Scenario: Create tag
- **WHEN** `createTag(name, colorHex)` is called with valid values
- **THEN** the tag name SHALL be trimmed
- **AND** the color SHALL be normalized to uppercase
- **AND** the tag SHALL be persisted and returned

#### Scenario: Create duplicate tag fails case-insensitively
- **WHEN** `createTag()` is called with a name that matches an existing tag ignoring case
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: List tags
- **WHEN** `getTags()` is called
- **THEN** all stored tags SHALL be returned ordered by name ascending

#### Scenario: Update tag
- **WHEN** `updateTag(currentName, newName, colorHex)` is called
- **THEN** the stored tag SHALL be updated with normalized values
- **AND** existing recording associations SHALL be preserved

#### Scenario: Delete tag
- **WHEN** `deleteTag(name)` is called
- **THEN** the tag and its associations SHALL be removed

#### Scenario: Replace recording tags
- **WHEN** `setRecordingTags(recordingPath, tagNames)` is called
- **THEN** tag names SHALL be validated and normalized
- **AND** the recording associations SHALL be replaced by the provided tags
- **AND** an empty set SHALL remove all associations

#### Scenario: Unknown tag aborts replacement
- **WHEN** `setRecordingTags()` receives a tag name that does not exist
- **THEN** the operation SHALL fail with `IllegalArgumentException`
- **AND** existing associations SHALL remain unchanged

### Requirement: Repository filters recordings by tags
The repository SHALL filter recordings by one or more tags using AND semantics.

#### Scenario: List without filters
- **WHEN** `getRecordingsList()` is called without tags
- **THEN** all recordings SHALL be returned

#### Scenario: List with one tag
- **WHEN** `getRecordingsList(tagNames)` receives one tag
- **THEN** only recordings associated with that tag SHALL be returned

#### Scenario: List with multiple tags
- **WHEN** `getRecordingsList(tagNames)` receives multiple tags
- **THEN** only recordings associated with every selected tag SHALL be returned

#### Scenario: Filter without file synchronization
- **WHEN** `getRecordingsByTags(tagNames)` is called
- **THEN** it SHALL filter the stored recordings without scanning or synchronizing files

### Requirement: Repository isolation
The repository SHALL depend on `RecordingTagStore` instead of Room database classes or entities.

#### Scenario: No Room dependency in repository
- **WHEN** `AudioRecordingRepository` is compiled
- **THEN** it SHALL NOT reference `androidx.room3` types
