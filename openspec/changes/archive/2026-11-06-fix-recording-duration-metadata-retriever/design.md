## Context

O `MetadataRetrieverImpl` é registrado como `single` no Koin e mantém uma única instância de `MediaMetadataRetriever`. O `AudioRecordingRepository.getAudioDuration()` chama `setDataSource()`, `extractDuration()` e `release()` para cada arquivo. Depois do primeiro `release()`, a instância não deve ser reutilizada. A documentação oficial do Android diz que `release()` libera os recursos adquiridos e deve ser chamado quando o objeto não será mais usado.

Antes da refatoração para interfaces, o repositório criava uma nova `MediaMetadataRetriever` por extração. A correção restaura esse comportamento dentro da implementação, preservando a interface e a injeção de dependência.

## Goals / Non-Goals

**Goals:**
- Corrigir a duração zerada em recargas da lista
- Garantir liberação de recursos em todos os caminhos, inclusive falhas
- Manter o contrato `MetadataRetriever` e o Koin inalterados
- Adicionar testes de regressão no limite testável do repositório

**Non-Goals:**
- Migrar para `androidx.media3.inspector.MetadataRetriever`
- Usar `MediaPlayer` como extrator principal de duração
- Alterar o fluxo de reprodução ou o formato dos arquivos
- Mover `loadRecordings()` para `Dispatchers.IO` nesta mudança

## Decisions

### 1. Nova instância por `setDataSource()`

**Decisão**: `MetadataRetrieverImpl.setDataSource()` libera a instância anterior e cria uma nova `MediaMetadataRetriever`.

**Razão**: É o padrão documentado e restaura o comportamento anterior à refatoração. Evita reutilizar um objeto já liberado.

**Alternativas consideradas:**
- Reutilizar a mesma instância e chamar `release()` apenas no fim da lista: rejeitado porque o contrato atual do repositório libera por arquivo e não há lifecycle claro para o singleton.
- Mudar a interface para `extractDuration(path)`: rejeitado nesta mudança para manter o escopo mínimo; pode ser avaliado depois.

### 2. `release()` idempotente

**Decisão**: `release()` libera a instância atual com `runCatching` e define a referência como `null`.

**Razão**: O repositório pode chamar `release()` no `finally` mesmo depois de uma falha em `setDataSource()`. A operação precisa ser segura para chamadas repetidas e para a exceção de `release()` em API 33+.

### 3. Liberação no `finally`

**Decisão**: `getAudioDuration()` move `release()` para um bloco `finally`.

**Razão**: Garante limpeza mesmo quando `setDataSource()` ou `extractDuration()` falham.

### 4. Serialização da extração

**Decisão**: `getAudioDuration()` é anotado com `@Synchronized`.

**Razão**: O `MetadataRetriever` continua sendo um singleton. A serialização evita que duas cargas concorrentes intercalem `setDataSource()` e `release()` na mesma instância.

### 5. Compatibilidade com `minSdk 24`

**Decisão**: Usar `release()` em vez de `close()`/`use`.

**Razão**: `close()` foi adicionado apenas na API 29; `release()` está disponível desde a API 10.

## Risks / Trade-offs

- **Duração ausente em ADTS**: Se o primeiro item também estiver `00:00:00`, o formato pode não expor duração no cabeçalho. Nesse caso, a correção de lifecycle não basta; o próximo passo seria fallback com `MediaPlayer` ou migração para Media3.
- **Serialização**: `@Synchronized` pode bloquear extrações concorrentes, mas a lista é processada sequencialmente e o custo é aceitável.
- **Teste da implementação Android**: `MetadataRetrieverImpl` depende de `MediaMetadataRetriever` real; os testes de regressão cobrem o repositório via interface mockada, e a validação final é manual no device/emulador.

## Migration Plan

Nenhuma migração de dados. Alterações incrementais e compatíveis com a interface existente. Rollback consiste em reverter os três arquivos de código e remover o change.

## Open Questions

Nenhuma.
