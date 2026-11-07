## Context

O app já possui modo de seleção com menu de contexto (`LugravTopBar`), `RecordingBottomSheet` para reprodução, `AudioRecordingRepository` para I/O de arquivos e `AudioRecordingViewModel` com `StateFlow` para estado. As gravações são arquivos `.aac` no diretório `lugrav`, nomeados com timestamp. O card extrai a data do nome do arquivo.

A mudança adiciona rename sem alterar interfaces existentes, mantendo a separação atual: UI Compose, ViewModel, Repository e regras puras de nome.

## Goals / Non-Goals

**Goals:**
- Renomear uma gravação selecionada para um nome significativo
- Validar o nome antes de habilitar `Salvar`
- Preservar extensão `.aac`, conteúdo e localização do arquivo
- Manter seleção, snackbars e recarga da lista consistentes
- Preservar a data exibida no card após o rename
- Cobrir regras, repositório e ViewModel com testes unitários

**Non-Goals:**
- Renomear múltiplas gravações
- Editar metadados de áudio
- Mover arquivos entre pastas
- Alterar o formato de gravação ou o fluxo de compartilhamento
- Corrigir o estado de playback no delete nesta mudança

## Decisions

### 1. Regra de nome compartilhada

**Decisão**: Criar `RecordingFileRules` com `normalizeName()` e `isValidName()`.

**Razão**: A UI precisa desabilitar `Salvar` e o repositório precisa rejeitar nomes inválidos. Uma regra única evita divergência.

**Regras:**
- `trim()` e remoção de `.aac` final
- não vazio
- diferente do nome atual
- sem `/`, `\`, `:`, `*`, `?`, `"`, `<`, `>`, `|` ou caracteres de controle
- não pode ser `.` nem `..`
- não pode começar nem terminar com `.`
- no máximo 100 caracteres

### 2. Dialog de rename

**Decisão**: Criar `RenameRecordingDialog` com `TextFieldValue`, `FocusRequester` e seleção total do nome atual.

**Razão**: Atende ao requisito de o nome atual aparecer selecionado e permite edição imediata. `Salvar` usa `isValidName()` como única fonte de verdade.

### 3. Rename no repositório

**Decisão**: `renameRecording()` valida a origem, normaliza o nome, trata nome igual como no-op, rejeita destino existente e usa `renameTo()` no mesmo diretório.

**Razão**: Mantém a operação simples e atômica no filesystem. Nome igual não deve gerar erro quando chamado programaticamente, embora a UI desabilite `Salvar`.

### 4. ViewModel e playback

**Decisão**: Adicionar `renameResult`, `renameRecording()` e `clearRenameResult()`. Usar um `Mutex` compartilhado com delete. Parar o playback se o arquivo renomeado for o atual.

**Razão**: Serializa operações de arquivo e mantém `isPlaying`/`currentPlayingPath` consistentes.

### 5. Data no card

**Decisão**: Tentar o padrão `lugrav_yyyyMMdd_HHmmss`; se não corresponder, usar `File.lastModified()`.

**Razão**: Nomes personalizados não contêm timestamp, mas o card deve continuar exibindo uma data útil.

### 6. Fluxo de UI

**Decisão**: O item Renomear fica no `DropdownMenu` entre Compartilhar e Excluir. O dialog é controlado por `LugravScreen`. Sucesso limpa a seleção e mostra snackbar; falha mantém a seleção e mostra erro.

## Risks / Trade-offs

- **Nome duplicado**: rejeitado com snackbar genérico de erro; não há sufixo automático.
- **Rename de arquivo em reprodução**: o playback é interrompido antes da operação; se o rename falhar, o áudio permanece parado.
- **Data fallback**: usa `lastModified()`, que pode mudar em cópias/restaurações; é aceitável para o requisito.
- **Validação conservadora**: caracteres como `:` são rejeitados mesmo sendo válidos no Android, para evitar problemas ao exportar/compartilhar.

## Migration Plan

Nenhuma migração de dados. Arquivos existentes continuam válidos. Rollback consiste em remover o dialog, a ação do menu, os métodos de rename e os testes adicionados.

## Open Questions

Nenhuma.
