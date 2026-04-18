## ADDED Requirements

### Requirement: Long press activates delete mode
O sistema SHALL permitir que o usuário ative o modo de exclusão através de um gesto de long press no card de gravação.

#### Scenario: Long press on recording card
- **WHEN** usuário executa long press em um card de gravação
- **THEN** o sistema marca o arquivo como selecionado para exclusão e exibe o ícone de lixeira na TopBar

#### Scenario: Long press on different recording
- **WHEN** usuário executa long press em outro card de gravação enquanto está em modo delete
- **THEN** o sistema atualiza a seleção para o novo arquivo

### Requirement: Delete icon in TopBar
O sistema SHALL exibir um ícone de lixeira na TopBar quando o modo delete está ativo.

#### Scenario: Delete mode activated
- **WHEN** modo delete é ativado (long press executado)
- **THEN** ícone de lixeira é exibido no lado direito da TopBar

#### Scenario: Delete mode deactivated
- **WHEN** usuário sai do modo delete (clicando em outro card ou área não-interativa)
- **THEN** ícone de lixeira deixa de ser exibido na TopBar

### Requirement: Delete confirmation dialog
O sistema SHALL exibir um dialog de confirmação antes de excluir uma gravação.

#### Scenario: User clicks delete icon
- **WHEN** usuário clica no ícone de lixeira da TopBar
- **THEN** dialog de confirmação é exibido com mensagem "Deseja excluir a gravação?" e botões Sim/Não

#### Scenario: User confirms deletion
- **WHEN** usuário clica em "Sim" no dialog de confirmação
- **THEN** o dialog é fechado e a gravação é excluída

#### Scenario: User cancels deletion
- **WHEN** usuário clica em "Não" no dialog de confirmação ou toca fora do dialog
- **THEN** o dialog é fechado sem excluir a gravação

### Requirement: Delete error handling
O sistema SHALL exibir uma snackbar de erro quando a exclusão falhar.

#### Scenario: Delete operation fails
- **WHEN** operação de exclusão falha (ex: arquivo não encontrado, permissão negada)
- **THEN** snackbar com mensagem de erro é exibida

#### Scenario: Delete result cleared after display
- **WHEN** resultado da operação de delete foi exibido (sucesso ou falha)
- **THEN** estado deleteResult é limpo através de clearDeleteResult()

### Requirement: Play icon removed from RecordingCard
O sistema SHALL remover o ícone de play do RecordingCard conforme especificado no design.

#### Scenario: RecordingCard rendered
- **WHEN** RecordingCard é renderizado na lista
- **THEN** não há ícone de play no lado esquerdo do card