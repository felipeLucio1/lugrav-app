## Context

O app grava arquivos AAC no diretório `lugrav` e lista os arquivos diretamente do sistema de arquivos. O `AudioRecordingRepository` já depende de interfaces para gravação, reprodução, metadados e diretórios, mas não tem persistência de metadados nem relações.

A feature de tags precisa de uma relação many-to-many entre gravações e tags, além de UI para criar tags, associá-las a gravações e filtrar a lista. O arquivo `.aac` continua sendo a fonte da verdade; o banco local guarda metadados e associações e é reconciliado a cada listagem.

## Goals / Non-Goals

**Goals:**
- Persistir gravações e tags localmente com Room 3
- Sincronizar arquivos existentes com o banco
- Expor tags em `AudioRecordingModel`
- Manter Room isolado de modelo, ViewModel e repositório
- Preservar o comportamento atual de gravação, reprodução, rename e delete
- Criar UI de criação, associação e exibição de tags
- Permitir filtrar a lista por uma ou mais tags
- Cobrir dados, repositório e ViewModel com testes

**Non-Goals:**
- UI de edição, renomeação ou exclusão de tags
- Migração de banco existente
- Alterar o formato ou o nome dos arquivos
- Sincronização remota

## Decisions

### 1. Room 3 com KSP

**Decisão**: Usar Room 3.0.3, KSP 2.3.10 e `androidx.sqlite:sqlite-framework` 2.7.1.

**Razão**: Room 3 é a versão estável atual, usa o pacote `androidx.room3`, exige KSP e é compatível com o Kotlin 2.2.10 e AGP 9.1 do projeto. O KSP 2.3.10 é a versão documentada para KGP 2.2.10–2.3.x e AGP 8.12+.

### 2. Arquivos como fonte da verdade

**Decisão**: `getRecordingsList()` varre os arquivos, monta os metadados e chama `RecordingTagStore.syncRecordings()` antes de consultar o banco.

**Razão**: O banco pode ficar defasado após operações externas, falhas de escrita ou restaurações. A sincronização insere arquivos novos, atualiza duração/timestamp e remove registros de arquivos que não existem mais.

### 3. Entidades e DTO de leitura

**Decisão**: Persistir `RecordingEntity`, `TagEntity` e `RecordingTagCrossRef`. Usar `RecordingWithTags` apenas como DTO de consulta.

**Razão**: `RecordingTagCrossRef` é a tabela de junção. `RecordingWithTags` não é tabela nem entidade; é um data class com `@Embedded` e `@Relation`/`Junction` para o Room retornar uma gravação com sua lista de tags em uma transação. Sem esse DTO, seria necessário um `JOIN` manual com outra projeção ou consultas N+1.

### 4. Store para isolar Room

**Decisão**: Criar `RecordingTagStore` e implementar `RoomRecordingTagStore` com os DAOs.

**Razão**: `AudioRecordingRepository` depende apenas da interface e de modelos de domínio. Nem o repositório nem o ViewModel importam `androidx.room3` ou entidades.

### 5. Operações de arquivo e banco

**Decisão**: Tornar `stopAndSave()`, `deleteRecording()` e `renameRecording()` `suspend`.

**Razão**: As operações de banco do Room 3 são `suspend`. O ViewModel executa essas chamadas em `Dispatchers.IO`.

**Trade-off**: Arquivo e banco não são uma transação única. Em rename, uma falha no banco tenta reverter o rename do arquivo. Em delete, o próximo `getRecordingsList()` remove a linha órfã. Em `stopAndSave()`, a próxima listagem faz o backfill se a inserção falhar.

### 6. Identidade e validação de tags

**Decisão**: Tags são identificadas pelo nome no domínio; internamente o banco usa `id` auto-gerado. O nome é normalizado com `trim()`, limitado a 30 caracteres, não pode conter caracteres de controle e a criação rejeita duplicidade case-insensitive. A cor deve seguir `#RRGGBB` e é normalizada para maiúsculas.

**Razão**: O nome é suficiente para a UI e evita expor IDs do banco no modelo de domínio. As relações persistidas usam IDs e são preservadas em rename de gravação ou tag. A validação case-insensitive evita tags visualmente duplicadas.

### 7. Filtro por tags com semântica AND

**Decisão**: Manter `selectedTagFilters` no ViewModel, expor `getRecordingsByTags(tagNames)` no repositório e usar `getRecordingsWithAllTags` no DAO. O carrossel alterna filtros e a lista é recarregada sem nova varredura de arquivos.

**Razão**: O filtro é local e não deve pagar o custo de sincronizar arquivos a cada toque. A semântica AND é a esperada ao selecionar múltiplas tags: a gravação precisa ter todas as tags selecionadas. O `RecordingFilter.kt` legado foi removido porque seus testes referenciavam APIs inexistentes; o filtro atual é baseado em tags.

### 8. Associação por substituição atômica

**Decisão**: Usar `setRecordingTags(recordingPath, tagNames)` para substituir o conjunto completo de tags de uma gravação em uma transação de escrita. Um conjunto vazio remove todas as associações.

**Razão**: A UI de seleção opera sobre o conjunto final, não sobre adições e remoções individuais. A transação evita estados parciais e garante que tags desconhecidas abortem a operação sem alterar as associações existentes.

## Risks / Trade-offs

- **Sincronização com I/O**: a listagem continua extraindo duração de cada arquivo; o volume atual é pequeno.
- **Banco defasado**: falhas de escrita são reconciliadas na próxima listagem.
- **Unicidade de nome**: a criação é validada case-insensitive na UI e no repositório; o índice único do banco é exato.
- **Sem migração**: o banco é criado na versão 1; não há dados anteriores.
- **Sem testes instrumentados**: a validação principal será por testes unitários com store fake; os DAOs serão validados pelo build do Room.

## Migration Plan

Nenhuma migração de dados. O banco é criado na versão 1 e os arquivos existentes são backfillados na primeira listagem. Rollback consiste em remover as dependências/plugins, entidades, DAOs, database, store e chamadas de tags; os arquivos permanecem intactos.

## Open Questions

Nenhuma.
