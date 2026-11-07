## Why

As gravações são salvas com nomes baseados em timestamp (`lugrav_yyyyMMdd_HHmmss`), o que dificulta identificar o conteúdo. O menu de contexto atual oferece apenas Compartilhar e Excluir. Os usuários precisam renomear uma gravação selecionada para um nome significativo sem sair do fluxo de seleção.

## What Changes

- Adicionar a ação `Renomear` ao menu de contexto da gravação selecionada
- Exibir um dialog com o nome atual já selecionado na caixa de texto
- Adicionar os botões `Cancelar` e `Salvar`
- Desabilitar `Salvar` quando o nome estiver vazio, for igual ao atual ou não atender às regras de nome
- Criar uma regra compartilhada de normalização e validação de nomes
- Renomear o arquivo no mesmo diretório, preservando a extensão `.aac` e o conteúdo
- Rejeitar nomes duplicados com mensagem de erro
- Parar a reprodução quando o arquivo renomeado for o que está tocando
- Preservar a data exibida no card após o rename, usando `lastModified()` como fallback
- Adicionar testes de validação, repositório e ViewModel
- Manter interfaces, Koin e formato dos arquivos inalterados

## Capabilities

### New Capabilities
- `recording-rename`: ação de renomear gravação, dialog, validação de nome e persistência do novo nome

### Modified Capabilities
- `recording-context-menu`: o menu de contexto passa a exibir também a ação Renomear
- `repository-unit-tests`: cenários de teste para `renameRecording()`
- `viewmodel-unit-tests`: cenários de teste para o estado e a ação de rename

## Impact

- `app/src/main/java/com/felipelucio/lugrav/RecordingFileRules.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingRepository.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingViewmodel.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/LugravTopBar.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/RecordingCard.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/RenameRecordingDialog.kt` (novo)
- `app/src/main/res/values/strings.xml`
- `app/src/test/java/com/felipelucio/lugrav/RecordingFileRulesTest.kt` (novo)
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingViewModelTest.kt`
- `openspec/specs/recording-rename/spec.md` (após arquivamento)
- `openspec/specs/recording-context-menu/spec.md` (após arquivamento)
- `openspec/specs/repository-unit-tests/spec.md` (após arquivamento)
- `openspec/specs/viewmodel-unit-tests/spec.md` (após arquivamento)
- Sem novas dependências e sem mudanças de API pública
