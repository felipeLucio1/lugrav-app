## 1. RecordingCard - Remover ícone play e adicionar long press

- [x] 1.1 Remover IconButton com Icons.Filled.PlayArrow do RecordingCard (linhas 50-59)
- [x] 1.2 Adicionar combinedClickable com onLongPress no ElevatedCard
- [x] 1.3 Adicionar parâmetro onLongPress ao RecordingCard
- [x] 1.4 Adicionar imports necessários (androidx.compose.foundation.clickable)

## 2. LugravTopBar - Adicionar ícone de delete

- [x] 2.1 Adicionar parâmetro opcional onDeleteClick ao LugravTopBar
- [x] 2.2 Adicionar ações na TopBar quando onDeleteClick não for nulo
- [x] 2.3 Exibir ícone de lixeira (Icons.Default.Delete) quando em modo delete
- [x] 2.4 Usar cor branca para o ícone (conforme design.md)

## 3. DeleteConfirmationDialog - Criar componente

- [x] 3.1 Criar arquivo DeleteConfirmationDialog.kt em view/components/
- [x] 3.2 Implementar AlertDialog com botões Sim/Não
- [x] 3.3 Adicionar parâmetro onConfirm e onDismiss
- [x] 3.4 Tornar componente reutilizável

## 4. Strings - Adicionar resources

- [x] 4.1 Adicionar string "delete_recording_message" = "Deseja excluir a gravação?"
- [x] 4.2 Adicionar string "confirm_yes" = "Sim"
- [x] 4.3 Adicionar string "confirm_no" = "Não"
- [x] 4.4 Adicionar string "delete_error" = "Erro ao excluir gravação"

## 5. LugravScreen - Integrar estados e lógica

- [x] 5.1 Adicionar estado selectedFileToDelete (String? = null)
- [x] 5.2 Adicionar estado isDeleteMode (Boolean = false)
- [x] 5.3 Adicionar estado showDeleteDialog (Boolean = false)
- [x] 5.4 Coleta deleteResult do ViewModel
- [x] 5.5 Passar onLongPress para RecordingCard que atualiza estados
- [x] 5.6 Passar onDeleteClick para LugravTopBar quando isDeleteMode = true
- [x] 5.7 Exibir DeleteConfirmationDialog quando showDeleteDialog = true
- [x] 5.8 Implementar LaunchedEffect para deleteResult com snackbar de erro
- [x] 5.9 Chamar viewModel.clearDeleteResult() após consumir resultado
- [x] 5.10 Implementar ação de clique no ícone de lixeira (mostrar dialog)
- [x] 5.11 Implementar ação de clique em "Sim" (chamar deleteRecording + limpar estados)
- [x] 5.12 Implementar ação de clique em "Não" (fechar dialog + limpar selectedFileToDelete)

## 6. Verificação

- [x] 6.1 Compilar o projeto para verificar erros
- [x] 6.2 Verificar que o código segue o estilo do projeto