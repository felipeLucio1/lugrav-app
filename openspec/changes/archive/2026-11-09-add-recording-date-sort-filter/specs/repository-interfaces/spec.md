## ADDED Requirements

### Requirement: Repository accepts recording filters
The `AudioRecordingRepository` SHALL accept a `RecordingFilter` when listing recordings.

#### Scenario: Default filter includes all recordings
- **WHEN** `getRecordingsList()` is called without arguments
- **THEN** `RecordingFilter.All` SHALL be used

#### Scenario: Custom filter is applied
- **WHEN** `getRecordingsList(filter)` is called
- **THEN** only recordings matching `filter.matches()` SHALL be returned

#### Scenario: Filter is applied before sorting
- **WHEN** `getRecordingsList(filter)` processes multiple files
- **THEN** the filter SHALL be applied before ordering by `createdAtMillis`
