## 1. Modelo e ordenação
- [x] 1.1 Adicionar `createdAtMillis` ao `AudioRecordingModel`
- [x] 1.2 Preencher `createdAtMillis` com `File.lastModified()` no `AudioRecordingRepository`
- [x] 1.3 Ordenar a lista por `createdAtMillis` decrescente com desempate por `path`

## 2. Filtros
- [x] 2.1 Criar `RecordingFilter` como `fun interface` com `RecordingFilter.All`
- [x] 2.2 Adicionar parâmetro `filter` em `AudioRecordingRepository.getRecordingsList()`
- [x] 2.3 Propagar e preservar o filtro ativo em `AudioRecordingViewModel.loadRecordings()`

## 3. UI
- [x] 3.1 Atualizar `RecordingCard` para receber `createdAtMillis` e exibir data/hora
- [x] 3.2 Atualizar a chamada de `RecordingCard` em `LugravScreen`
- [x] 3.3 Remover o parsing de data por nome de arquivo do `RecordingCard`

## 4. Testes
- [x] 4.1 Atualizar construtores de `AudioRecordingModel` nos testes
- [x] 4.2 Adicionar testes de ordenação por `lastModified` no repositório
- [x] 4.3 Adicionar testes de filtro no repositório
- [x] 4.4 Adicionar testes de propagação e preservação do filtro no ViewModel

## 5. Validação
- [x] 5.1 Compilar com `:app:compileDebugKotlin`
- [x] 5.2 Executar `:app:testDebugUnitTest`
- [x] 5.3 Executar `:app:lintDebug`
- [x] 5.4 Revisar o diff e confirmar que o nome dos arquivos não mudou
