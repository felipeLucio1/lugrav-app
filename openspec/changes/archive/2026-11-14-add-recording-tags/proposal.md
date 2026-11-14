## Why

As gravações são descobertas apenas por arquivos `.aac` no diretório do app. Não existe persistência de metadados nem forma de associar tags do usuário às gravações. Para suportar tags com segurança, a camada de dados precisa de um banco local sincronizado com os arquivos, mantendo o arquivo como fonte da verdade.

## What Changes

- Adicionar Room 3 como banco local de metadados e relações
- Criar `UserTag` e expor `tags` em `AudioRecordingModel`
- Criar `RecordingEntity`, `TagEntity` e `RecordingTagCrossRef`
- Criar `RecordingWithTags` como DTO de leitura many-to-many
- Criar DAOs de gravação, tag e relação
- Criar `LugravDatabase` com `AndroidSQLiteDriver`
- Criar `RecordingTagStore` e `RoomRecordingTagStore` para isolar Room do repositório
- Sincronizar arquivos `.aac` com o banco em `getRecordingsList()`
- Tornar `stopAndSave()`, `deleteRecording()` e `renameRecording()` operações `suspend` consistentes com o banco
- Adicionar operações de criar, listar, atualizar, excluir e associar tags
- Implementar associação de tags por substituição atômica com `setRecordingTags`
- Criar carrossel de tags, `NewTagDialog` e `SelectTagsDialog`
- Exibir tags nos cards de gravação
- Implementar filtro de gravações por tags com semântica AND
- Adicionar a ação "Selecionar tags" ao menu contextual
- Atualizar testes de repositório e ViewModel

## Capabilities

### New Capabilities
- `recording-tags`: persistência de tags, associação many-to-many com gravações e sincronização entre arquivos e banco

### Modified Capabilities
- `recording-list`: o modelo de gravação passa a expor as tags associadas, o carrossel, a criação e o filtro por tags
- `recording-context-menu`: o menu contextual passa a oferecer "Selecionar tags"
- `repository-interfaces`: o repositório passa a persistir metadados, tags e filtros
- `repository-unit-tests`: cenários de sincronização, tags, filtros, cascata e operações `suspend`
- `viewmodel-unit-tests`: cenários de carregamento, criação, exclusão, filtro e associação de tags

## Impact

- `gradle/libs.versions.toml`
- `build.gradle.kts`
- `app/build.gradle.kts`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingModel.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingRepository.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingViewmodel.kt`
- `app/src/main/java/com/felipelucio/lugrav/RecordingFilter.kt` (removido; filtro por tags substitui o legado)
- `app/src/main/java/com/felipelucio/lugrav/UserTag.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/TagRules.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/data/room/RecordingTagStore.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/data/room/*` (novo)
- `app/src/main/java/com/felipelucio/lugrav/view/components/TagBadge.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/view/components/TagCarousel.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/view/components/NewTagDialog.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/view/components/SelectTagsDialog.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/view/components/LugravTopBar.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/RecordingCard.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/java/com/felipelucio/lugrav/di/AppModule.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingViewModelTest.kt`
- `app/src/test/java/com/felipelucio/lugrav/TagRulesTest.kt` (novo)
- `openspec/specs/recording-tags/spec.md` (após arquivamento)
- `openspec/specs/recording-list/spec.md` (após arquivamento)
- `openspec/specs/recording-context-menu/spec.md` (após arquivamento)
- `openspec/specs/repository-interfaces/spec.md` (após arquivamento)
- `openspec/specs/repository-unit-tests/spec.md` (após arquivamento)
- `openspec/specs/viewmodel-unit-tests/spec.md` (após arquivamento)
