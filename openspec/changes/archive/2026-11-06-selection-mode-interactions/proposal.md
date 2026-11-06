## Why

No modo de seleção ativado por long press, o toque em cards pode iniciar reprodução ou abrir o bottom sheet, criando comportamento confuso. É necessário restringir as interações ao gerenciamento de seleção, permitindo sair do modo apenas por ações explícitas e trocar a seleção entre cards sem alterar estados de reprodução.

## What Changes

- Permitir sair do modo de seleção apenas por: seta `ArrowBack` na TopAppBar, toque no card já selecionado, ou botão nativo voltar
- No modo de seleção, tocar em outro card troca o card selecionado
- No modo de seleção, tocar em cards não inicia reprodução nem abre o bottom sheet
- Não alterar estados de reprodução que não reflitam o estado real da aplicação

## Capabilities

### New Capabilities

- `selection-mode-interactions`: Regras de interação do modo de seleção (entrada, troca e saída) sem reprodução

### Modified Capabilities

- Nenhuma

## Impact

- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`: lógica de clique dos cards e `BackHandler`
- `app/src/main/java/com/felipelucio/lugrav/view/components/RecordingCard.kt`: sem mudança funcional
- `app/src/main/java/com/felipelucio/lugrav/view/components/LugravTopBar.kt`: sem mudança (já expõe `onClearSelection`)
