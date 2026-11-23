## Purpose

Define o domínio, a persistência, as associações, os filtros e a sincronização entre tags e gravações.

## Requirements

### Requirement: User tag domain model
The system SHALL represent a user tag with a unique name and a color in `#RRGGBB` format.

#### Scenario: Tag contains name and color
- **WHEN** a tag is created
- **THEN** it SHALL expose `name` and `colorHex`

#### Scenario: Tag name is normalized
- **WHEN** a tag name contains leading or trailing whitespace
- **THEN** the stored name SHALL be trimmed

#### Scenario: Blank tag name is rejected
- **WHEN** a tag name is empty or contains only whitespace
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: Long tag name is rejected
- **WHEN** a tag name has more than 30 characters after trimming
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: Control characters are rejected
- **WHEN** a tag name contains line breaks or other ISO control characters
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: Duplicate tag name is rejected case-insensitively
- **WHEN** a tag is created with a name that differs only by letter case from an existing tag
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: Invalid tag color is rejected
- **WHEN** `colorHex` does not match `#RRGGBB`
- **THEN** the operation SHALL fail with `IllegalArgumentException`

#### Scenario: Tag color is normalized
- **WHEN** a valid lowercase color such as `#ff0000` is provided
- **THEN** the stored color SHALL be uppercase, such as `#FF0000`

### Requirement: Tag persistence
The system SHALL persist tags in the local Room database with an auto-generated integer ID and a unique name.

#### Scenario: Tag is inserted
- **WHEN** a valid tag is created
- **THEN** a `TagEntity` SHALL be inserted
- **AND** the stored name SHALL be unique

#### Scenario: Tags are listed alphabetically
- **WHEN** `getTags()` is called
- **THEN** tags SHALL be returned ordered by name ascending

#### Scenario: Tag can be renamed
- **WHEN** a tag is updated with a new valid name and color
- **THEN** the stored tag SHALL contain the new values
- **AND** its recording associations SHALL be preserved

#### Scenario: Tag deletion removes associations
- **WHEN** a tag is deleted
- **THEN** its rows in `recording_tags` SHALL be removed by cascade

### Requirement: Recording metadata persistence
The system SHALL persist each recording's path, formatted duration and creation timestamp.

#### Scenario: Recording is persisted after save
- **WHEN** `stopAndSave()` completes
- **THEN** the recording path, duration and `createdAtMillis` SHALL be stored

#### Scenario: Recording path is unique
- **WHEN** a recording with an existing path is synchronized
- **THEN** the existing row SHALL be updated instead of duplicated

#### Scenario: Rename preserves associations
- **WHEN** a recording is renamed
- **THEN** its stored path SHALL be updated
- **AND** its tag associations SHALL remain attached to the same recording

#### Scenario: Deleted recording removes associations
- **WHEN** a recording is deleted
- **THEN** its rows in `recording_tags` SHALL be removed by cascade

### Requirement: Recording-tag association
The system SHALL persist a many-to-many relationship between recordings and tags using a cross-reference table.

#### Scenario: Recording tags are replaced atomically
- **WHEN** `setRecordingTags(recordingPath, tagNames)` is called
- **THEN** all existing associations for the recording SHALL be replaced by the provided tags
- **AND** the replacement SHALL run in a single write transaction

#### Scenario: Empty set clears associations
- **WHEN** `setRecordingTags(recordingPath, emptySet())` is called
- **THEN** all associations for the recording SHALL be removed

#### Scenario: Unknown tag aborts replacement
- **WHEN** `setRecordingTags()` receives a tag name that does not exist
- **THEN** the operation SHALL fail with `IllegalArgumentException`
- **AND** existing associations SHALL remain unchanged

#### Scenario: Association is idempotent
- **WHEN** the same complete tag set is applied more than once to the same recording
- **THEN** only one association per tag SHALL exist

### Requirement: Recording with tags read model
The system SHALL expose a read model that contains a recording and its associated tags.

#### Scenario: Recording with tags is queried
- **WHEN** recordings are loaded
- **THEN** each result SHALL contain its recording data and associated tags
- **AND** the query SHALL run in a transaction

#### Scenario: Recording without tags returns empty list
- **WHEN** a recording has no associations
- **THEN** its tags list SHALL be empty

### Requirement: Tag filtering
The system SHALL filter recordings by one or more tag names using AND semantics.

#### Scenario: No tag filter returns all recordings
- **WHEN** recordings are queried with an empty tag set
- **THEN** all stored recordings SHALL be returned

#### Scenario: Single tag filter
- **WHEN** recordings are queried with one tag name
- **THEN** only recordings associated with that tag SHALL be returned

#### Scenario: Multiple tag filter requires all tags
- **WHEN** recordings are queried with multiple tag names
- **THEN** only recordings associated with every selected tag SHALL be returned

#### Scenario: Unknown tag filter returns empty list
- **WHEN** recordings are queried with a tag name that does not exist
- **THEN** the result SHALL be empty

### Requirement: Data layer isolation
The system SHALL keep Room implementation details out of the domain model, ViewModel and repository.

#### Scenario: Repository depends on store interface
- **WHEN** the repository is constructed
- **THEN** it SHALL receive a `RecordingTagStore`
- **AND** it SHALL NOT import `androidx.room3` or Room entities

#### Scenario: ViewModel depends on repository
- **WHEN** the ViewModel loads or mutates recordings or tags
- **THEN** it SHALL use `AudioRecordingRepository` only

### Requirement: File and database synchronization
The system SHALL reconcile the Room database with the `.aac` files on every recordings listing.

#### Scenario: New file is backfilled
- **WHEN** `getRecordingsList()` finds an `.aac` file that is not in the database
- **THEN** a recording row SHALL be inserted

#### Scenario: Existing file is updated
- **WHEN** an existing recording's duration or timestamp changes
- **THEN** its row SHALL be updated

#### Scenario: Missing file is removed
- **WHEN** a recording row exists for a file that no longer exists
- **THEN** the row SHALL be deleted

#### Scenario: Tags survive synchronization
- **WHEN** an existing recording is synchronized
- **THEN** its tag associations SHALL be preserved

### Requirement: Atomic tag deletion
The system SHALL delete a tag and all of its recording associations in a single atomic database transaction.

#### Scenario: Deleting an associated tag
- **WHEN** a tag associated with one or more recordings is deleted
- **THEN** the tag record is removed
- **AND** all cross-reference rows for that tag are removed
- **AND** the recordings remain stored and accessible without that tag

#### Scenario: Transaction failure
- **WHEN** an error occurs while deleting the tag or its associations
- **THEN** the transaction is rolled back
- **AND** neither the tag nor its associations are partially removed

### Requirement: Tag deletion result
The ViewModel SHALL report the normalized name of the deleted tag on success.

#### Scenario: Successful deletion result
- **WHEN** the ViewModel deletes a tag named `  Trabalho  `
- **THEN** the deletion result is successful
- **AND** the result contains the normalized name `Trabalho`
- **AND** the active filter for that tag is cleared
- **AND** tags and recordings are refreshed

#### Scenario: Failed deletion result
- **WHEN** the tag deletion operation fails
- **THEN** the deletion result is a failure
- **AND** the tag list and recording list are not updated as if the deletion succeeded

### Requirement: Tags assigned during recording review
O sistema SHALL permitir que as tags de uma nova gravação sejam definidas durante a revisão pós-gravação, substituindo o conjunto de associações da gravação pelas tags selecionadas.

#### Scenario: Tags selecionadas na revisão
- **WHEN** o usuário salva a revisão pós-gravação com tags selecionadas
- **THEN** a gravação fica associada exatamente a essas tags
- **AND** as associações anteriores da gravação são substituídas

#### Scenario: Revisão sem tags
- **WHEN** o usuário salva a revisão pós-gravação sem selecionar tags
- **THEN** a gravação permanece sem associações de tags

#### Scenario: Falha ao persistir tags
- **WHEN** a persistência das tags selecionadas falha durante o salvamento da revisão
- **THEN** o arquivo permanece com o nome anterior
- **AND** as associações de tags anteriores são preservadas
- **AND** uma mensagem de erro é apresentada
