# Plano de Refatoração: AudioRecordingRepository

## Objetivo
Refatorar `AudioRecordingRepository.kt` para extrair interfaces das dependências Android (MediaRecorder, MediaPlayer, MediaMetadataRetriever, Context), tornando-o totalmente testável sem Robolectric, mantendo comportamento original.

## Análise das Dependências

### 1. **Context** (Android)
**Usos atuais:**
- `context.cacheDir` (linha 29) - para arquivo temp de gravação
- `context.getExternalFilesDir(null)` (linhas 69, 91) - para pasta de armazenamento

**Interface necessária:** `FileProvider`
```kotlin
interface FileProvider {
    fun getCacheDir(): File
    fun getExternalFilesDir(): File?
}
```

### 2. **MediaRecorder** (Android)
**Usos atuais:**
- Criação condicional baseada na versão SDK (linhas 31-35)
- Configuração: `setAudioSource`, `setOutputFormat`, `setAudioEncoder`, `setAudioSamplingRate`, `setAudioEncodingBitRate`, `setAudioChannels`, `setOutputFile` (linhas 36-42)
- `prepare()` (linha 44)
- `start()` (linha 45)
- `stop()` (linha 51)
- `release()` (linha 52)

**Interface necessária:** `AudioRecorder`
```kotlin
interface AudioRecorder {
    fun setup(audioSource: Int, outputFormat: Int, audioEncoder: Int, 
            samplingRate: Int, encodingBitRate: Int, channels: Int, outputFile: String)
    fun prepare()
    fun start()
    fun stop()
    fun release()
}
```

### 3. **MediaPlayer** (Android)
**Usos atuais:**
- Criação (linha 113) - `MediaPlayer()`
- `setDataSource(filePath)` (linha 114)
- `prepare()` (linha 115)
- `start()` (linha 116)
- `pause()` (linha 126)
- `setOnCompletionListener` (linhas 117-121)
- `stop()` (linha 135)
- `release()` (linhas 119, 136)

**Interface necessária:** `AudioPlayer`
```kotlin
interface AudioPlayer {
    fun setDataSource(path: String)
    fun prepare()
    fun start()
    fun pause()
    fun stop()
    fun release()
    fun setOnCompletionListener(listener: () -> Unit)
}
```

### 4. **MediaMetadataRetriever** (Android)
**Usos atuais:**
- Criação (linha 161) - `MediaMetadataRetriever()`
- `setDataSource(filePath)` (linha 162)
- `extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)` (linha 163)
- `release()` (linha 164)

**Interface necessária:** `MetadataRetriever`
```kotlin
interface MetadataRetriever {
    fun setDataSource(path: String)
    fun extractDuration(): Long?
    fun release()
}
```

## Arquivos a Serem Criados

### 1. `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioRecorder.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

interface AudioRecorder {
    fun setup(audioSource: Int, outputFormat: Int, audioEncoder: Int, 
            samplingRate: Int, encodingBitRate: Int, channels: Int, outputFile: String)
    fun prepare()
    fun start()
    fun stop()
    fun release()
}
```

### 2. `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioRecorderImpl.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

import android.media.MediaRecorder
import android.os.Build

class AudioRecorderImpl(private val context: android.content.Context) : AudioRecorder {
    private var recorder: MediaRecorder? = null
    
    override fun setup(audioSource: Int, outputFormat: Int, audioEncoder: Int, 
                            samplingRate: Int, encodingBitRate: Int, channels: Int, outputFile: String) {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(audioSource)
            setOutputFormat(outputFormat)
            setAudioEncoder(audioEncoder)
            setAudioSamplingRate(samplingRate)
            setAudioEncodingBitRate(encodingBitRate)
            setAudioChannels(channels)
            setOutputFile(outputFile)
        }
    }
    
    override fun prepare() { recorder?.prepare() }
    override fun start() { recorder?.start() }
    override fun stop() { recorder?.stop() }
    override fun release() { 
        recorder?.release() 
        recorder = null
    }
}
```

### 3. `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioPlayer.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

interface AudioPlayer {
    fun setDataSource(path: String)
    fun prepare()
    fun start()
    fun pause()
    fun stop()
    fun release()
    fun setOnCompletionListener(listener: () -> Unit)
    fun getCurrentPosition(): Int
    fun getDuration(): Int
}
```

### 4. `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioPlayerImpl.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

import android.media.MediaPlayer

class AudioPlayerImpl : AudioPlayer {
    private var player: MediaPlayer? = null
    
    override fun setDataSource(path: String) {
        player = MediaPlayer().apply {
            setDataSource(path)
        }
    }
    
    override fun prepare() { player?.prepare() }
    override fun start() { player?.start() }
    override fun pause() { player?.pause() }
    override fun stop() { player?.stop() }
    override fun release() { 
        player?.release() 
        player = null
    }
    override fun setOnCompletionListener(listener: () -> Unit) {
        player?.setOnCompletionListener { listener() }
    }
    override fun getCurrentPosition(): Int = player?.currentPosition ?: 0
    override fun getDuration(): Int = player?.duration ?: 0
}
```

### 5. `app/src/main/java/com/felipelucio/lugrav/interfaces/MetadataRetriever.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

interface MetadataRetriever {
    fun setDataSource(path: String)
    fun extractDuration(): Long?
    fun release()
}
```

### 6. `app/src/main/java/com/felipelucio/lugrav/interfaces/MetadataRetrieverImpl.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

import android.media.MediaMetadataRetriever

class MetadataRetrieverImpl : MetadataRetriever {
    private val retriever = MediaMetadataRetriever()
    
    override fun setDataSource(path: String) {
        retriever.setDataSource(path)
    }
    
    override fun extractDuration(): Long? {
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()
    }
    
    override fun release() {
        retriever.release()
    }
}
```

### 7. `app/src/main/java/com/felipelucio/lugrav/interfaces/FileProvider.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

import android.content.Context
import java.io.File

interface FileProvider {
    fun getCacheDir(): File
    fun getExternalFilesDir(): File?
}
```

### 8. `app/src/main/java/com/felipelucio/lugrav/interfaces/FileProviderImpl.kt`
```kotlin
package com.felipelucio.lugrav.interfaces

import android.content.Context
import java.io.File

class FileProviderImpl(private val context: Context) : FileProvider {
    override fun getCacheDir(): File = context.cacheDir
    override fun getExternalFilesDir(): File? = context.getExternalFilesDir(null)
}
```

## Arquivo a Ser Modificado

### `app/src/main/java/com/felipelucio/lugrav/AudioRecordingRepository.kt`

**Mudanças:**
1. Remover imports Android (`MediaRecorder`, `MediaPlayer`, `MediaMetadataRetriever`)
2. Adicionar imports das novas interfaces
3. Construtor recebe 4 dependências via injeção:
   ```kotlin
   class AudioRecordingRepository(
       private val fileProvider: FileProvider,
       private val audioRecorder: AudioRecorder,
       private val audioPlayer: AudioPlayer,
       private val metadataRetriever: MetadataRetriever
   )
   ```
4. Substituir usos diretos por chamadas às interfaces
5. Manter lógica de negócio idêntica:
   - Nomes de arquivos (`lugrav_YYYYMMdd_HHmmss.aac`)
   - Formatação de duração (`formatDuration`)
   - Tratamento de erros (`try-catch` em `getAudioDuration`)
   - Construção de `AudioRecordingModel`

**Código Refatorado (completo):**
```kotlin
package com.felipelucio.lugrav

import com.felipelucio.lugrav.interfaces.AudioRecorder
import com.felipelucio.lugrav.interfaces.AudioPlayer
import com.felipelucio.lugrav.interfaces.FileProvider
import com.felipelucio.lugrav.interfaces.MetadataRetriever
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecordingRepository(
    private val fileProvider: FileProvider,
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val metadataRetriever: MetadataRetriever
) {
    private var tempFile: File? = null

    companion object {
        private const val APP_FOLDER = "lugrav"
        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        private const val FILE_EXTENSION = ".aac"
    }

    fun startRecording() {
        tempFile = File(fileProvider.getCacheDir(), "temp_audio_${System.currentTimeMillis()}.aac")
        
        audioRecorder.setup(
            audioSource = android.media.MediaRecorder.AudioSource.MIC,
            outputFormat = android.media.MediaRecorder.OutputFormat.AAC_ADTS,
            audioEncoder = android.media.MediaRecorder.AudioEncoder.AAC,
            samplingRate = 44100,
            encodingBitRate = 128000,
            channels = 2,
            outputFile = tempFile?.absolutePath ?: ""
        )
        audioRecorder.prepare()
        audioRecorder.start()
    }

    fun stopRecording(): ByteArray {
        audioRecorder.stop()
        audioRecorder.release()
        
        val audioBytes = tempFile?.let { file ->
            FileInputStream(file).use { it.readBytes() }
        } ?: byteArrayOf()
        
        tempFile?.delete()
        tempFile = null
        
        return audioBytes
    }

    fun stopAndSave(): String {
        val audioBytes = stopRecording()
        
        val baseDir = fileProvider.getExternalFilesDir()
        val appFolder = File(baseDir, APP_FOLDER)
        
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        
        val timestamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val fileName = buildString {
            append(APP_FOLDER)
            append("_")
            append(timestamp)
            append(FILE_EXTENSION)
        }
        val outputFile = File(appFolder, fileName)
        
        outputFile.writeBytes(audioBytes)
        
        return outputFile.absolutePath
    }

    fun getRecordingsList(): List<AudioRecordingModel> {
        val baseDir = fileProvider.getExternalFilesDir()
        val appFolder = File(baseDir, APP_FOLDER)
        
        if (!appFolder.exists()) {
            return emptyList()
        }
        
        val files = appFolder.listFiles { file ->
            file.extension == "aac"
        } ?: return emptyList()
        
        return files.map { file ->
            val duration = getAudioDuration(file.absolutePath)
            AudioRecordingModel(
                path = file.absolutePath,
                duration = duration
            )
        }
    }

    fun playAudio(filePath: String, onCompletion: () -> Unit = {}) {
        stopAudio()
        
        audioPlayer.setDataSource(filePath)
        audioPlayer.prepare()
        audioPlayer.start()
        audioPlayer.setOnCompletionListener {
            onCompletion()
            audioPlayer.release()
        }
    }

    fun pauseAudio() {
        audioPlayer.pause()
    }

    fun resumeAudio() {
        audioPlayer.start()
    }

    fun stopAudio() {
        audioPlayer.stop()
        audioPlayer.release()
    }

    @Throws(IOException::class)
    fun deleteRecording(filePath: String) {
        stopAudio()
        
        val file = File(filePath)
        if (!file.exists()) {
            throw IOException("File does not exist: $filePath")
        }
        
        if (!file.delete()) {
            throw IOException("Failed to delete file: $filePath")
        }
    }

    fun getCurrentPosition(): Int = audioPlayer.getCurrentPosition()
    
    fun getDuration(): Int = audioPlayer.getDuration()

    private fun getAudioDuration(filePath: String): String {
        return try {
            metadataRetriever.setDataSource(filePath)
            val durationMs = metadataRetriever.extractDuration() ?: 0L
            metadataRetriever.release()
            formatDuration(durationMs)
        } catch (e: Exception) {
            "00:00:00"
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
```

## Configuração de Dependência (Koin)

Atualizar `di/AppModule.kt` para injetar as novas implementações:

```kotlin
package com.felipelucio.lugrav.di

import org.koin.dsl.module
import com.felipelucio.lugrav.interfaces.*

val appModule = module {
    single { FileProviderImpl(androidContext()) }
    single<AudioRecorder> { AudioRecorderImpl(androidContext()) }
    single<AudioPlayer> { AudioPlayerImpl() }
    single<MetadataRetriever> { MetadataRetrieverImpl() }
    single { AudioRecordingRepository(get(), get(), get(), get()) }
    single { AudioRecordingViewModel(get()) }
}
```

## Benefícios da Refatoração

1. **100% testável sem Robolectric** - Use MockK puro
2. **Código mais limpo** - Separação clara de responsabilidades
3. **Fácil mocking em testes** - `mockk<AudioRecorder>(), mockk<AudioPlayer>(), etc.`
4. **Comportamento idêntico** - Lógica de negócio preservada
5. **Segue princípios SOLID** - Dependency Inversion Principle

## Plano de Implementação (Ordem)

1. Criar pasta `interfaces/` em `app/src/main/java/com/felipelucio/lugrav/`
2. Criar 8 arquivos de interface/implementação (listados acima)
3. Refatorar `AudioRecordingRepository.kt` (substituir código, manter lógica)
4. Atualizar `di/AppModule.kt` para injeção de dependências
5. Verificar compilação: `./gradlew compileDebugKotlin --no-daemon`
6. Criar testes unitários (próximo passo, não nesta tarefa)

## Estrutura Final de Arquivos

```
app/src/main/java/com/felipelucio/lugrav/
├── interfaces/
│   ├── AudioRecorder.kt
│   ├── AudioRecorderImpl.kt
│   ├── AudioPlayer.kt
│   ├── AudioPlayerImpl.kt
│   ├── MetadataRetriever.kt
│   ├── MetadataRetrieverImpl.kt
│   ├── FileProvider.kt
│   └── FileProviderImpl.kt
├── di/
│   └── AppModule.kt (modificado)
├── AudioRecordingRepository.kt (refatorado)
├── AudioRecordingViewModel.kt (inalterado)
└── ... (outros arquivos)
```

## Verificação de Compilação

Após implementação, executar:
```bash
./gradlew compileDebugKotlin --no-daemon
```

Isso garantirá que:
- Todas as interfaces estão corretas
- Implementações respeitam contratos
- Repository funciona com as novas dependências
- Comportamento original é preservado
