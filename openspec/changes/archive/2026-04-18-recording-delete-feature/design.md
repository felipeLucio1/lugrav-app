## Context

Este é um app Android nativo (Kotlin + Jetpack Compose) para gravação de voz. O app já possui:
- Repository com método `deleteRecording(filePath)` que chama `stopAudio()` antes de excluir
- ViewModel com `deleteRecording(filePath)` usando Mutex para concorrência
- ViewModel com `clearDeleteResult()` para limpar estado após operação

O design.md do projeto estabelece:
- Cores: SkyBlue (#29B6F6), Error (#B3261E)
- Cards ElevatedCard com radius 16dp
- Material Design 3

## Goals / Non-Goals

**Goals:**
- Remover ícone play do RecordingCard (não está no design original)
- Implementar long press no card para ativar modo delete
- Exibir ícone lixeira na TopBar quando em modo delete
- Dialog de confirmação antes de excluir
- Snackbar de erro após falha na exclusão

**Non-Goals:**
- Implementar seleção múltipla de itens
- Undo de exclusão
- Testes unitários ou de UI

## Decisions

### 1. Abordagem de Dialog: Estados na Screen vs Props

Decisão: Usar estados na LugravScreen (`_selectedFileToDelete`, `_showDeleteDialog`)

Rationale: Mantém o componente Dialog puro e reutilizável. O Dialog não precisa saber sobre ViewModel ou logic de delete.

### 2. Limpeza de Estado após Delete

Decisão: Chamar `clearDeleteResult()` após consumir o resultado no LaunchedEffect

Rationale: Sem isso, o estado permanece com valor causando re-renderizações desnecessárias. O comportamento espelha o padrão já usado em `recordingSuccess` na mesma screen.

### 3. Gesto de Long Press

Decisão: Usar `combinedClickable` do Compose com `onLongPress` para ativar modo delete

Rationale: API nativa do Compose, não requer dependência adicional. O `onClick` continua funcionando para abrir o RecordingBottomSheet quando não está em modo delete.

### 4. Posicionamento do ícone delete na TopBar

Decisão: Usar o parâmetro `actions` do CenterAlignedTopAppBar

Rationale: Mantém o padrão do Material 3 para ações na TopBar. Ícone branco conforme design (ícones da TopBar devem ser brancos).

### 5. Fluxo de usuário

Decisão: Long press → ativa modo delete → clique no ícone de lixeira → dialog de confirmação

Rationale:
- Permite que usuário selecione qual arquivo excluir (pode mudar de ideia com long press em outro arquivo)
- Separa a ação de ativar modo delete da ação de confirmar exclusão
- Evita exclusão acidental

## Risks / Trade-offs

- [Risk] Usuário pode esquecer que está em modo delete e tentar abrir bottom sheet → [Mitigation] Long press em outro card atualiza a seleção, clique em área não-clickável sai do modo delete

- [Risk] Exclusão falhar silenciosamente → [Mitigation] Snackbar de erro exibido após falha

- [Risk] Race condition se usuário tentar excluir outro arquivo rapidamente → [Mitigation] Mutex no ViewModel serializa operações