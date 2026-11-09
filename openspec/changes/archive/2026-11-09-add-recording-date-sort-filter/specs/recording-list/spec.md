## ADDED Requirements

### Requirement: Recording creation timestamp
The system SHALL populate each recording with a timestamp based on the file's `lastModified()` value.

#### Scenario: Valid file timestamp is used
- **WHEN** `getRecordingsList()` processes an existing file
- **THEN** `AudioRecordingModel.createdAtMillis` SHALL equal `File.lastModified()`

#### Scenario: Invalid file timestamp is normalized
- **WHEN** `File.lastModified()` returns `0` or a negative value
- **THEN** `AudioRecordingModel.createdAtMillis` SHALL be `0L`

### Requirement: Recordings sorted by creation timestamp
The system SHALL return recordings ordered by creation timestamp, newest first.

#### Scenario: Newest recording appears first
- **WHEN** `getRecordingsList()` returns multiple recordings
- **THEN** they SHALL be ordered by `createdAtMillis` descending

#### Scenario: Equal timestamps have deterministic order
- **WHEN** two recordings have the same `createdAtMillis`
- **THEN** they SHALL be ordered by `path` ascending

#### Scenario: Unknown timestamps appear last
- **WHEN** a recording has `createdAtMillis` equal to `0L`
- **THEN** it SHALL appear after recordings with positive timestamps

### Requirement: Extensible recording filters
The system SHALL support filtering recordings through a `RecordingFilter` abstraction.

#### Scenario: Default filter includes all recordings
- **WHEN** `getRecordingsList()` is called without a filter
- **THEN** all `.aac` recordings SHALL be returned

#### Scenario: Custom filter excludes non-matching recordings
- **WHEN** `getRecordingsList(filter)` is called with a custom filter
- **THEN** only recordings whose `matches()` returns `true` SHALL be returned

#### Scenario: Filtering happens before sorting
- **WHEN** `getRecordingsList(filter)` is called
- **THEN** the filter SHALL be applied before the list is sorted

#### Scenario: Filter structure supports future tag filters
- **WHEN** a future filter implementation is added
- **THEN** it SHALL be usable without changing the `getRecordingsList()` signature

### Requirement: Recording card displays creation date and time
The recording card SHALL display the recording timestamp provided by the model.

#### Scenario: Valid timestamp is displayed
- **WHEN** a recording has `createdAtMillis` greater than `0`
- **THEN** the card SHALL display the date and time formatted as `dd MMM yyyy · HH:mm`

#### Scenario: Unknown timestamp omits date
- **WHEN** a recording has `createdAtMillis` equal to `0L`
- **THEN** the card SHALL display only the duration
