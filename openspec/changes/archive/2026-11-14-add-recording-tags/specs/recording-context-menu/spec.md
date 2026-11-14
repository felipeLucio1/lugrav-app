## MODIFIED Requirements

### Requirement: Contextual AppBar mode
The system SHALL display a back arrow (ArrowBack) and a context menu (MoreVert) in the AppBar when a recording is selected.

#### Scenario: Back arrow is shown in selection mode
- **WHEN** a recording is selected
- **THEN** ArrowBack navigation icon SHALL be displayed in LugravTopBar
- **AND** tapping it SHALL clear the selection

#### Scenario: Context menu is shown in selection mode
- **WHEN** a recording is selected
- **THEN** MoreVert action SHALL be displayed in LugravTopBar
- **AND** tapping it SHALL open a dropdown with Share, Rename, Select tags, and Delete actions

## ADDED Requirements

### Requirement: Select tags for a recording
The system SHALL allow selecting multiple tags for a recording from the context menu.

#### Scenario: Select tags action opens the dialog
- **WHEN** user selects "Selecionar tags" from the context menu
- **THEN** `SelectTagsDialog` SHALL be displayed for the selected recording

#### Scenario: Dialog shows all available tags
- **WHEN** the dialog opens and tags exist
- **THEN** it SHALL display a scrollable list with a checkbox for each tag
- **AND** tags already associated with the recording SHALL start checked

#### Scenario: Save is disabled without changes
- **WHEN** the selected tag set equals the recording's current tags
- **THEN** the save action SHALL be disabled

#### Scenario: User changes the selection
- **WHEN** user checks or unchecks tags
- **THEN** the save action SHALL be enabled

#### Scenario: Save replaces recording tags
- **WHEN** user saves a changed selection
- **THEN** `setRecordingTags` SHALL replace the recording's associations
- **AND** the dialog SHALL close
- **AND** on success the selection mode SHALL be cleared and a success message SHALL be shown

#### Scenario: Save failure keeps selection mode
- **WHEN** saving tags fails
- **THEN** an error message SHALL be shown
- **AND** the selected recording SHALL remain selected

#### Scenario: Cancel keeps selection mode
- **WHEN** user cancels the dialog
- **THEN** the dialog SHALL close
- **AND** the selected recording SHALL remain selected

#### Scenario: Empty tag list
- **WHEN** the dialog opens and no tags exist
- **THEN** it SHALL display an empty-state message

#### Scenario: Tags are loading
- **WHEN** the dialog opens while tags are loading
- **THEN** it SHALL display a loading indicator
