## ADDED Requirements

### Requirement: Test recording filter propagation
The ViewModel tests SHALL verify that the active recording filter is passed to the repository and preserved across reloads.

#### Scenario: Initial filter defaults to all
- **WHEN** `loadRecordings()` is called without a filter
- **THEN** the repository SHALL receive `RecordingFilter.All`

#### Scenario: Custom filter is forwarded
- **WHEN** `loadRecordings(filter)` is called with a custom filter
- **THEN** the repository SHALL receive that filter

#### Scenario: Reload preserves active filter
- **WHEN** `loadRecordings(filter)` is followed by `loadRecordings()`
- **THEN** the repository SHALL receive the same custom filter on both calls
