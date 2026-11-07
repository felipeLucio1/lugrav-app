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
- **AND** tapping it SHALL open a dropdown with Share, Rename, and Delete actions
