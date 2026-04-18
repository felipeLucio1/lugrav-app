## ADDED Requirements

### Requirement: FileProvider configured for audio sharing
O sistema SHALL configurar um FileProvider no AndroidManifest.xml para gerar content URIs de arquivos de áudio AAC.

#### Scenario: FileProvider declared in manifest
- **WHEN** app é instalado
- **THEN** FileProvider está registrado no manifest com authorities "${applicationId}.fileprovider"

#### Scenario: File paths configured
- **WHEN** FileProvider precisa gerar URI para arquivo
- **THEN** filepaths.xml define caminho "lugrav/" em external-files-path

### Requirement: Share icon visible in toolbar during delete mode
O sistema SHALL exibir um ícone de compartilhamento na TopBar quando o modo delete está ativo.

#### Scenario: Delete mode activated
- **WHEN** usuário executa long press em uma gravação
- **THEN** TopBar exibe ícone de share (esquerda) e ícone de delete (direita)

#### Scenario: Delete mode deactivated
- **WHEN** usuário fecha o modo delete (dialog confirmado ou cancelado)
- **THEN** ícones de share e delete desaparecem da TopBar

### Requirement: Share intent launches with audio file
O sistema SHALL criar e lançar um Intent.ACTION_SEND com o arquivo de áudio quando o usuário clica no ícone de share.

#### Scenario: User clicks share icon
- **WHEN** usuário clica no ícone de share na TopBar
- **THEN** sistema gera content URI via FileProvider para o arquivo AAC
- **THEN** Intent.ACTION_SEND é criado com type "audio/aac" e EXTRA_STREAM contendo o content URI
- **THEN** FLAG_GRANT_READ_URI_PERMISSION é adicionado ao Intent
- **THEN** Android Sharesheet é exibido com apps compatíveis

#### Scenario: No compatible apps available
- **WHEN** usuário clica em share mas nenhum app suporta audio/aac
- **THEN** Android exibe mensagem indicando que nenhum app está disponível

### Requirement: Share icon follows design style
O sistema SHALL exibir o ícone de share na cor branca, conforme especificado no design.md.

#### Scenario: Share icon rendered
- **WHEN** ícone de share é renderizado na TopBar
- **THEN** a cor do ícone é MaterialTheme.colorScheme.onPrimary (branco)