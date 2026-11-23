## ADDED Requirements

### Requirement: Finalization opens recording review

Ao finalizar uma gravação, o sistema SHALL manter o arquivo salvo com o nome sugerido e SHALL apresentar a revisão pós-gravação antes de recarregar a lista de gravações.

#### Scenario: Gravação finalizada com sucesso

- **WHEN** o usuário finaliza uma gravação com sucesso
- **THEN** o arquivo é salvo com o nome sugerido
- **AND** a revisão pós-gravação é exibida
- **AND** a lista de gravações não é recarregada antes da ação do usuário

#### Scenario: Gravação finalizada com falha

- **WHEN** o usuário finaliza uma gravação e o salvamento falha
- **THEN** a revisão pós-gravação não é exibida
- **AND** uma mensagem de erro é apresentada

### Requirement: Recording list refreshes after review

A lista de gravações SHALL ser recarregada após o usuário salvar, descartar ou fechar a revisão pós-gravação.

#### Scenario: Lista atualizada após salvar

- **WHEN** o usuário salva a revisão pós-gravação com sucesso
- **THEN** a lista de gravações é recarregada
- **AND** a gravação revisada aparece com o nome e as tags persistidos

#### Scenario: Lista atualizada após descartar

- **WHEN** o usuário descarta a revisão pós-gravação com sucesso
- **THEN** a lista de gravações é recarregada
- **AND** a gravação descartada não aparece

#### Scenario: Lista atualizada ao fechar a revisão

- **WHEN** o usuário fecha a revisão pós-gravação por Back ou toque fora
- **THEN** a lista de gravações é recarregada
- **AND** a gravação aparece com o nome sugerido e sem tags
