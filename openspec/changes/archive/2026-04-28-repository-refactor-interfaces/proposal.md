## Why

The `AudioRecordingRepository.kt` directly depends on Android classes (`MediaRecorder`, `MediaPlayer`, `MediaMetadataRetriever`, `Context`) making it impossible to unit test without Robolectric or complex mocking. Extracting interfaces following SOLID's Dependency Inversion Principle will make the repository fully testable with MockK alone, while preserving identical behavior.

## What Changes

- Extract `AudioRecorder` interface from `MediaRecorder` usage with `AudioRecorderImpl` implementation
- Extract `AudioPlayer` interface from `MediaPlayer` usage with `AudioPlayerImpl` implementation  
- Extract `MetadataRetriever` interface from `MediaMetadataRetriever` usage with `MetadataRetrieverImpl` implementation
- Extract `FileProvider` interface from `Context` usage with `FileProviderImpl` implementation
- Refactor `AudioRecordingRepository` to depend on interfaces instead of concrete Android classes
- Update `AppModule.kt` (Koin DI) to provide new interface implementations
- Add new package `com.felipelucio.lugrav.interfaces` with 8 files (4 interfaces + 4 implementations)

## Capabilities

### New Capabilities
- `repository-interfaces`: Extract Android dependencies into testable interfaces following SOLID DIP

### Modified Capabilities
- `audio-recording-repository`: Repository now depends on interfaces instead of concrete Android classes (implementation change, not behavioral)

## Impact

- **New files**: 8 files in `app/src/main/java/com/felipelucio/lugrav/interfaces/`
- **Modified files**: `AudioRecordingRepository.kt`, `di/AppModule.kt`
- **Dependencies**: No new external dependencies (uses existing Koin for DI)
- **Behavior**: 100% identical - all business logic preserved
- **Testability**: Repository becomes 100% testable with MockK (no Robolectric needed)
