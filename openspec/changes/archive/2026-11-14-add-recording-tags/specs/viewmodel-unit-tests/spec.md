## ADDED Requirements

### Requirement: Test tag loading
The ViewModel SHALL expose tag loading state and update the tag list.

#### Scenario: Load tags succeeds
- **WHEN** `loadTags()` succeeds
- **THEN** `isLoadingTags` SHALL be `false`
- **AND** `tags` SHALL contain the loaded tags

#### Scenario: Load tags fails
- **WHEN** `getTags()` throws an exception
- **THEN** `isLoadingTags` SHALL be `false`
- **AND** `tags` SHALL remain unchanged

#### Scenario: Load tags prunes deleted filters
- **WHEN** `loadTags()` finds that a selected filter no longer exists
- **THEN** the filter SHALL be removed
- **AND** recordings SHALL be reloaded with the remaining filters

### Requirement: Test tag creation and deletion
The ViewModel SHALL manage tag creation and deletion results and reload dependent state.

#### Scenario: Create tag succeeds
- **WHEN** `createTag()` succeeds
- **THEN** `createTagResult` SHALL contain a success Result
- **AND** tags SHALL be reloaded
- **AND** recordings SHALL be reloaded if filters changed

#### Scenario: Create tag fails
- **WHEN** `createTag()` fails
- **THEN** `createTagResult` SHALL contain a failure Result

#### Scenario: Clear create tag result
- **WHEN** `clearCreateTagResult()` is called
- **THEN** `createTagResult` SHALL be null

#### Scenario: Delete tag succeeds
- **WHEN** `deleteTag()` succeeds
- **THEN** `deleteTagResult` SHALL contain a success Result
- **AND** the deleted tag SHALL be removed from active filters
- **AND** recordings SHALL be reloaded

#### Scenario: Delete tag fails
- **WHEN** `deleteTag()` fails
- **THEN** `deleteTagResult` SHALL contain a failure Result
- **AND** active filters SHALL remain unchanged

#### Scenario: Clear delete tag result
- **WHEN** `clearDeleteTagResult()` is called
- **THEN** `deleteTagResult` SHALL be null

### Requirement: Test tag filters
The ViewModel SHALL manage selected tag filters and reload recordings.

#### Scenario: Toggle tag filter adds and removes
- **WHEN** `toggleTagFilter(name)` is called
- **THEN** the tag SHALL be added to or removed from `selectedTagFilters`
- **AND** recordings SHALL be reloaded

#### Scenario: Set tag filters
- **WHEN** `setTagFilters(names)` is called
- **THEN** `selectedTagFilters` SHALL equal the provided set
- **AND** recordings SHALL be reloaded with all selected tags

#### Scenario: Clear tag filters
- **WHEN** `clearTagFilters()` is called
- **THEN** `selectedTagFilters` SHALL be empty
- **AND** all recordings SHALL be reloaded

#### Scenario: Load recordings uses selected filters
- **WHEN** `loadRecordings()` is called with active filters
- **THEN** the repository SHALL receive the selected tag set

### Requirement: Test set recording tags
The ViewModel SHALL expose the result of replacing a recording's tags and reload recordings.

#### Scenario: Set recording tags succeeds
- **WHEN** `setRecordingTags(path, tagNames)` succeeds
- **THEN** `setRecordingTagsResult` SHALL contain a success Result
- **AND** recordings SHALL be reloaded with the current filters

#### Scenario: Set recording tags fails
- **WHEN** `setRecordingTags()` fails
- **THEN** `setRecordingTagsResult` SHALL contain a failure Result
- **AND** recordings SHALL not be reloaded

#### Scenario: Clear set recording tags result
- **WHEN** `clearSetRecordingTagsResult()` is called
- **THEN** `setRecordingTagsResult` SHALL be null
