# Plano: Testes Unitários para AudioRecordingRepository

## Objetivo
Criar testes unitários para `AudioRecordingRepository` focando em casos de uso e responsabilidades na arquitetura MVVM.

## Arquivo de Teste
`app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`

## Padrão de Nomenclatura
Seguir o padrão de `AudioRecordingViewModelTest.kt`:
- Estilo Given/When/Then usando backticks
- Formato: `given [cenário] when [ação] then [esperado]` ou `when [ação] then [esperado]`

## Ferramentas Recomendadas (Documentação Android)

### 1. **MockK** (Já configurado)
- Use `mockk<Type>(relaxed = true)` para classes Android difíceis de instanciar
- Use `every { ... } returns ...` para stubbing
- Use `verify { ... }` para verificar interações

### 2. **Robolectric** (Recomendado pela Documentação Oficial)
**Por que Robolectric?**
- Executa testes em JVM simulando ambiente Android (sem emulador)
- Tem `ShadowMediaRecorder` e `ShadowMediaPlayer` prontos para uso
- **ShadowMediaRecorder**: rastreia propriedades (audioSource, outputFormat, etc.) e estados (started/stopped)
- **ShadowMediaPlayer**: emula playback, completion listener, estados internos
- **Importante**: Não grava áudio real, apenas simula comportamento

**Configuração Robolectric:**
```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30]) // ou versão compatível
class AudioRecordingRepositoryTest {
    // ...
}
```

**Dependência necessária** (adicionar ao `build.gradle.kts`):
```kotlin
testImplementation("org.robolectric:robolectric:4.12")
```

### 3. **Context em Testes**
Opções (da mais para menos recomendada):

**Opção A: `ApplicationProvider` (Recomendada pela Android Developers)**
```kotlin
import androidx.test.core.app.ApplicationProvider
val context = ApplicationProvider.getApplicationContext<Context>()
// Funciona bem para testes simples, mas pode não ter todos os recursos
```

**Opção B: `mockk<Context>(relaxed = true)` (Mais flexível)**
```kotlin
val context = mockk<Context>(relaxed = true)
// Permite mockar comportamentos específicos como getExternalFilesDir()
```

**Opção C: Robolectric `RuntimeEnvironment` (Se usar Robolectric)**
```kotlin
val context = RuntimeEnvironment.getApplication()
// Útil quando já usa Robolectric runner
```

**Melhor Abordagem para este Repositório:**
Usar **MockK para Context** + **Robolectric para MediaRecorder/MediaPlayer**:
```kotlin
val context = mockk<Context>(relaxed = true)
// Mock necessário:
every { context.getExternalFilesDir(null) } returns tempDir
```

## Cenários de Teste

### 1. Gravação (Recording)

#### 1.1 `given recording starts when startRecording is called then mediaRecorder should be configured`
- Use Robolectric's `ShadowMediaRecorder` para verificar configurações
- Verifique: audioSource = MIC, outputFormat = AAC_ADTS, audioEncoder = AAC
- Verifique: setAudioSamplingRate(44100), setAudioEncodingBitRate(128000), setAudioChannels(2)

#### 1.2 `given recording starts when startRecording is called then should start mediaRecorder`
- Verifique que `start()` foi chamado no MediaRecorder
- Use Shadow: `ShadowMediaRecorder.getState()` deve ser "STARTED"

#### 1.3 `given start recording fails when prepare throws exception then should propagate exception`
- Mock `MediaRecorder.prepare()` via Robolectric ou MockK para lançar `IOException`
- Verifique exceção é propagada

#### 1.4 `when stopRecording is called then should return audio bytes`
- Setup: MediaRecorder com temp file criado
- Call `stopRecording()`
- Verifique bytes retornados via `FileInputStream`
- Verifique: `stop()` e `release()` chamados
- Verifique: temp file foi deletado

#### 1.5 `given recording stops when stopAndSave is called then should save file and return path`
- Setup: `stopRecording()` retorna bytes
- Mock `Context.getExternalFilesDir()` para retornar diretório temp
- Call `stopAndSave()`
- Verifique arquivo criado em `lugrav/` com padrão: `lugrav_YYYYMMDD_HHMMSS.aac`
- Verifique caminho retornado está correto

#### 1.6 `when stopAndSave is called then should create app folder if not exists`
- Mock `File.exists()` para retornar false
- Verifique `File.mkdirs()` foi chamado

### 2. Playback (Reprodução)

#### 2.1 `when playAudio is called then should configure mediaPlayer and start`
- Use Robolectric's `ShadowMediaPlayer`
- Verifique `setDataSource()` chamado com caminho correto
- Verifique `prepare()` e `start()` chamados
- Shadow state deve ser "STARTED"

#### 2.2 `given audio completes when playAudio is called then should call onCompletion and release`
- Use `ShadowMediaPlayer.MediaInfo` para configurar duração
- Trigger completion via Shadow: `shadowMediaPlayer.invokeCompletionListener()`
- Verifique `onCompletion` callback foi invocado
- Verifique `MediaPlayer.release()` foi chamado

#### 2.3 `when pauseAudio is called then should pause mediaPlayer`
- Setup: MediaPlayer ativo
- Call `pauseAudio()`
- Verifique `MediaPlayer.pause()` chamado
- Shadow state deve ser "PAUSED"

#### 2.4 `when resumeAudio is called then should start mediaPlayer`
- Setup: MediaPlayer pausado
- Call `resumeAudio()`
- Verifique `MediaPlayer.start()` chamado
- Shadow state deve voltar para "STARTED"

#### 2.5 `when stopAudio is called then should stop and release mediaPlayer`
- Setup: MediaPlayer ativo
- Call `stopAudio()`
- Verifique `stop()` e `release()` chamados
- Verifique `mediaPlayer` é setado para null

#### 2.6 `when stopAudio is called and mediaPlayer is null then should not crash`
- Call `stopAudio()` sem configurar MediaPlayer
- Verifique nenhuma exceção é lançada

### 3. Listagem de Gravações

#### 3.1 `when getRecordingsList is called and folder exists then should return list of recordings`
- Mock `Context.getExternalFilesDir()` para retornar diretório com arquivos mock
- Mock `MediaMetadataRetriever` (ou use Shadow) para retornar duração
- Verifique retorna lista com objetos `AudioRecordingModel`

#### 3.2 `when getRecordingsList is called and folder does not exist then should return empty list`
- Mock `File.exists()` para retornar false
- Verifique retorna lista vazia

#### 3.3 `when getRecordingsList is called then should filter only aac files`
- Setup: diretório com arquivos `.aac` e `.mp3`
- Verifique apenas arquivos `.aac` são incluídos

### 4. Deleção de Gravações

#### 4.1 `given file exists when deleteRecording is called then should delete file and stop audio`
- Setup: Arquivo existe
- Call `deleteRecording("test.aac")`
- Verifique `File.delete()` foi chamado
- Verifique `stopAudio()` foi chamado antes da deleção

#### 4.2 `given file does not exist when deleteRecording is called then should throw IOException`
- Mock `File.exists()` para retornar false
- Call `deleteRecording("test.aac")`
- Verifique `IOException` é lançada com mensagem "File does not exist: test.aac"

#### 4.3 `given delete fails when deleteRecording is called then should throw IOException`
- Mock `File.delete()` para retornar false
- Call `deleteRecording("test.aac")`
- Verifique `IOException` é lançada com mensagem "Failed to delete file: test.aac"

### 5. Duração e Posição

#### 5.1 `when getCurrentPosition is called and mediaPlayer exists then should return position`
- Setup: `mediaPlayer.currentPosition` retorna 5000
- Verifique retorna 5000

#### 5.2 `when getCurrentPosition is called and mediaPlayer is null then should return 0`
- Verifique retorna 0

#### 5.3 `when getDuration is called and mediaPlayer exists then should return duration`
- Setup: `mediaPlayer.duration` retorna 30000
- Verifique retorna 30000

#### 5.4 `when getDuration is called and mediaPlayer is null then should return 0`
- Verifique retorna 0

#### 5.5 `given valid file when getAudioDuration is called then should return formatted duration`
- Use `ShadowMediaMetadataRetriever` ou mock `MediaMetadataRetriever`
- Configurar para retornar duração "60000" (60 segundos)
- Verifique retorna "00:01:00"

#### 5.6 `given invalid file when getAudioDuration is called then should return default duration`
- Mock `MediaMetadataRetriever` para lançar exceção
- Verifique retorna "00:00:00"

## Estrutura de Exemplo (Atualizada com Robolectric)

```kotlin
package com.felipelucio.lugrav

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaRecorder
import org.robolectric.shadows.ShadowMediaPlayer
import org.robolectric.Shadows.shadowOf
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class AudioRecordingRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: AudioRecordingRepository
    private lateinit var tempDir: File

    @Before
    fun setup() {
        tempDir = createTempDir("test", "audio")
        context = mockk(relaxed = true)
        every { context.getExternalFilesDir(null) } returns tempDir
        
        repository = AudioRecordingRepository(context)
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `given recording starts when startRecording is called then mediaRecorder should be configured`() {
        // When
        repository.startRecording()
        
        // Then - Use Robolectric Shadow to verify
        val recorder = MediaRecorder(context) // Será shadowed pelo Robolectric
        val shadow = shadowOf(recorder)
        assertEquals(MediaRecorder.AudioSource.MIC, shadow.audioSource)
        assertEquals(MediaRecorder.OutputFormat.AAC_ADTS, shadow.outputFormat)
        assertEquals(MediaRecorder.AudioEncoder.AAC, shadow.audioEncoder)
    }

    @Test
    fun `when getRecordingsList is called and folder exists then should return list of recordings`() {
        // Setup: criar arquivos fake no tempDir
        val lugravDir = File(tempDir, "lugrav")
        lugravDir.mkdirs()
        File(lugravDir, "test1.aac").createNewFile()
        File(lugravDir, "test2.aac").createNewFile()
        
        // When
        val result = repository.getRecordingsList()
        
        // Then
        assertEquals(2, result.size)
        assertTrue(result.all { it.path.endsWith(".aac") })
    }
    
    // Mais testes...
}
```

## Arquivos para Criar/Modificar

1. **Criar**: `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
2. **Modificar**: `app/build.gradle.kts` - adicionar dependência Robolectric:
```kotlin
testImplementation("org.robolectric:robolectric:4.12")
```
3. **Verificar**: `libs.versions.toml` tem MockK dependency (já confirmado)

## Notas Importantes

1. **Robolectric NÃO grava áudio real** - apenas simula comportamento do MediaRecorder
2. **MockK para Context** é mais simples que tentar usar ApplicationProvider real
3. **Shadows** permitem verificar propriedades que normalmente seriam privadas
4. **NÃO use `MockContext`** (android.test.mock) - muito limitado, lança exceções
5. **Para MediaMetadataRetriever** - pode usar mock direto ou ShadowMediaMetadataRetriever se disponível

## Próximos Passos
1. Adicionar dependência Robolectric no `build.gradle.kts`
2. Criar arquivo de teste seguindo exemplos acima
3. Implementar cenários de 1 a 5 gradualmente
4. Executar testes com `./gradlew testDebugUnitTest --no-daemon`
