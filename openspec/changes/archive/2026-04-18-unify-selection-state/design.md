## Context

A LugravScreen.kt atualmente possui múltiplos estados para gerenciar seleção de arquivos:
- `isDeleteMode: Boolean`
- `selectedFileToDelete: String?`
- `selectedFileToShare: String?`

Esta abordagem criou complexidade desnecessária e não permite que o ícone de share seja visível independentemente do modo delete.

## Goals / Non-Goals

**Goals:**
- Unificar estados de seleção em um único `selectedFilePath`
- Manter ícones de share e delete visíveis enquanto um card está selecionado
- Simplificar lógica de UI

**Non-Goals:**
- Modificar comportamento de recordings ou ViewModel
- Adicionar novas funcionalidades

## Decisions

### 1. Estado único de seleção

Decisão: Usar `selectedFilePath: String?` como único estado de seleção

Alternativas consideradas:
- Manter três estados separados (rejeitado - complexidade desnecessária)
- Usar enum para tipo de seleção (excessivo para duas ações)

Rationale: A documentação do Compose (state hoisting) e práticas recomendadas de 2026 indicam que estados relacionados devem ser unificados quando são mutuamente exclusivos. Compartilhar e excluir não podem acontecer simultaneamente.

### 2. Ícones visíveis sempre que selecionado

Decisão: Ambos os ícones (share e delete) aparecem quando `selectedFilePath != null`

Rationale: Melhor UX - usuário pode escolher ação sem necessidade de modes diferentes. Simplifica o modelo mental do usuário.

### 3. Limpar estado após ação

Decisão: `selectedFilePath` é limpo após ação de share ou delete confirmada

Rationale: Mantém consistência com comportamento anterior onde modo delete era desativado após ação.

## Risks / Trade-offs

- [Risk] Usuário tentar clicar em share e delete simultaneamente → [Mitigation] Ações são sequential - primeiro ação chosen limpa o estado antes da segunda poder ser executada

- [Risk] Long press em card diferente não atualiza seleção → [Mitigation] Com estado único, long press em qualquer card atualiza selectedFilePath

## Migration Plan

1. Modificar LugravScreen.kt para usar estado único
2. Testar fluxo de long press + share
3. Testar fluxo de long press + delete

Rollback: Reverter mudanças no arquivo - não afeta dados ou outros componentes.