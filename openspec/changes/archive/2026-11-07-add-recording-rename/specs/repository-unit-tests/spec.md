## ADDED Requirements

### Requirement: Test renameRecording behavior
The repository SHALL rename recording files while validating names and preserving content.

#### Scenario: Rename valid recording
- **WHEN** `renameRecording(filePath, newName)` is called with a valid new name
- **THEN** the file SHALL be renamed in the same directory
- **AND** the returned path SHALL end with `.aac`

#### Scenario: Rename preserves file content
- **WHEN** `renameRecording(filePath, newName)` succeeds
- **THEN** the renamed file SHALL contain the same bytes as the original file

#### Scenario: Rename appends extension
- **WHEN** `renameRecording(filePath, "Novo nome")` is called
- **THEN** the resulting file name SHALL be `Novo nome.aac`

#### Scenario: Rename does not duplicate extension
- **WHEN** `renameRecording(filePath, "Novo nome.aac")` is called
- **THEN** the resulting file name SHALL be `Novo nome.aac`
- **AND** it SHALL NOT be `Novo nome.aac.aac`

#### Scenario: Rename to same name is a no-op
- **WHEN** the normalized new name equals the current name
- **THEN** the original path SHALL be returned
- **AND** the file SHALL remain unchanged

#### Scenario: Rename with empty name throws IOException
- **WHEN** `renameRecording(filePath, "")` is called
- **THEN** an `IOException` SHALL be thrown

#### Scenario: Rename with invalid characters throws IOException
- **WHEN** `renameRecording(filePath, "nome/invalido")` is called
- **THEN** an `IOException` SHALL be thrown

#### Scenario: Rename to existing name throws IOException
- **WHEN** the target file already exists
- **THEN** an `IOException` SHALL be thrown

#### Scenario: Rename non-existent file throws IOException
- **WHEN** `renameRecording(filePath, newName)` is called and the source file does not exist
- **THEN** an `IOException` SHALL be thrown
