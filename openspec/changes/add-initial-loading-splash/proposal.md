## Why

O app exibe a tela principal antes de a lista de gravações terminar de carregar. Como o carregamento é disparado por um `LaunchedEffect` dentro do `LugravScreen`, o primeiro frame pode mostrar "Nenhuma gravação encontrada" mesmo quando existem arquivos. A API oficial `SplashScreen` permite manter a tela de abertura visível até que os dados iniciais estejam prontos.

## What Changes

- Adicionar a dependência oficial `androidx.core:core-splashscreen`
- Criar um tema de splash usando os tokens visuais do app:
  - fundo `surface_color` (`#FFFBFE`)
  - fundo do ícone `sky_blue` (`#29B6F6`)
  - ícone vetorial `@drawable/ic_launcher_foreground`
- Aplicar o tema de splash apenas na `MainActivity`
- Instalar o splash com `installSplashScreen()` antes de `super.onCreate()`
- Manter o splash visível com `setKeepOnScreenCondition` enquanto a lista inicial estiver carregando
- Expor `isLoadingRecordings` no `AudioRecordingViewModel`
- Disparar o carregamento inicial no `MainActivity`, antes de `setContent`
- Remover o carregamento duplicado do `LugravScreen`
- Executar o carregamento da lista fora da main thread
- Adicionar testes unitários do estado de carregamento no ViewModel
- Manter interfaces, repositório, Koin e formato dos arquivos inalterados

## Capabilities

### New Capabilities
- `app-launch-splash`: splash oficial que aguarda o carregamento inicial da lista de gravações antes de exibir a tela principal

### Modified Capabilities
- `viewmodel-unit-tests`: cenários de teste para `isLoadingRecordings` e conclusão do carregamento inicial

## Impact

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/java/com/felipelucio/lugrav/MainActivity.kt`
- `app/src/main/java/com/felipelucio/lugrav/AudioRecordingViewmodel.kt`
- `app/src/main/java/com/felipelucio/lugrav/view/LugravScreen.kt`
- `app/src/test/java/com/felipelucio/lugrav/AudioRecordingViewModelTest.kt`
- `openspec/specs/app-launch-splash/spec.md` (após arquivamento)
- `openspec/specs/viewmodel-unit-tests/spec.md` (após arquivamento)
- Sem novas telas, Activities ou strings
