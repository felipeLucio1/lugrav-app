## Context

O app já possui:
- Long press em RecordingCard define selectedFilePath
- Card selecionado exibe borda destacada
- LugravScreen gerencia seleção e ações de compartilhar/excluir
- Clique no card: se já selecionado limpa seleção; senão abre RecordingBottomSheet para reprodução
- Compartilhamento via FileProvider + Intent.ACTION_SEND
- Exclusão com DeleteConfirmationDialog e tratamento via ViewModel/Snackbar

## Goals / Non-Goals

**Goals:**
- Formalizar UX de menu de contexto na AppBar para ações em gravação selecionada
- Exibir seta para esquerda (ArrowBack) para sair do modo seleção
- Agrupar ações (Compartilhar/Excluir) em menu de contexto (MoreVert + DropdownMenu)
- Manter fluxo existente (reprodução via bottom sheet) intacto
- Garantir consistência: ao executar ação (share/delete) limpar seleção
- Permitir sair do modo contextual facilmente

**Non-Goals:**
- Renomear/editar gravação
- Seleção múltipla
- Adicionar novas ações além de Compartilhar/Excluir

## Decisions

### 1. Ativação do modo contextual
- **Decisão**: Usar long press no RecordingCard para entrar em modo seleção (já existente).
- **Alternativa**: Menu popup - rejeitado por já existir padrão de AppBar contextual.

### 2. Sair do modo contextual
- **Decisão**: Exibir ArrowBack como 
avigationIcon na LugravTopBar quando isSelectionMode == true.
- **Razão**: Segue padrão Android de contextual action bar/ActionMode, com affordance clara para voltar.

### 3. Local das ações
- **Decisão**: Exibir ações em DropdownMenu acionado por MoreVert na LugravTopBar quando em modo seleção.
- **Razão**: Mantém a AppBar limpa e agrupa ações contextuais.

### 4. Limpeza de seleção
- **Decisão**: Limpar seleção ao executar ação (share/delete), ao clicar na seta ArrowBack, ou ao clicar no card já selecionado.
- **Justificativa**: Evita estado confuso; após compartilhar/excluir não faz sentido manter seleção.

### 5. Compartilhamento
- **Decisão**: Manter implementação atual com FileProvider.getUriForFile e Intent.ACTION_SEND tipo udio/aac.
- **Nota**: FileProvider já configurado no projeto.

### 6. Exclusão
- **Decisão**: Manter diálogo de confirmação DeleteConfirmationDialog. Chamar iewModel.deleteRecording() e tratar resultado via Snackbar.
- **Comportamento**: Após confirmação, limpar seleção e atualizar lista.

## Risks / Trade-offs

- **Conflito entre seleção e reprodução**: Clique em item não selecionado abre bottom sheet; clique em item selecionado limpa seleção. Lógica atual já trata isso.
- **Descoberta de ações no menu**: Ações ficam atrás do MoreVert; aceitável para 2 ações e interface mais limpa.
- **Limpar seleção ao compartilhar**: Boa prática para não deixar estado residual.

## Migration Plan

Nenhuma migração necessária - mudanças são incrementais sobre código existente.

## Open Questions

Nenhuma - comportamento definido e implementado conforme plano atualizado.
