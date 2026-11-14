## 1. Artefatos OpenSpec
- [x] 1.1 Criar proposal, design, tasks e delta specs da change

## 2. Build
- [x] 2.1 Adicionar Room 3.0.3, KSP 2.3.10 e sqlite-framework 2.7.1 ao version catalog
- [x] 2.2 Aplicar plugins KSP e Room no projeto e configurar schema export

## 3. Modelos de domínio
- [x] 3.1 Criar `UserTag` e `TagRules`
- [x] 3.2 Adicionar `tags` a `AudioRecordingModel`

## 4. Room
- [x] 4.1 Criar `RecordingEntity`, `TagEntity`, `RecordingTagCrossRef` e `RecordingWithTags`
- [x] 4.2 Criar `RecordingDao`, `TagDao` e `RecordingTagDao`
- [x] 4.3 Criar `LugravDatabase`

## 5. Store
- [x] 5.1 Criar `RecordingTagStore` e `RecordingMetadata`
- [x] 5.2 Implementar `RoomRecordingTagStore` com sincronização, CRUD e associações

## 6. Repositório
- [x] 6.1 Injetar `RecordingTagStore` no `AudioRecordingRepository`
- [x] 6.2 Sincronizar arquivos com o banco em `getRecordingsList()`
- [x] 6.3 Persistir gravação em `stopAndSave()`
- [x] 6.4 Atualizar banco em `renameRecording()` e `deleteRecording()`
- [x] 6.5 Adicionar operações de tag no repositório

## 7. Integração
- [x] 7.1 Atualizar DI com database, DAOs e store
- [x] 7.2 Ajustar ViewModel para chamadas `suspend`
- [x] 7.3 Remover `RecordingFilter.kt` e testes de filtro

## 8. Testes
- [x] 8.1 Atualizar `AudioRecordingRepositoryTest` com store fake e `runTest`
- [x] 8.2 Atualizar `AudioRecordingViewModelTest` para `coEvery`/`coVerify`
- [x] 8.3 Adicionar testes de `TagRules`
- [x] 8.4 Adicionar testes de sincronização e operações de tag no repositório

## 9. UI de tags
- [x] 9.1 Criar `TagBadge`, `TagCarousel` e `NewTagDialog`
- [x] 9.2 Integrar carrossel, criação e filtro em `LugravScreen`
- [x] 9.3 Criar `SelectTagsDialog` e ação "Selecionar tags" no menu contextual
- [x] 9.4 Exibir tags no `RecordingCard`
- [x] 9.5 Adicionar strings da feature

## 10. Filtro e associação
- [x] 10.1 Adicionar filtros por tags ao ViewModel
- [x] 10.2 Implementar filtro AND em `getRecordingsByTags`
- [x] 10.3 Substituir associações com `setRecordingTags` atômico
- [x] 10.4 Remover `addTagToRecording`/`removeTagFromRecording`

## 11. Testes adicionais
- [x] 11.1 Atualizar testes para `setRecordingTags`
- [x] 11.2 Cobrir filtros por tags no repositório e ViewModel

## 12. Validação
- [x] 12.1 Executar `:app:compileDebugKotlin`
- [x] 12.2 Executar `:app:testDebugUnitTest`
- [x] 12.3 Executar `:app:lintDebug`
- [x] 12.4 Revisar `git diff` e `git diff --check`
