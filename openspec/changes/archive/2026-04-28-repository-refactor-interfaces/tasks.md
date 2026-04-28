## 1. Create Interfaces

- [x] 1.1 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioRecorder.kt` with interface defining: `setup(audioSource: Int, outputFormat: Int, audioEncoder: Int, samplingRate: Int, encodingBitRate: Int, channels: Int, outputFile: String)`, `prepare()`, `start()`, `stop()`, `release()`
- [x] 1.2 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioRecorderImpl.kt` implementing `AudioRecorder` with `MediaRecorder` delegation and SDK version check
- [x] 1.3 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioPlayer.kt` with interface defining: `setDataSource(path: String)`, `prepare()`, `start()`, `pause()`, `stop()`, `release()`, `setOnCompletionListener(listener: () -> Unit)`, `getCurrentPosition(): Int`, `getDuration(): Int`
- [x] 1.4 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/AudioPlayerImpl.kt` implementing `AudioPlayer` with `MediaPlayer` delegation
- [x] 1.5 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/MetadataRetriever.kt` with interface defining: `setDataSource(path: String)`, `extractDuration(): Long?`, `release()`
- [x] 1.6 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/MetadataRetrieverImpl.kt` implementing `MetadataRetriever` with `MediaMetadataRetriever` delegation
- [x] 1.7 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/FileProvider.kt` with interface defining: `getCacheDir(): File`, `getExternalFilesDir(): File?`
- [x] 1.8 Create `app/src/main/java/com/felipelucio/lugrav/interfaces/FileProviderImpl.kt` implementing `FileProvider` with `Context` delegation
- [x] 1.9 Verify compilation: `./gradlew compileDebugKotlin --no-daemon`

## 2. Refactor AudioRecordingRepository

- [x] 2.1 Modify `AudioRecordingRepository.kt` constructor to accept: `fileProvider: FileProvider`, `audioRecorder: AudioRecorder`, `audioPlayer: AudioPlayer`, `metadataRetriever: MetadataRetriever`
- [x] 2.2 Remove Android imports (`MediaRecorder`, `MediaPlayer`, `MediaMetadataRetriever`, `Build`)
- [x] 2.3 Add interface imports from `com.felipelucio.lugrav.interfaces.*`
- [x] 2.4 Replace `MediaRecorder` usage in `startRecording()` with `audioRecorder.setup(...)` and `audioRecorder.prepare()`, `audioRecorder.start()`
- [x] 2.5 Replace `MediaRecorder` usage in `stopRecording()` with `audioRecorder.stop()`, `audioRecorder.release()`
- [x] 2.6 Replace `MediaPlayer` usage in `playAudio()` with `audioPlayer.setDataSource()`, `audioPlayer.prepare()`, `audioPlayer.start()`, `audioPlayer.setOnCompletionListener()`
- [x] 2.7 Replace `MediaPlayer` usage in `pauseAudio()`, `resumeAudio()`, `stopAudio()` with `audioPlayer` methods
- [x] 2.8 Replace `MediaPlayer` usage in `getCurrentPosition()`, `getDuration()` with `audioPlayer` methods
- [x] 2.9 Replace `Context` usage in `stopAndSave()` and `getRecordingsList()` with `fileProvider.getCacheDir()`, `fileProvider.getExternalFilesDir()`
- [x] 2.10 Replace `MediaMetadataRetriever` usage in `getAudioDuration()` with `metadataRetriever` methods
- [x] 2.11 Keep all business logic identical: file naming (`lugrav_YYYYMMdd_HHmmss.aac`), duration formatting, error handling
- [x] 2.12 Verify compilation: `./gradlew compileDebugKotlin --no-daemon`

## 3. Update Dependency Injection (Koin)

- [x] 3.1 Modify `app/src/main/java/com/felipelucio/lugrav/di/AppModule.kt` to add: `single<FileProvider> { FileProviderImpl(androidContext()) }`
- [x] 3.2 Add: `single<AudioRecorder> { AudioRecorderImpl(androidContext()) }`
- [x] 3.3 Add: `single<AudioPlayer> { AudioPlayerImpl() }`
- [x] 3.4 Add: `single<MetadataRetriever> { MetadataRetrieverImpl() }`
- [x] 3.5 Modify: `single { AudioRecordingRepository(get(), get(), get(), get()) }`
- [x] 3.6 Verify compilation: `./gradlew compileDebugKotlin --no-daemon`

## 4. Verification

- [x] 4.1 Run full build: `./gradlew clean build --no-daemon`
- [ ] 4.2 Verify app installs and runs on device/emulator (manual)
- [ ] 4.3 Test recording functionality manually (manual)
- [ ] 4.4 Test playback functionality manually (manual)
- [ ] 4.5 Verify file listing and deletion work correctly (manual)
