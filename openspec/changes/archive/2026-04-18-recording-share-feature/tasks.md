## 1. FileProvider Setup

- [x] 1.1 Criar arquivo res/xml/filepaths.xml com external-files-path para diretório "lugrav/"
- [x] 1.2 Adicionar provider FileProvider no AndroidManifest.xml com authorities "${applicationId}.fileprovider"

## 2. LugravTopBar - Adicionar ícone de share

- [x] 2.1 Adicionar parâmetro opcional onShareClick ao LugravTopBar
- [x] 2.2 Adicionar ícone de share (Icons.Filled.Share) nas ações da toolbar
- [x] 2.3 Exibir share à esquerda do delete quando ambos estão presentes
- [x] 2.4 Usar cor branca para o ícone (MaterialTheme.colorScheme.onPrimary)

## 3. Strings - Adicionar resources

- [x] 3.1 Adicionar string "share_button" = "Compartilhar" no strings.xml

## 4. LugravScreen - Integrar estados e lógica de share

- [x] 4.1 Adicionar estado selectedFileToShare (String? = null) na LugravScreen
- [x] 4.2 Atualizar long press do RecordingCard para definir selectedFileToShare
- [x] 4.3 Passar onShareClick para LugravTopBar quando isDeleteMode = true e selectedFileToShare != null
- [x] 4.4 Implementar função shareRecording(filePath) na LugravScreen
- [x] 4.5 Implementar lógica de share com FileProvider.getUriForFile()
- [x] 4.6 Criar Intent.ACTION_SEND com type "audio/aac" e EXTRA_STREAM
- [x] 4.7 Adicionar FLAG_GRANT_READ_URI_PERMISSION ao intent
- [x] 4.8 Usar Intent.createChooser() para mostrar apps receptores
- [x] 4.9 Limpar selectedFileToShare após ShareSheet ser iniciada
- [x] 4.10 Importar FileProvider no arquivo

## 5. Verificação

- [x] 5.1 Compilar o projeto para verificar erros
- [x] 5.2 Verificar que o código segue o estilo do projeto (ícones brancos, design.md)