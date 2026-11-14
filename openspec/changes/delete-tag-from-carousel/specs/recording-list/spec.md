## ADDED Requirements

### Requirement: Tag deletion mode
The recording list SHALL enter a tag deletion mode when the user long-presses any tag badge in the tag carousel.

#### Scenario: Entering deletion mode
- **WHEN** the user long-presses a tag badge and no recording is active
- **THEN** the carousel enters deletion mode
- **AND** every tag badge displays a delete icon before its color dot and name
- **AND** the "Nova Tag" action is disabled

#### Scenario: Exiting deletion mode
- **WHEN** deletion mode is active and the user presses Back or taps outside the carousel
- **THEN** deletion mode ends without deleting any tag
- **AND** any open dialog or bottom sheet is dismissed

#### Scenario: Blocking interactions during deletion mode
- **WHEN** deletion mode is active
- **THEN** tapping a recording card does not open the recording
- **AND** tapping the recording FAB does not start recording
- **AND** tapping outside the carousel only exits deletion mode

#### Scenario: Recording in progress
- **WHEN** a recording is in progress
- **THEN** long-pressing a tag badge does not enter deletion mode

### Requirement: Tag deletion confirmation
The recording list SHALL require explicit confirmation before deleting a tag.

#### Scenario: Confirming deletion
- **WHEN** the user taps the delete icon on a tag
- **THEN** a confirmation dialog displays `Deseja excluir a tag <nome da tag>?`
- **AND** the dialog provides "Sim" and "Não" actions

#### Scenario: Cancelling deletion
- **WHEN** the confirmation dialog is open and the user chooses "Não" or dismisses the dialog
- **THEN** no tag is deleted
- **AND** deletion mode remains active

#### Scenario: Accepting deletion
- **WHEN** the user chooses "Sim"
- **THEN** the tag is deleted
- **AND** the dialog closes

### Requirement: Tag deletion feedback
The recording list SHALL show feedback after a tag deletion completes.

#### Scenario: Successful deletion
- **WHEN** a tag named `Trabalho` is deleted successfully
- **THEN** a snackbar displays `Tag Trabalho excluída com sucesso`
- **AND** the snackbar duration is short
- **AND** the tag list and recording list are refreshed

#### Scenario: Failed deletion
- **WHEN** tag deletion fails
- **THEN** a snackbar displays `Erro ao excluir tag`
- **AND** the tag remains visible
