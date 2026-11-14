## Context

O app já possui uma tela principal com top bar, carrossel de tags, lista de gravações, FAB de gravação e diálogos de criação/seleção de tags. A implementação atual usa cores pontuais (`SkyBlue`, `SurfaceColor`, `ErrorColor`), tipografia padrão do sistema e componentes Material 3 sem uma camada visual unificada.

Esta change é exclusivamente visual. Não há mudanças em persistência, filtros, criação de tags, associação de tags, reprodução, renomeação ou exclusão.

## Goals / Non-Goals

**Goals:**
- Aplicar tema light com identidade visual consistente.
- Introduzir Space Grotesk e Inter via Google Fonts remoto.
- Padronizar formas, espaçamentos e cores de superfície.
- Atualizar a paleta de cores disponível na criação de tags.
- Renderizar tags com estilo tonal.
- Preservar todos os fluxos, labels e comportamentos existentes.

**Non-Goals:**
- Criar tags padrão, de exemplo ou semeadas.
- Alterar tags existentes ou migrar `colorHex`.
- Adicionar contadores de tags, chip “Todas” ou ação “+ Tag” no card.
- Alterar o título do card; ele continua exibindo o nome do arquivo.
- Implementar tema dark.
- Alterar camada de dados, ViewModel ou testes.

## Decisions

### 1. Tema light único

**Decisão:** Expor `LugravLightTheme`, baseado em `lightColorScheme`, sem `darkColorScheme` e sem `isSystemInDarkTheme`.

**Razão:** O produto ainda não deve oferecer tema dark. Um único tema reduz variação visual e mantém a implementação previsível.

### 2. Google Fonts remoto

**Decisão:** Usar `androidx.compose.ui:ui-text-google-fonts` já presente no app, com `GoogleFont.Provider`, `Space Grotesk` para títulos e `Inter` para corpo.

**Razão:** Evita empacotar arquivos de fonte e mantém o APK menor. Se o provider não estiver disponível, a tipografia deve cair para a fonte padrão do sistema sem quebrar a tela.

### 3. Tokens de cor e superfícies

**Decisão:** Centralizar os tokens em `Color.kt` e preencher o `lightColorScheme` com `primary`, `background`, `surface`, `surfaceVariant`, `onSurface`, `onSurfaceVariant`, `outline` e `error`.

**Razão:** Os componentes devem consumir `MaterialTheme.colorScheme` em vez de cores hardcoded, facilitando consistência e futuras mudanças.

### 4. Paleta de tags apenas como opções de cor

**Decisão:** Atualizar `TagColorOptions` com 10 cores tonais. `DEFAULT_TAG_COLOR_HEX` passa a ser `#475569` (Slate).

**Razão:** O usuário pediu apenas novas cores. Os nomes “Trabalho”, “Ideias” etc. são exemplos e não devem virar tags, strings de tags ou dados semeados. `TagColorOption.nameRes` permanece apenas como descrição de acessibilidade da cor.

### 5. Badge tonal sem alterar o modelo

**Decisão:** `TagBadge` deriva um fundo pastel a partir do `colorHex` da tag e usa a cor de destaque para dot e texto. Para cores muito claras, como o branco legado, usar um tom escuro de fallback para manter contraste.

**Razão:** Tags existentes continuam válidas sem migração de banco. Apenas a apresentação muda.

### 6. Preservação de interações

**Decisão:** Manter os nomes e fluxos existentes: “Nova Tag”, “Selecionar tags”, “Gravar”, “Parar gravação”, menu contextual e diálogos.

**Razão:** A change é de restyle. Alterações de nomenclatura ou fluxo aumentariam o escopo e o risco de regressão.

## Risks / Trade-offs

- **Google Fonts depende de rede/Play Services:** usar fallback para a fonte padrão evita falha visual ou crash.
- **Tags antigas com cores fora da nova paleta:** o estilo tonal é derivado em tempo de renderização; não há migração.
- **Cabeçalho “Recentes”:** é a única adição estrutural visual; não adiciona contadores nem novas ações.
- **Sem testes de UI:** a validação será por compilação, testes unitários existentes, lint e inspeção visual.
