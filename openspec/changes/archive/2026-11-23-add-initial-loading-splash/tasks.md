## 1. Dependência
- [x] 1.1 Adicionar a versão e o alias `androidx-core-splashscreen` ao `gradle/libs.versions.toml`
- [x] 1.2 Adicionar `implementation(libs.androidx.core.splashscreen)` ao `app/build.gradle.kts`

## 2. Tema e recursos
- [x] 2.1 Adicionar `sky_blue` e `surface_color` ao `app/src/main/res/values/colors.xml`
- [x] 2.2 Criar `Theme.ProjTeste.Starting` com fundo `surface_color`, ícone `ic_launcher_foreground` e icon background `sky_blue`
- [x] 2.3 Aplicar `Theme.ProjTeste.Starting` na `MainActivity` no `AndroidManifest.xml`

## 3. ViewModel
- [x] 3.1 Adicionar `isLoadingRecordings` e dispatcher injetável ao `AudioRecordingViewModel`
- [x] 3.2 Executar `getRecordingsList()` fora da main thread e limpar o loading em sucesso e falha

## 4. Activity e UI
- [x] 4.1 Integrar `installSplashScreen()`, a condição de carregamento e o disparo inicial no `MainActivity`
- [x] 4.2 Remover o `LaunchedEffect` de carregamento do `LugravScreen`

## 5. Testes
- [x] 5.1 Ajustar a criação do ViewModel nos testes para injetar `Dispatchers.Unconfined`
- [x] 5.2 Adicionar testes do estado inicial, sucesso e falha de `isLoadingRecordings`

## 6. Validação
- [x] 6.1 Compilar com `:app:compileDebugKotlin`
- [x] 6.2 Executar `:app:testDebugUnitTest`
- [x] 6.3 Executar `:app:lintDebug`
- [x] 6.4 Revisar o diff e confirmar que não há carregamento inicial duplicado
