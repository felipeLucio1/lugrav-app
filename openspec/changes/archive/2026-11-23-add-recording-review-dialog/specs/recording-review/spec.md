# Spec Delta

## Purpose

Define the post-recording review experience that lets users confirm or discard a newly saved recording, including editing its name and selecting tags.

## ADDED Requirements

### Requirement: Review newly saved recording

Ao finalizar uma gravação, o sistema SHALL manter o arquivo salvo com o nome sugerido e SHALL apresentar um dialog de revisão antes de atualizar a lista de gravações.

#### Scenario: Finalização abre a revisão

- **WHEN** o usuário finaliza uma gravação com sucesso
- **THEN** o arquivo permanece salvo com o nome sugerido
- **AND** o dialog de revisão é exibido
- **AND** a lista de gravações não é recarregada antes da ação do usuário

#### Scenario: Falha ao finalizar não abre a revisão

- **WHEN** o usuário finaliza uma gravação e o salvamento falha
- **THEN** o dialog de revisão não é exibido
- **AND** uma mensagem de erro é apresentada

### Requirement: Edit name during review

O dialog de revisão SHALL preencher o campo de nome com o nome sugerido, selecionar o texto e permitir que o usuário informe outro nome válido.

#### Scenario: Nome sugerido pré-preenchido

- **WHEN** o dialog de revisão é aberto
- **THEN** o campo de nome contém o nome sugerido
- **AND** o texto do campo está selecionado

#### Scenario: Nome inválido bloqueia o salvamento

- **WHEN** o usuário informa um nome inválido no dialog de revisão
- **THEN** a ação `Salvar` permanece desabilitada
- **AND** nenhuma alteração é persistida

#### Scenario: Nome sugerido é aceito

- **WHEN** o usuário mantém o nome sugerido
- **THEN** a ação `Salvar` permanece habilitada
- **AND** o arquivo não precisa ser renomeado

### Requirement: Select tags during review

O dialog de revisão SHALL listar as tags existentes com seleção independente e SHALL permitir salvar sem nenhuma tag selecionada.

#### Scenario: Tags opcionais

- **WHEN** o usuário abre o dialog de revisão
- **THEN** nenhuma tag está selecionada
- **AND** a ação `Salvar` permanece habilitada quando o nome é válido

#### Scenario: Associações salvas

- **WHEN** o usuário salva a revisão com uma ou mais tags selecionadas
- **THEN** a gravação fica associada exatamente às tags selecionadas

#### Scenario: Nenhuma tag selecionada

- **WHEN** o usuário salva a revisão sem selecionar tags
- **THEN** a gravação permanece sem associações de tags

### Requirement: Save reviewed recording

Ao salvar a revisão, o sistema SHALL validar o nome, SHALL renomear o arquivo somente quando o nome mudar, SHALL persistir as tags selecionadas e SHALL fechar o dialog e recarregar a lista em caso de sucesso.

#### Scenario: Salvar com nome alterado

- **WHEN** o usuário salva a revisão com um nome diferente do sugerido
- **THEN** o arquivo é renomeado
- **AND** as tags selecionadas são persistidas
- **AND** o dialog é fechado
- **AND** a lista de gravações é recarregada

#### Scenario: Salvar com o nome sugerido

- **WHEN** o usuário salva a revisão mantendo o nome sugerido
- **THEN** o arquivo não é renomeado
- **AND** as tags selecionadas são persistidas
- **AND** o dialog é fechado
- **AND** a lista de gravações é recarregada

#### Scenario: Falha ao salvar mantém a revisão

- **WHEN** o salvamento da revisão falha
- **THEN** o dialog permanece aberto
- **AND** uma mensagem de erro é apresentada
- **AND** o arquivo original é preservado

### Requirement: Discard reviewed recording

Ao descartar a revisão, o sistema SHALL remover o arquivo recém-salvo e seu metadata, SHALL fechar o dialog e SHALL recarregar a lista em caso de sucesso.

#### Scenario: Descartar gravação

- **WHEN** o usuário aciona `Descartar`
- **THEN** o arquivo é removido
- **AND** o metadata da gravação é removido
- **AND** o dialog é fechado
- **AND** a lista de gravações é recarregada

#### Scenario: Falha ao descartar mantém a revisão

- **WHEN** a remoção da gravação falha
- **THEN** o dialog permanece aberto
- **AND** uma mensagem de erro é apresentada

### Requirement: Dismiss review keeps recording

Ao fechar o dialog por Back ou toque fora, o sistema SHALL manter a gravação com o nome sugerido e sem tags, SHALL fechar o dialog e SHALL recarregar a lista.

#### Scenario: Fechar revisão sem salvar

- **WHEN** o usuário fecha o dialog por Back ou toque fora
- **THEN** a gravação é mantida com o nome sugerido
- **AND** a gravação permanece sem tags
- **AND** a lista de gravações é recarregada
