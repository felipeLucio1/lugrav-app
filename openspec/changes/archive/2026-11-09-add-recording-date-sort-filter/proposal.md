## Why

A lista de gravações é exibida na ordem retornada por `listFiles()`, sem ordenação explícita por data e hora. O card recalcula a data a partir do nome do arquivo e perde a referência após um rename. Além disso, não existe um ponto de extensão para filtros futuros, como tags do usuário.

## What Changes

- Adicionar `createdAtMillis` ao `AudioRecordingModel`
- Preencher `createdAtMillis` com `File.lastModified()` no `AudioRecordingRepository`
- Ordenar a lista por `createdAtMillis` decrescente, com desempate determinístico por `path`
- Criar a interface funcional `RecordingFilter` com a instância padrão `RecordingFilter.All`
- Adicionar parâmetro de filtro em `getRecordingsList()`
- Propagar e preservar o filtro ativo no `AudioRecordingViewModel`
- Exibir data e hora no `RecordingCard` a partir de `createdAtMillis`
- Remover o parsing de data por nome de arquivo do card
- Adicionar testes de ordenação, filtro e propagação no ViewModel

## Capabilities

### New Capabilities
- `recording-list`: data de criação baseada em `lastModified`, ordenação da lista, filtro extensível e exibição de data/hora

### Modified Capabilities
- `repository-interfaces`: `getRecordingsList()` passa a aceitar um `RecordingFilter`
- `repository-unit-tests`: cenários de ordenação por `lastModified` e filtro
- `viewmodel-unit-tests`: cenários de propagação e preservação do filtro ativo

## Impact

- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingModel.kt`
- `app/src/main/java/com/felipelucio/lugrav/RecordingFilter.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingRepository.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingViewmodel.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/RecordingCard.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingViewModelTest.kt`
- `openspec/specs/recording-list/spec.md` (após arquivamento)
- `openspec/specs/repository-interfaces/spec.md` (após arquivamento)
- `openspec/specs/repository-unit-tests/spec.md` (após arquivamento)
- `openspec/specs/viewmodel-unit-tests/spec.md` (após arquivamento)
- Sem novas dependências e sem alterações no formato dos arquivos
