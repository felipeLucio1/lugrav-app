## Why

Quando um RecordingCard está selecionado (com border azul visível), o clique no card currently abre o bottom sheet ao invés de desmarcar a seleção. O usuário não consegue desmarcar o card sem executar outra ação (share ou delete). Isso cria uma experiência de usuário frustrante onde a seleção fica "presa".

## What Changes

- Modificar o comportamento do onClick do RecordingCard na LugravScreen para verificar se o card já está selecionado
- Se o card já está selecionado (selectedFilePath == recording.path) → desmarcar (selectedFilePath = null)
- Se o card não está selecionado → comportamento normal (abrir bottom sheet)
- Nenhuma modificação necessária no RecordingCard.kt

## Capabilities

### New Capabilities

- Nenhuma nova capability - é uma melhoria de UX na funcionalidade existente de seleção

### Modified Capabilities

- Nenhuma modificação em capabilities existentes - apenas ajuste na lógica de UI

## Impact

- LugravScreen.kt: Modificar lambda onClick do RecordingCard para incluir toggle de seleção
- Nenhum impacto em Repository, ViewModel ou outros componentes