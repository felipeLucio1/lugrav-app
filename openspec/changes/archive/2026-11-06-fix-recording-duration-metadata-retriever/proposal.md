## Why

As durações exibidas nos cards de gravação ficam sempre em `00:00:00` depois que a lista é recarregada. A refatoração para interfaces passou a usar uma única instância de `MediaMetadataRetriever` no `MetadataRetrieverImpl`, mas o repositório chama `release()` após cada extração. A documentação oficial orienta liberar o recurso quando o objeto não será mais usado; reutilizar a mesma instância após `release()` não é suportado.

## What Changes

- Criar uma nova instância de `MediaMetadataRetriever` a cada `setDataSource()` no `MetadataRetrieverImpl`
- Liberar a instância anterior antes de criar a nova
- Tornar `release()` idempotente e seguro
- Garantir `release()` no `finally` do repositório, inclusive em falhas de extração
- Serializar a extração de duração para evitar uso concorrente da instância singleton
- Adicionar testes de regressão para liberação em falha e múltiplos arquivos
- Manter interface, Koin, fluxo de reprodução e formato dos arquivos inalterados

## Capabilities

### New Capabilities
- Nenhuma

### Modified Capabilities
- `repository-interfaces`: ciclo de vida do `MetadataRetriever` e liberação garantida pelo repositório
- `repository-unit-tests`: cenários de regressão para liberação e múltiplas extrações

## Impact

- `app/src/main/java/com/felipelucio/lugrav/interfaces/MetadataRetrieverImpl.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingRepository.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
- `openspec/specs/repository-interfaces/spec.md` (após arquivamento)
- `openspec/specs/repository-unit-tests/spec.md` (após arquivamento)
- Sem novas dependências e sem mudanças de API pública
