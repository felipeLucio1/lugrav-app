## Why

Os usuários precisam de um menu de contexto na AppBar quando selecionam uma gravação na lista. Atualmente o app já suporta long press para selecionar um item e exibe ícones de Compartilhar/Excluir na TopBar condicionalmente, mas essa interação precisa ser formalizada como um "menu de contexto" (contextual action bar) claro, com melhor UX: ativação por long press/seleção, seta para voltar para sair do modo, e ações contextuais agrupadas em um menu.

## What Changes

- Formalizar o modo seleção/contextual ao pressionar e segurar (long press) uma gravação na lista
- Exibir seta para esquerda (ArrowBack) na LugravTopBar quando um item está selecionado para sair da seleção
- Exibir ações contextuais (Compartilhar e Excluir) dentro de um menu de contexto (MoreVert + DropdownMenu) na LugravTopBar
- Garantir que ao clicar em um item já selecionado a seleção seja limpa (comportamento atual mantido)
- Permitir sair do modo contextual (limpar seleção) sem executar ação
- Manter integração com RecordingBottomSheet para reprodução ao clicar em item não selecionado
- Preservar comportamento de compartilhamento via FileProvider (ACTION_SEND, tipo udio/aac)
- Preservar diálogo de confirmação para exclusão e tratamento de erros via Snackbar

## Capabilities

### New Capabilities

- ecording-context-menu: Modo seleção/contextual com seta para voltar e ações na AppBar (Compartilhar/Excluir em menu) ativado por seleção de gravação

### Modified Capabilities

- Nenhuma

## Impact

- pp/src/main/java/com/felipelucio/lugrav/view/components/LugravTopBar.kt: Suporte a modo contextual com 
avigationIcon (ArrowBack) e DropdownMenu (MoreVert)
- pp/src/main/java/com/felipelucio/lugrav/view/components/RecordingCard.kt: Long press já implementado
- pp/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt: Gerencia selectedFilePath, limpeza de seleção e callbacks
- pp/src/main/java/com/felipelucio/lugrav/AudioRecordingViewModel.kt: Sem alterações necessárias
- pp/src/main/java/com/felipelucio/lugrav/AudioRecordingRepository.kt: Sem alterações necessárias
- pp/src/main/res/values/strings.xml: Strings para voltar e mais opções
