## 1. Regras de nome
- [x] 1.1 Criar `RecordingFileRules` com `normalizeName()` e `isValidName()`
- [x] 1.2 Adicionar testes unitários das regras de nome

## 2. Repositório
- [x] 2.1 Implementar `renameRecording()` com validação, no-op para nome igual e rejeição de duplicados
- [x] 2.2 Centralizar a extensão `.aac` em `RecordingFileRules`
- [x] 2.3 Adicionar testes de rename no `AudioRecordingRepositoryTest`

## 3. ViewModel
- [x] 3.1 Adicionar `renameResult`, `renameRecording()` e `clearRenameResult()`
- [x] 3.2 Compartilhar o mutex entre rename e delete
- [x] 3.3 Parar o playback quando o arquivo renomeado for o atual
- [x] 3.4 Adicionar testes de rename no `AudioRecordingViewModelTest`

## 4. UI
- [x] 4.1 Adicionar strings de rename
- [x] 4.2 Criar `RenameRecordingDialog` com nome atual selecionado e `Salvar` condicionado à validação
- [x] 4.3 Adicionar a ação Renomear ao `LugravTopBar`
- [x] 4.4 Integrar dialog, callbacks e snackbars no `LugravScreen`
- [x] 4.5 Atualizar `RecordingCard` para usar `lastModified()` como fallback de data

## 5. Validação
- [x] 5.1 Compilar com `:app:compileDebugKotlin`
- [x] 5.2 Executar `:app:testDebugUnitTest`
- [x] 5.3 Revisar o fluxo de UI e as mensagens de erro
