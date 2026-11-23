## Context

Ver `proposal.md` para motivação. O fluxo atual salva a gravação em `stopAndSave()` com um nome sugerido e já permite renomear e associar tags por ações separadas. A revisão pós-gravação precisa reaproveitar essas operações sem alterar schema, DAO ou migração.

## Goals / Non-Goals

**Goals:**
- Exibir a revisão assim que o arquivo for salvo, antes de recarregar a lista.
- Manter o arquivo já salvo como fonte da verdade durante toda a revisão.
- Reaproveitar rename e associação de tags existentes.
- Garantir rollback do rename se a persistência das tags falhar.
- Cobrir o novo comportamento com testes unitários de ViewModel e Repository.

**Non-Goals:**
- Alterar o schema do Room, DAO ou migrações.
- Criar testes de componentes Compose, `LugravScreen` ou `RecordingReviewDialog`.
- Testar `RecordingFileRules` diretamente; a validação será exercitada pelos testes do ViewModel.
- Alterar o comportamento visual do modo de exclusão de tags nesta change.

## Decisions

### Manter o arquivo salvo durante a revisão

`stopAndSave()` continua salvando imediatamente com o nome sugerido. O ViewModel expõe `recordingReviewPath` e a lista só é recarregada após salvar, descartar ou fechar.

Alternativa considerada: adiar o salvamento até a revisão. Rejeitada porque aumentaria o estado temporário e exigiria lidar com gravação em memória, perda de processo e limpeza de arquivos parciais.

### Rename e tags orquestrados no Repository

Adicionar `AudioRecordingRepository.saveRecordingReview(filePath, newName, tagNames): String`. O método renomeia somente se o nome mudou, persiste as tags no caminho final e tenta reverter o rename se a persistência das tags falhar.

Alternativa considerada: orquestrar no ViewModel. Rejeitada porque o rollback é uma responsabilidade de persistência e deve ficar no Repository, onde os testes unitários já cobrem arquivos e stores.

### Validação de nome para revisão

Adicionar `RecordingFileRules.isValidRecordingName(raw)` para aceitar o nome sugerido e nomes novos válidos. O dialog usa a regra para habilitar `Salvar`; o ViewModel também valida antes de chamar o Repository.

Alternativa considerada: reutilizar `isValidName` com o nome atual. Rejeitada por tornar a chamada ambígua e espalhar a regra de “mesmo nome é válido” pela UI.

### Estado e eventos no ViewModel

O ViewModel expõe `recordingReviewPath`, `isRecordingReviewInProgress` e `recordingReviewResult`, além de `saveRecordingReview`, `discardRecordingReview`, `keepRecordingReview` e `clearRecordingReviewResult`.

`finalizeRecording()` passa a publicar o caminho salvo em vez de emitir snackbar imediato. A UI observa `recordingReviewPath` para abrir o dialog e usa `recordingReviewResult` para feedback e recarga da lista.

Alternativa considerada: eventos pontuais via `Channel`. Rejeitada para manter o padrão de estado já usado no ViewModel e simplificar os testes.

### Descartar reutiliza deleteRecording

`discardRecordingReview()` chama `deleteRecording(path)`, que já remove arquivo e metadata. Não há necessidade de um novo método no Repository.

Alternativa considerada: método dedicado `discardRecordingReview`. Rejeitada por duplicar uma operação já coberta.

### Estratégia de testes

Testes apenas de métodos de ViewModel e Repository:
- ViewModel: finalizar, salvar, descartar, manter, nome inválido e falhas.
- Repository: rename opcional, tags vazias, persistência de tags e rollback.

Sem testes de UI e sem testes diretos de `RecordingFileRules`.

## Risks / Trade-offs

- [Arquivo órfão se o processo morrer durante a revisão] → O arquivo permanece salvo com o nome sugerido; o usuário pode encontrá-lo na lista na próxima abertura.
- [Rollback do rename pode falhar] → O Repository propaga o erro e mantém o arquivo onde estiver; a UI mostra falha e não fecha a revisão.
- [Estado de revisão preso após erro] → `recordingReviewPath` só é limpo em sucesso ou ao fechar; o usuário pode tentar novamente ou descartar.
- [Validação duplicada entre dialog e ViewModel] → O ViewModel é a barreira final; a UI apenas melhora a experiência.

## Migration Plan

Não há migração de dados. A mudança é compatível com gravações e tags existentes. Rollback consiste em remover o dialog e voltar a exibir o feedback imediato em `finalizeRecording()`.
