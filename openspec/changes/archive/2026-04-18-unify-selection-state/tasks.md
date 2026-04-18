## 1. LugravScreen - Unificar estados

- [x] 1.1 Remover estados isDeleteMode, selectedFileToDelete, selectedFileToShare
- [x] 1.2 Adicionar estado único selectedFilePath: String? = null

## 2. LugravScreen - Atualizar lógica de long press

- [x] 2.1 Modificar onLongPress do RecordingCard para setar apenas selectedFilePath

## 3. LugravScreen - Atualizar toolbar

- [x] 3.1 Modificar condição de exibição do ícone share para selectedFilePath != null
- [x] 3.2 Modificar condição de exibição do ícone delete para selectedFilePath != null

## 4. LugravScreen - Atualizar RecordingCard

- [x] 4.1 Modificar isSelected para comparar selectedFilePath com recording.path

## 5. LugravScreen - Atualizar ações de share e delete

- [x] 5.1 Modificar onConfirm do DeleteConfirmationDialog para limpar selectedFilePath
- [x] 5.2 Verificar que shareRecording já limpa selectedFilePath após execução

## 6. Verificação

- [x] 6.1 Compilar o projeto para verificar erros
- [x] 6.2 Verificar que o código segue o estilo do projeto