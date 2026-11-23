## ADDED Requirements

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
