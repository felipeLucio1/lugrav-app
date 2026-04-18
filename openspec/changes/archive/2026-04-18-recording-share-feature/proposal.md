## Why

Os usuários precisam poder compartilhar suas gravações de áudio com outros aplicativos (WhatsApp, email, etc). A documentação oficial do Android recomenda usar FileProvider + Intent.ACTION_SEND para compartilhamento seguro de arquivos entre aplicativos.

## What Changes

- Configurar FileProvider no AndroidManifest.xml para permitir compartilhamento de arquivos
- Criar arquivo res/xml/filepaths.xml especificando o diretório de gravações (lugrav/)
- Modificar LugravTopBar para exibir ícone de share (Icons.Filled.Share) ao lado do ícone de delete quando em modo delete
- Adicionar estados e lógica de compartilhamento na LugravScreen
- Implementar função de share usando Intent.ACTION_SEND com FileProvider para gerar content URI
- Adicionar string resource para o botão de share

## Capabilities

### New Capabilities

- `recording-share`: Funcionalidade para compartilhar gravações de áudio via Intent.ACTION_SEND com outros aplicativos

### Modified Capabilities

- Nenhuma modificação em capacidades existentes (a feature de delete já existe)

## Impact

- AndroidManifest.xml: Adicionar provider FileProvider
- res/xml/filepaths.xml: **NOVO** arquivo de configuração de caminhos
- LugravTopBar.kt: Adicionar parâmetro onShareClick e segundo ícone na toolbar
- LugravScreen.kt: Estados para selectedFileToShare + função de share
- strings.xml: Adicionar string "share_button"
- AudioRecordingRepository.kt: Sem alterações necessárias
- AudioRecordingViewmodel.kt: Sem alterações necessárias (lógica toda na UI)