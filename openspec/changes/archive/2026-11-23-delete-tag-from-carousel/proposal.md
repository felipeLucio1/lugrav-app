## Why

As tags podem ser criadas e associadas às gravações, mas não há fluxo na interface para excluí-las. A operação `deleteTag` já existe no ViewModel e no repositório, porém não está conectada à UI e a implementação Room atual não envolve explicitamente a remoção das associações em uma transação.

## What Changes

- Adicionar modo de exclusão de tags ativado por long press em qualquer badge de tag.
- Exibir um ícone de `X` antes do ponto colorido e do nome em todas as tags durante o modo.
- Abrir um dialog de confirmação ao tocar no `X`, com a mensagem `Deseja excluir a tag <nome da tag>?`.
- O botão `Não` cancela a exclusão; o botão `Sim` executa.
- Bloquear interações fora do carrossel durante o modo: o toque apenas encerra o modo, sem abrir gravação, iniciar gravação ou executar outras ações.
- Remover a tag e suas associações em uma transação atômica, preservando as gravações.
- Recarregar as listas de tags e gravações após a exclusão.
- Exibir snackbar de sucesso com a mensagem `Tag <nome da tag> excluída com sucesso`, com duração curta.
- Manter os fluxos existentes de filtro, criação e associação de tags.

## Capabilities

### Modified Capabilities

- `recording-list`: modo de exclusão, confirmação, bloqueio de interações externas e feedback visual.
- `recording-tags`: exclusão atômica da tag e das associações, preservando as gravações.

## Impact

- `app/src/main/java/com/felipelucio/lugrav/data/room/RecordingTagDao.kt`
- `app/src/main/java/com/felipelucio/lugrav/data/room/RoomRecordingTagStore.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingViewmodel.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/TagBadge.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/TagCarousel.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/DeleteTagConfirmationDialog.kt` (novo)
- `app/src/main/res/values/strings.xml`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingViewModelTest.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
