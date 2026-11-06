## ADDED Requirements

### Requirement: MetadataRetriever lifecycle
The system SHALL create a fresh `MediaMetadataRetriever` for each data source and release it when extraction is complete.

#### Scenario: New retriever per data source
- **WHEN** `MetadataRetrieverImpl.setDataSource(path)` is called
- **THEN** a new `MediaMetadataRetriever` SHALL be created
- **AND** any previous instance SHALL be released

#### Scenario: Release is idempotent
- **WHEN** `MetadataRetrieverImpl.release()` is called
- **THEN** the current `MediaMetadataRetriever` SHALL be released
- **AND** the internal reference SHALL be cleared
- **AND** subsequent `release()` calls SHALL be safe

#### Scenario: Data source failure releases new instance
- **WHEN** `setDataSource()` throws an exception
- **THEN** the newly created `MediaMetadataRetriever` SHALL be released
- **AND** the exception SHALL be propagated

#### Scenario: Duration extraction failure returns null
- **WHEN** `extractDuration()` encounters an exception
- **THEN** it SHALL return null

#### Scenario: Repository releases retriever after extraction
- **WHEN** `getAudioDuration(filePath)` is called
- **THEN** `metadataRetriever.release()` SHALL be called even if `setDataSource()` or `extractDuration()` fails

#### Scenario: Duration extraction is serialized
- **WHEN** multiple duration extractions run concurrently
- **THEN** the repository SHALL serialize access to the `MetadataRetriever`
