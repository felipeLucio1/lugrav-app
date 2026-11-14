## 1. Camada de dados

- [x] 1.1 Adicionar `deleteForTag(tagId)` ao `RecordingTagDao`
- [x] 1.2 Executar `deleteTag` do `RoomRecordingTagStore` em `withWriteTransaction`, removendo associações e tag
- [x] 1.3 Confirmar que nenhuma gravação ou arquivo é removido

## 2. ViewModel

- [x] 2.1 Alterar `deleteTagResult` para `Result<String>`
- [x] 2.2 Normalizar o nome, excluir a tag e publicar o nome no sucesso
- [x] 2.3 Recarregar tags, remover filtro e recarregar gravações após a exclusão

## 3. Estado da tela

- [x] 3.1 Adicionar `isTagDeletionMode`, `tagPendingDeletionName` e `isTagDeletionInProgress`
- [x] 3.2 Unificar `BackHandler` para fechar dialog, sair do modo ou limpar seleção
- [x] 3.3 Fazer toques fora do carrossel apenas encerrarem o modo
- [x] 3.4 Bloquear abertura de gravação, FAB e “Nova Tag” durante o modo
- [x] 3.5 Fechar dialogs/bottom sheet e não entrar no modo durante gravação

## 4. Componentes e recursos

- [x] 4.1 Atualizar `TagBadge` com `combinedClickable`, `X` antes do ponto e acessibilidade
- [x] 4.2 Atualizar `TagCarousel` com estado e callbacks de exclusão
- [x] 4.3 Criar `DeleteTagConfirmationDialog`
- [x] 4.4 Adicionar strings de confirmação, sucesso, erro e acessibilidade
- [x] 4.5 Exibir snackbar curto com o nome da tag após o sucesso

## 5. Testes

- [x] 5.1 Atualizar testes do ViewModel para o resultado com nome
- [x] 5.2 Adicionar teste de repositório garantindo que a gravação permanece
- [x] 5.3 Confirmar que o teste instrumentado do store Room não é aplicável neste ambiente sem emulador; a transação foi implementada no store real e a preservação da gravação foi coberta pelo teste de repositório

## 6. Validação

- [x] 6.1 Executar `:app:compileDebugKotlin`
- [x] 6.2 Executar `:app:testDebugUnitTest`
- [x] 6.3 Executar `:app:lintDebug`
- [x] 6.4 Executar `git diff --check`
