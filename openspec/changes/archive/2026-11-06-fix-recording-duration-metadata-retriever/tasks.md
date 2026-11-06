## 1. Implementar ciclo de vida do retriever
- [x] 1.1 Alterar `MetadataRetrieverImpl` para criar uma nova `MediaMetadataRetriever` a cada `setDataSource()`
- [x] 1.2 Liberar a instância anterior antes de criar a nova
- [x] 1.3 Liberar a nova instância quando `setDataSource()` falhar e propagar a exceção
- [x] 1.4 Tornar `extractDuration()` tolerante a exceções retornando `null`
- [x] 1.5 Tornar `release()` idempotente e seguro

## 2. Robustez do repositório
- [x] 2.1 Mover `metadataRetriever.release()` para o bloco `finally` em `getAudioDuration()`
- [x] 2.2 Serializar `getAudioDuration()` com `@Synchronized`

## 3. Testes de regressão
- [x] 3.1 Verificar `release()` quando `extractDuration()` falha
- [x] 3.2 Adicionar teste quando `setDataSource()` falha
- [x] 3.3 Adicionar teste quando `extractDuration()` retorna `null`
- [x] 3.4 Adicionar teste com múltiplos arquivos
- [x] 3.5 Adicionar teste de formatação de `65000ms` para `00:01:05`

## 4. Validação
- [x] 4.1 Compilar com `:app:compileDebugKotlin`
- [x] 4.2 Executar `:app:testDebugUnitTest`
- [x] 4.3 Validar manualmente em device/emulador: gravar 2+ áudios, recarregar a lista e conferir durações
