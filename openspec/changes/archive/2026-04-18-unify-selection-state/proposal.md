## Why

A LugravScreen atualmente possui três estados separados (isDeleteMode, selectedFileToDelete, selectedFileToShare) para gerenciar a seleção de arquivos. Isso cria complexidade desnecessária já que não é possível compartilhar e excluir ao mesmo tempo. A documentação do Compose recomenda manter estados no nível mais alto onde são consumidos e usar estados únicos para operações mutuamente exclusivas.

## What Changes

- Unificar isDeleteMode, selectedFileToDelete e selectedFileToShare em um único estado selectedFilePath
- Manter ícone de share visível enquanto um card está selecionado (não apenas em modo delete)
- Simplificar lógica de long press para setar apenas selectedFilePath
- Atualizar condição de exibição dos ícones na toolbar para usar selectedFilePath
- Limpar selectedFilePath após ação de share ou delete completar

## Capabilities

### New Capabilities

- Nenhuma nova capability - é uma refatoração de código existente

### Modified Capabilities

- Nenhuma modificação em capabilities existentes - apenas simplificação de estado UI

## Impact

- LugravScreen.kt: Unificar estados e simplificar lógica de seleção
- Nenhuma modificação em Repository, ViewModel ou outros componentes