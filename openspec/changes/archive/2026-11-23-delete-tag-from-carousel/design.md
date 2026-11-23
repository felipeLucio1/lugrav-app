## Context

O app já possui um carrossel de tags com filtros, criação de tags e associação com gravações. A exclusão de tag existe nas camadas de domínio, repositório e ViewModel, mas não está exposta na interface. O banco usa uma tabela de cross-reference `recording_tags` com `ON DELETE CASCADE` para a tag, porém a operação atual não é explicitamente transacional.

## Goals / Non-Goals

**Goals:**

- Expor a exclusão de tags pelo carrossel com long press.
- Mostrar `X` em todas as tags durante o modo de exclusão.
- Confirmar a exclusão antes de executar.
- Bloquear abertura de gravação, início de gravação e demais interações externas enquanto o modo estiver ativo.
- Executar a remoção da tag e das associações em uma única transação.
- Preservar as gravações e recarregar as listas.
- Exibir snackbar curto com o nome da tag excluída.

**Non-Goals:**

- Alterar o schema do banco ou criar migração.
- Excluir arquivos de gravação.
- Alterar o fluxo de criação ou associação de tags.
- Adicionar exclusão em lote.
- Alterar o tema visual existente.

## Decisions

### 1. Long press entra no modo de exclusão

**Decisão:** `TagBadge` usará `combinedClickable`; long press em qualquer tag ativa `isTagDeletionMode` na tela.

**Razão:** O gesto fica restrito ao carrossel e não conflita com o long press já usado pelos cards de gravação.

### 2. `X` antes do ponto colorido

**Decisão:** No modo de exclusão, renderizar `Icons.Filled.Close` como primeiro elemento do badge, antes do ponto e do nome, usando `MaterialTheme.colorScheme.error`.

**Razão:** O `X` fica visualmente destacado como ação destrutiva, sem remover a identidade de cor da tag.

### 3. Confirmação obrigatória

**Decisão:** O `X` abre `DeleteTagConfirmationDialog` com a mensagem `Deseja excluir a tag <nome da tag>?`. `Não` fecha o dialog e mantém o modo; `Sim` executa a exclusão.

**Razão:** Evita exclusão acidental por toque no ícone.

### 4. Bloqueio por guardas de interação

**Decisão:** Durante o modo, os callbacks de card, FAB, fundo da lista e “Nova Tag” verificam o estado antes de executar suas ações. Toques fora do carrossel apenas encerram o modo. O `BackHandler` também encerra o modo.

**Razão:** Um overlay full-screen impediria a interação com o carrossel. As guardas mantêm o carrossel ativo e bloqueiam apenas as ações externas.

### 5. Exclusão atômica no store

**Decisão:** `RoomRecordingTagStore.deleteTag` buscará a tag, removerá os cross-refs com `deleteForTag(tagId)` e removerá a tag, tudo dentro de `database.withWriteTransaction`.

**Razão:** Garante que uma falha parcial não deixe associações órfãs. As gravações permanecem intactas.

### 6. Resultado carrega o nome da tag

**Decisão:** `deleteTagResult` passará a ser `Result<String>`, contendo o nome normalizado da tag excluída.

**Razão:** A UI consegue montar o snackbar correto sem manter estado duplicado de qual tag foi excluída.

## Risks / Trade-offs

- **Long press pode ser pouco descoberto:** o modo é reversível e não altera dados até a confirmação.
- **Bloqueio de interações:** o usuário precisa sair do modo para abrir uma gravação ou gravar; isso é intencional.
- **Exclusão durante reprodução:** ao entrar no modo, o bottom sheet e a reprodução serão encerrados para evitar estados conflitantes.
- **Teste de atomicidade:** a validação unitária cobre o repositório com fake; um teste instrumentado com Room em memória cobre a transação real quando houver dispositivo/emulador.
