## Context

O app é uma única `ComponentActivity` com Compose, ViewModel obtido via Koin e `AudioRecordingRepository` para I/O local. Hoje o `LugravScreen` dispara `viewModel.loadRecordings()` em um `LaunchedEffect(Unit)`, ou seja, depois que a tela já começou a ser composta. Isso permite que o estado vazio seja exibido antes da lista real.

A documentação oficial recomenda a biblioteca de compatibilidade `androidx.core:core-splashscreen`, com `installSplashScreen()` antes de `super.onCreate()` e `setKeepOnScreenCondition` para manter o splash enquanto uma pequena quantidade de dados locais é carregada. Os tokens visuais do app estão em `.opencode/design.md`: primária SkyBlue `#29B6F6` e background M3 Surface `#FFFBFE`.

## Goals / Non-Goals

**Goals:**
- Exibir o splash oficial do Android durante o carregamento inicial da lista
- Aguardar o término do carregamento antes de liberar a tela principal
- Usar o fundo do app (`#FFFBFE`) e a cor primária (`#29B6F6`) no ícone
- Garantir um único disparo do carregamento inicial
- Manter a condição do splash rápida e o carregamento fora da main thread
- Liberar o splash mesmo quando o carregamento falhar
- Cobrir o estado de carregamento no ViewModel com testes

**Non-Goals:**
- Criar uma Activity ou tela Compose de splash
- Adicionar tempo mínimo de exibição ou animação customizada de saída
- Adicionar timeout artificial para o carregamento local
- Alterar o repositório, as interfaces ou o formato dos arquivos
- Adicionar suporte a dark mode
- Implementar placeholders para carregamentos de rede

## Decisions

### 1. Biblioteca oficial de compatibilidade

**Decisão**: Usar `androidx.core:core-splashscreen:1.2.0`.

**Razão**: A documentação oficial recomenda a biblioteca de compatibilidade em vez da API de plataforma ou de uma Activity de splash. Ela funciona com `ComponentActivity` e Compose, inclusive em APIs anteriores à 31.

### 2. Tema visual alinhado ao design do app

**Decisão**: Criar `Theme.ProjTeste.Starting` com parent `Theme.SplashScreen.IconBackground`:

- `windowSplashScreenBackground` = `@color/surface_color` (`#FFFBFE`)
- `windowSplashScreenAnimatedIcon` = `@drawable/ic_launcher_foreground`
- `windowSplashScreenIconBackgroundColor` = `@color/sky_blue` (`#29B6F6`)
- `postSplashScreenTheme` = `@style/Theme.ProjTeste`

**Razão**: O fundo da janela replica o background do app definido em `.opencode/design.md`. O fundo SkyBlue atrás do ícone mantém o foreground branco visível e usa a cor primária. O vetor existente em `res/drawable` atende ao requisito de ícone drawable da API.

### 3. Instalação e condição no MainActivity

**Decisão**: Chamar `installSplashScreen()` antes de `super.onCreate()`, obter o ViewModel na Activity e definir:

```kotlin
splashScreen.setKeepOnScreenCondition {
    viewModel.isLoadingRecordings.value
}
```

O carregamento inicial é disparado antes de `setContent`.

**Razão**: O splash precisa de uma condição baseada em estado observável. Ler `StateFlow.value` é O(1) e não bloqueia a avaliação feita antes de cada frame. A Activity é o ponto correto para iniciar o carregamento antes do primeiro conteúdo.

### 4. Estado de carregamento no ViewModel

**Decisão**: Adicionar `isLoadingRecordings`, iniciando em `true`, e um `CoroutineDispatcher` injetável com default `Dispatchers.IO`. `loadRecordings()`:

- marca `isLoadingRecordings = true`
- executa `repository.getRecordingsList()` com `withContext(ioDispatcher)`
- atualiza `recordingsList`
- mantém a lista atual em caso de falha
- define `isLoadingRecordings = false` no `finally`

**Razão**: Evita bloquear a main thread e garante que o splash seja liberado em sucesso, lista vazia ou erro. O dispatcher injetável mantém os testes determinísticos.

### 5. Carregamento inicial único

**Decisão**: Remover o `LaunchedEffect(Unit) { viewModel.loadRecordings() }` do `LugravScreen`.

**Razão**: O carregamento inicial passa a ser responsabilidade da Activity. Recargas posteriores continuam sendo feitas pelo ViewModel após gravar, renomear ou excluir.

### 6. Sem timeout ou animação customizada

**Decisão**: Não adicionar timeout, tempo mínimo ou `setOnExitAnimationListener`.

**Razão**: O carregamento é local e limitado. A API oficial já fornece a animação de saída padrão. Um timeout poderia liberar a tela antes da lista estar pronta; uma animação customizada não é necessária para o requisito.

## Risks / Trade-offs

- **Carregamento local lento**: com muitos arquivos, o splash pode permanecer por mais tempo. O carregamento em `Dispatchers.IO` evita bloquear a UI, mas não reduz o tempo de I/O.
- **Falha no carregamento**: o splash é liberado e a lista permanece vazia/atual; não há snackbar específico nesta mudança.
- **Recargas concorrentes**: `isLoadingRecordings` pode alternar durante recargas posteriores, mas o splash já não está visível nesse ponto.
- **Contraste do ícone**: usar `@color/sky_blue` como fundo do ícone é necessário porque o foreground existente é branco.
- **Hot start**: a API oficial não exibe o splash em hot start; a lista já está em memória nesse cenário.

## Migration Plan

Nenhuma migração de dados. O comportamento anterior é restaurado removendo o tema inicial, a dependência, a condição do splash, o estado de carregamento e reintroduzindo o `LaunchedEffect` no `LugravScreen`.

## Open Questions

Nenhuma.
