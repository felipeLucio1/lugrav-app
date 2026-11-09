## ADDED Requirements

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

### Requirement: Test recording filtering
The repository tests SHALL verify custom filters.

#### Scenario: Custom filter returns only matching recordings
- **WHEN** `getRecordingsList(filter)` is called with a filter that matches one file
- **THEN** only that recording SHALL be returned

#### Scenario: Default filter returns all recordings
- **WHEN** `getRecordingsList()` is called without a filter
- **THEN** all `.aac` recordings SHALL be returned
