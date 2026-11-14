## ADDED Requirements

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
