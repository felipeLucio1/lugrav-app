## Context

A LugravScreen.kt implementa seleção de card via long press, mostrando ícones de share e delete na toolbar quando um card está selecionado. O problema atual é que o clique (onClick) no card sempre abre o bottom sheet, mesmo quando o card está selecionado. O usuário não consegue desmarcar o card apenas tocando nele.

## Goals / Non-Goals

**Goals:**
- Permitir que o usuário desmarque um card selecionado ao clicar nele
- Manter comportamento de abrir bottom sheet quando o card não está selecionado

**Non-Goals:**
- Modificar comportamento de long press
- Adicionar novas formas de desmarcar (por exemplo, clicar fora do card)

## Decisions

### 1. Toggle no onClick

Decisão: Verificar na lambda onClick se o card está selecionado e desmarcar se estiver

Alternativas consideradas:
- Adicionar parâmetro de toggle no RecordingCard (rejeitado - complexidade desnecessária, lógica deve estar na Screen)
- Usar novo parâmetro onToggleSelection (rejeitado - toggle é ação de desmarcar, não toggle entre states)

Rationale: A lógica de seleção está toda na LugravScreen (estado selectedFilePath), então o toggle deve ser implementado na Screen onde o estado reside. Isso segue o padrão de state hoisting do Compose.

### 2. Sem mudança no RecordingCard

Decisão: Não modificar RecordingCard.kt

Rationale: O RecordingCard já expõe isSelected como parâmetro booleano e onClick como lambda. A lógica de toggle pertence à Screen, não ao componente reutilizável.

## Risks / Trade-offs

- [Risk] Usuário clicka rapidamente e accidentally deseleciona → [Mitigation] O click para abrir bottom sheet também será afetado, mas o long press permite selecionar novamente facilmente

- [Risk] Confusão entre click (deselecionar) e long press (selecionar) → [Mitigation] São gestos distintos com resultados claros - click = toggle, long press = selecionar

## Migration Plan

1. Modificar LugravScreen.kt para incluir verificação na lambda onClick
2. Testar fluxo: long press → selection aparece → click no mesmo card → selection remove
3. Testar fluxo: click em card não selecionado → bottom sheet abre normalmente

Rollback: Reverter mudança na lambda onClick - não afeta dados ou outros componentes.