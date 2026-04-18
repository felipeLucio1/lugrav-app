## Context

O app Lugrav já possui funcionalidade de delete implementada com long press na RecordingCard. Agora precisa adicionar funcionalidade de share usando a abordagem oficial do Android (FileProvider + Intent.ACTION_SEND).

O app usa:
- Scoped storage com `getExternalFilesDir(null)` - diretório privado do app
- Arquivos AAC salvos em `lugrav/` dentro do diretório externo do app
- Jetpack Compose para UI

## Goals / Non-Goals

**Goals:**
- Configurar FileProvider para permitir compartilhamento de arquivos AAC
- Exibir ícone de share na toolbar quando em modo delete
- Implementar Intent.ACTION_SEND com content URI gerado pelo FileProvider
- Seguir estilo do design.md (ícones brancos na toolbar)

**Non-Goals:**
- Compartilhamento múltiplo (vários arquivos)
- Preview do arquivo antes de compartilhar
- Testes unitários ou de UI

## Decisions

### 1. FileProvider vs direct file path

Decisão: Usar FileProvider

Alternativas consideradas:
- Uri.fromFile() - documentado como não seguro, não funciona em scoped storage
- FileProvider - abordagem oficial recomendada pela documentação Android

Rationale: Documentação oficial Android recomenda FileProvider para compartilhamento seguro entre apps. O Uri.fromFile() força o app receptor a ter READ_EXTERNAL_STORAGE e não funciona com scoped storage.

### 2. External-files-path vs files-path

Decisão: Usar `<external-files-path>` no filepaths.xml

Rationale: O app usa `getExternalFilesDir(null)` que retorna o diretório específico do app em armazenamento externo. O elemento correto para este caso é `external-files-path`.

### 3. Intent.ACTION_SEND vs ShareCompat

Decisão: Usar Intent.ACTION_SEND diretamente

Alternativa considerada:
- ShareCompat.IntentBuilder - recomendado em alguns artigos para lidar com idiosyncrasias

Rationale: A documentação oficial de "Send binary content" mostra o uso direto de Intent.ACTION_SEND com EXTRA_STREAM e FLAG_GRANT_READ_URI_PERMISSION. É a abordagem mais direta e documentada.

### 4. Posição do ícone share na toolbar

Decisão: Share à esquerda do delete (ordem: share, delete)

Rationale: Segue convenção comuns de UI onde ações menos destrutivas vêm primeiro. O delete é a ação mais crítica (confirmação required), enquanto share é mais simples.

## Risks / Trade-offs

- [Risk] Apps receptores não terem handler para audio/aac → [Mitigation] O sistema Android Sharesheet mostra apenas apps compatíveis; se nenhum app suportar, mostra mensagem de erro

- [Risk] Permission granted to wrong app → [Mitigation] FLAG_GRANT_READ_URI_PERMISSION é temporário e expira quando a pilha de tasks do app receptor termina

- [Risk] Arquivo deletado enquanto está sendo compartilhado → [Mitigation] O content URI é gerado no momento do clique; se arquivo for deletado depois, app receptor akan dapat erro

## Migration Plan

1. Adicionar FileProvider no manifest (não requer migration de dados)
2. Criar filepaths.xml (não requer migration)
3. Modificar UI (não requer migration)
4.Deploy como update normal

Rollback: Remover provider do manifest e arquivos de configuração, reverter modificações na UI. Sem impacto em dados existentes.