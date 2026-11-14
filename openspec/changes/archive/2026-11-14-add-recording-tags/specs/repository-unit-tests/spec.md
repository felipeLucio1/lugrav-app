## ADDED Requirements

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
