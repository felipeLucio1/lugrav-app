## ADDED Requirements

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
