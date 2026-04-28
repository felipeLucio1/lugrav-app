# Plano: Cobertura de Testes e Análise da ViewModel

## Status Atual dos Testes
- ✅ **Todos os 9 testes unitários passam** (BUILD SUCCESSFUL em 23s)
- ✅ **ViewModel já satisfaz todos os testes de estado**

## Testes Existentes (Todos Passando)
1. `when recording audio is recording state should be true` - ✅
2. `recording time increments during recording` - ✅
3. `play audio sets isPlaying to true` - ✅
4. `when stop reproducing audio playing audio state should be setted to null` - ✅
5. `pause audio sets isPlaying to false` - ✅
6. `resume audio sets isPlaying to true` - ✅
7. `toggle play pause when no audio is playing should start playing` - ✅
8. `when clear selected audio should set to null` - ✅
9. `when clear delete result should set to null` - ✅

## Configuração de Cobertura (Jacoco)

### Passo 1: Adicionar configuração no `app/build.gradle.kts`
```kotlin
plugins {
    id("jacoco")
}

android {
    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )
    
    val debugTree = fileTree("${project.buildDir}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }
    
    val mainSrc = "${project.projectDir}/src/main/java"
    
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.buildDir) {
        include("**/*.exec", "**/*.ec")
    })
}
```

### Passo 2: Executar cobertura
```bash
./gradlew testDebugUnitTest jacocoTestReport --no-daemon
```

### Passo 3: Verificar relatório
- HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

## Análise da ViewModel

### O que a ViewModel faz corretamente (testado):
- ✅ `startRecording()` define `_isRecording.value = true`
- ✅ `playAudio()` define `_isPlaying.value = true` e `_currentPlayingPath`
- ✅ `stopAudio()` define `_isPlaying = false`, limpa path e tempo
- ✅ `pauseAudio()` define `_isPlaying = false`
- ✅ `resumeAudio()` define `_isPlaying = true`
- ✅ `togglePlayPause()` com nenhum áudio tocando inicia reprodução
- ✅ `clearSelectedAudio()` define `_selectedAudioPath = null`
- ✅ `clearDeleteResult()` define `_deleteResult = null`

### Possíveis melhorias identificadas (não bloqueantes):
1. **`togglePlayPause`**: O método chama `_selectedAudioPath.value = filePath`, mas o teste não verifica isso. Pode ser adicionado um `assertNotNull(viewModel.selectedAudioPath.value)` no teste se desejado.

2. **Cobertura parcial**: Os métodos `finalizeRecording()`, `deleteRecording()`, e `loadRecordings()` não são testados pelos testes de estado atuais (foram removidos intencionalmente).

## Conclusão
**Nenhuma modificação na ViewModel é necessária** - todos os testes já passam. A ViewModel `AudioRecordingViewModel.kt` gerencia corretamente os estados testados.

### Próximos Passos Recomendados:
1. Apenas configurar Jacoco para obter métricas de cobertura
2. Opcionalmente adicionar mais testes de estado se necessário
3. Considerar adicionar testes para `finalizeRecording()` e `deleteRecording()` focando apenas em estados (sem `coVerify`)
