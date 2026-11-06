## Context

O modo de seleção é ativado por onLongPress em um RecordingCard, armazenando o caminho em selectedFilePath. A LugravTopBar exibe ArrowBack (via onClearSelection) e um menu de contexto quando selectedFilePath != null. O toque em card não selecionado abre RecordingBottomSheet para reprodução.

## Goals / Non-Goals

**Goals:**
- Restringir a saída do modo de seleção a três ações explícitas: ArrowBack, toque no card selecionado e botão nativo voltar
- Trocar a seleção ao tocar em outro card durante o modo de seleção
- Evitar qualquer alteração de estado de reprodução enquanto o modo de seleção estiver ativo

**Non-Goals:**
- Alterar o fluxo de reprodução fora do modo de seleção
- Adicionar Snackbar ou strings para tentativas de reprodução bloqueadas
- Seleção múltipla

## Decisions

### 1. Saída do modo de seleção
- **Decisão**: Sair apenas via onClearSelection (ArrowBack), toque no card já selecionado, ou BackHandler do botão nativo voltar.
- **Razão**: Ações explícitas e previsíveis; evita saídas acidentais.

### 2. Troca de seleção
- **Decisão**: Com seleção ativa, tocar em outro card atualiza selectedFilePath para o novo caminho.
- **Razão**: O estado deve refletir o card realmente selecionado, sem iniciar reprodução.

### 3. Bloqueio de reprodução
- **Decisão**: Em modo de seleção, o clique nunca define selectedRecording, nunca chama reprodução e não altera isPlaying/currentPlayingPath.
- **Razão**: Manter os estados fiéis ao estado real da aplicação.

### 4. Botão nativo voltar
- **Decisão**: Usar BackHandler(enabled = selectedFilePath != null) para limpar a seleção.
- **Razão**: Intercepta o voltar somente no modo de seleção, preservando o comportamento padrão fora dele.

## Risks / Trade-offs

- **Descoberta de saída**: O usuário precisa conhecer as três formas de sair; a seta ArrowBack é a principal affordance visual.
- **Sem feedback de bloqueio**: Não haverá Snackbar ao tocar em cards durante a seleção; a troca de seleção é o próprio feedback.

## Migration Plan

Nenhuma migração necessária; mudanças incrementais.

## Open Questions

Nenhuma.
