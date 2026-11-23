## ADDED Requirements

### Requirement: Tags assigned during recording review

O sistema SHALL permitir que as tags de uma nova gravação sejam definidas durante a revisão pós-gravação, substituindo o conjunto de associações da gravação pelas tags selecionadas.

#### Scenario: Tags selecionadas na revisão

- **WHEN** o usuário salva a revisão pós-gravação com tags selecionadas
- **THEN** a gravação fica associada exatamente a essas tags
- **AND** as associações anteriores da gravação são substituídas

#### Scenario: Revisão sem tags

- **WHEN** o usuário salva a revisão pós-gravação sem selecionar tags
- **THEN** a gravação permanece sem associações de tags

#### Scenario: Falha ao persistir tags

- **WHEN** a persistência das tags selecionadas falha durante o salvamento da revisão
- **THEN** o arquivo permanece com o nome anterior
- **AND** as associações de tags anteriores são preservadas
- **AND** uma mensagem de erro é apresentada
