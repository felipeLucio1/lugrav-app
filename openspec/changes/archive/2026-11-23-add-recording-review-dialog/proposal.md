## Why

Hoje, ao finalizar uma gravação, o arquivo é salvo imediatamente com um nome gerado e sem tags, e o usuário precisa renomear e associar tags depois por ações separadas. O fluxo pós-gravação deve permitir revisar nome e tags antes de concluir, aproveitando o arquivo já salvo e as operações existentes de rename e associação de tags.

## What Changes

- Exibir um dialog após finalizar uma nova gravação.
- Preencher e selecionar automaticamente o campo de nome com o nome sugerido do arquivo.
- Listar as tags existentes para seleção, sem tags pré-selecionadas.
- Adicionar os botões `Descartar` e `Salvar`.
- Habilitar `Salvar` quando o nome for válido, independentemente da quantidade de tags selecionadas.
- Salvar renomeando o arquivo somente quando o nome mudar e gravando as associações de tags.
- Descartar removendo o arquivo recém-salvo e seu metadata.
- Ao fechar o dialog por Back ou toque fora, manter a gravação com o nome sugerido e sem tags.
- Manter o arquivo sendo salvo imediatamente com o nome sugerido ao finalizar a gravação.
- Preservar os fluxos existentes de reprodução, renomeação e seleção de tags.

## Capabilities

### New Capabilities

- `recording-review`: dialog pós-gravação para revisar nome e tags, com salvar, descartar e manter o nome sugerido.

### Modified Capabilities

- `recording-list`: o fluxo de finalização passa a abrir a revisão antes de atualizar a lista e exibir o feedback de salvamento.
- `recording-tags`: as associações de tags de uma nova gravação passam a ser definidas na revisão pós-gravação.

## Impact

- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingViewmodel.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingRepository.kt`
- `app/src/main/java/com/felipelucio/lugrav/RecordingFileRules.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/RecordingReviewDialog.kt` (novo)
- `app/src/main/res/values/strings.xml`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingViewModelTest.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
- Sem alterações de schema, DAO ou migração.
