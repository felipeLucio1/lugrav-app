## Why

A interface atual usa uma paleta mínima e estilos padrão do Material 3, sem uma identidade visual consistente entre a tela principal, os cards, os badges de tag e os diálogos. O objetivo é aplicar uma identidade visual clara e leve, mantendo todos os fluxos, nomes de botões, menus e regras de negócio existentes.

## What Changes

- Adotar um tema **light only**, sem propor ou implementar tema dark.
- Criar uma paleta visual com `SkyBlue` como cor primária, superfícies claras, cards tonais e textos com hierarquia.
- Adotar **Space Grotesk** para títulos/headlines e **Inter** para corpo, metadados e labels, via Google Fonts remoto.
- Definir formas e espaçamentos consistentes: cards `16dp`, pills arredondadas, FAB `28dp`, margens `16dp` e gaps de `12dp`.
- Restilizar top bar, carrossel de tags, cards de gravação, FAB, diálogos e bottom sheet.
- Substituir as opções de cor de tag por uma paleta de 10 cores tonais; a cor padrão para novas tags passa a ser Slate `#475569`.
- Renderizar badges de tag com fundo pastel, dot e texto na cor de destaque.
- Adicionar o cabeçalho visual “Recentes” e o subtítulo “Sua biblioteca de sons capturados”.
- Preservar nomes existentes de botões e menus, incluindo “Nova Tag”, “Selecionar tags”, “Gravar” e “Parar gravação”.
- Não criar, semear, renomear, associar ou migrar tags.
- Não alterar ViewModel, repositório, DAO, store, modelos ou testes.

## Capabilities

### New Capabilities
- `app-theme`: tema light, paleta, tipografia, formas, espaçamentos e status bar.

### Modified Capabilities
- `recording-list`: estilos visuais da tela principal, carrossel, cards, FAB, diálogos e opções de cor de tag.

## Impact

- `app/src/main/java/com/felipelucio/lugrav/ui/theme/Color.kt`
- `app/src/main/java/com/felipelucio/lugrav/ui/theme/Theme.kt`
- `app/src/main/java/com/felipelucio/lugrav/ui/theme/Type.kt`
- `app/src/main/java/com/felipelucio/lugrav/ui/theme/TagColors.kt`
- `app/src/main/java/com/felipelucio/lugrav/ui/theme/Shapes.kt` (novo)
- `app/src/main/java/com/felipelucio/lugrav/MainActivity.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/components/*.kt`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/font_certs.xml` (novo)
- `openspec/specs/app-theme/spec.md` (após arquivamento)
- `openspec/specs/recording-list/spec.md` (após arquivamento)
