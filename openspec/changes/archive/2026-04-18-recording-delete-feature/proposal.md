## Why

Os usuários precisam de uma forma de excluir gravações de áudio. O app atualmente exibe ícones de play nos cards que não são necessários (design especifica "play_arrow" no botão direito, não esquerdo), e não há funcionalidade de exclusão implementada. A exclusão deve ser acionada por um gesto de pressionar e segurar (long press) no card, seguida de confirmação via dialog.

## What Changes

- Remover o ícone play (IconButton) do lado esquerdo do RecordingCard
- Adicionar gesto de long press no RecordingCard para ativar modo de seleção
- Exibir ícone de lixeira na TopBar quando o modo delete está ativo
- Criar componente DeleteConfirmationDialog com botões Sim/Não
- Integrar estados de delete mode e gerenciamento de resultado na LugravScreen
- Adicionar snackbar de erro quando exclusão falha
- Chamar clearDeleteResult após consumir o resultado

## Capabilities

### New Capabilities

- `recording-delete`: Funcionalidade completa de exclusão de gravação via long press + dialog de confirmação

### Modified Capabilities

- Nenhuma modificação em capacidades existentes

## Impact

- `AudioRecordingRepository.kt`: Já possui método deleteRecording (implementado anteriormente)
- `AudioRecordingViewModel.kt`: Já possui método deleteRecording e clearDeleteResult (implementado anteriormente)
- `RecordingCard.kt`: Remoção do ícone play + adição de long press
- `LugravTopBar.kt`: Adição opcional de ícone de delete
- `LugravScreen.kt`: Integração de estados e snackbar
- Novo componente: `DeleteConfirmationDialog.kt`
- `strings.xml`: Novas strings para dialog e mensagens de erro