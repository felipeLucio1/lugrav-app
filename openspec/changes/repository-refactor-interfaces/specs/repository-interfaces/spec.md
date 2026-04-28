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
