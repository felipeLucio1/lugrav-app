## Context

O `AudioRecordingRepository` lista os arquivos `.aac` do diretório `lugrav` e cria `AudioRecordingModel` com `path` e `duration`. A ordem atual vem de `File.listFiles()`, sem garantia de ordenação. O `RecordingCard` recalcula a data a partir do nome `lugrav_yyyyMMdd_HHmmss` e usa `lastModified()` como fallback, mas nomes personalizados após rename não preservam o padrão.

O `MetadataRetriever` é usado apenas para extrair duração. Como as gravações são AAC ADTS, metadados de data no container não são confiáveis. A mudança adota `File.lastModified()` como fonte da data/hora, conforme decisão de produto.

## Goals / Non-Goals

**Goals:**
- Ordenar a lista por data/hora decrescente antes de entregá-la à UI
- Usar `File.lastModified()` como fonte da data/hora
- Exibir data e hora no card a partir do modelo
- Criar um ponto de extensão para filtros futuros
- Preservar o filtro ativo em recargas após delete/rename
- Cobrir ordenação, filtro e propagação com testes unitários

**Non-Goals:**
- Usar `METADATA_KEY_DATE` ou alterar `MetadataRetriever`
- Implementar filtros de tags ou controles de filtro na UI
- Criar banco de dados ou sidecar de metadados
- Alterar o formato de gravação ou a criação do nome dos arquivos
- Alterar o fluxo de reprodução, delete ou rename

## Decisions

### 1. `createdAtMillis` no modelo

**Decisão**: Adicionar `createdAtMillis: Long` ao `AudioRecordingModel`.

**Razão**: A data resolvida deve acompanhar o item da lista até a UI. Epoch millis é simples, ordenável e compatível com `minSdk 24`.

### 2. `File.lastModified()` como fonte

**Decisão**: No repositório, preencher:

```kotlin
createdAtMillis = file.lastModified().takeIf { it > 0 } ?: 0L
```

**Razão**: `lastModified()` é estável para arquivos locais e não muda com rename. Valores inválidos são normalizados para `0L`, evitando datas inválidas e mantendo a ordenação determinística.

**Trade-off**: `lastModified()` é o horário de modificação, não necessariamente o de criação. Cópias/restaurações podem alterar o valor. Isso é aceitável para o requisito atual.

### 3. Ordenação no repositório

**Decisão**: Aplicar filtro e ordenação antes de retornar:

```kotlin
.filter(filter::matches)
.sortedWith(
    compareByDescending<AudioRecordingModel> { it.createdAtMillis }
        .thenBy { it.path }
)
```

**Razão**: A UI recebe uma lista pronta, imutável e determinística. O desempate por `path` evita ordem instável quando dois arquivos têm o mesmo timestamp.

### 4. Filtro extensível

**Decisão**: Criar `RecordingFilter` como `fun interface`:

```kotlin
fun interface RecordingFilter {
    fun matches(recording: AudioRecordingModel): Boolean

    companion object {
        val All = RecordingFilter { true }
    }
}
```

**Razão**: Permite filtros por lambda agora e filtros futuros por classe, como tags, sem alterar a assinatura do repositório. `All` preserva o comportamento atual.

### 5. Filtro no ViewModel

**Decisão**: Manter `private var activeFilter: RecordingFilter = RecordingFilter.All` e usar:

```kotlin
fun loadRecordings(filter: RecordingFilter = activeFilter)
```

**Razão**: O filtro pode ser aplicado no futuro sem quebrar recargas disparadas por delete/rename. Chamadas existentes continuam funcionando com `All`.

### 6. Data/hora no card

**Decisão**: `RecordingCard` recebe `createdAtMillis` e formata com `dd MMM yyyy · HH:mm`. Quando o valor é `0L`, exibe apenas a duração.

**Razão**: A UI deixa de duplicar a lógica de data e passa a refletir exatamente o valor usado na ordenação.

## Risks / Trade-offs

- **Semântica de `lastModified`**: não é creation time real; pode mudar em cópias/restaurações.
- **Timestamp zero**: arquivos sem timestamp válido ficam no fim da lista e sem data no card.
- **Filtro pós-extração**: todos os arquivos têm duração extraída antes do filtro. Para o volume atual é aceitável; filtros futuros podem exigir otimização.
- **Filtro em memória**: não há persistência do filtro entre processos; apenas dentro do ViewModel.

## Migration Plan

Nenhuma migração de dados. Arquivos existentes usam o `lastModified()` atual. Rollback consiste em remover `createdAtMillis`, `RecordingFilter`, a ordenação, a exibição de data/hora e os testes adicionados.

## Open Questions

Nenhuma.
