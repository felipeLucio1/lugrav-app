## Why

The `AudioRecordingRepository` was recently refactored to use interfaces (AudioRecorder, AudioPlayer, MetadataRetriever, FileProvider) for testability, but no unit tests exist to verify its behavior. Unit tests are needed to ensure the repository methods work correctly with mocked dependencies and to catch regressions.

## What Changes

- Create `AudioRecordingRepositoryTest.kt` with comprehensive unit tests
- Mock all 4 interfaces using MockK (relaxed mocks)
- Test all public methods: `startRecording()`, `stopRecording()`, `stopAndSave()`, `getRecordingsList()`, `playAudio()`, `pauseAudio()`, `resumeAudio()`, `stopAudio()`, `deleteRecording()`, `getCurrentPosition()`, `getDuration()`
- Verify interactions with mocked interfaces (setup parameters, method call order)
- Test error scenarios (file not found, delete failures, duration extraction errors)
- Follow existing test naming convention: `when [action] then [expected]`

## Capabilities

### New Capabilities
- `repository-unit-tests`: Unit tests for AudioRecordingRepository covering all public methods and error scenarios

### Modified Capabilities
<!-- No existing spec changes -->

## Impact

- **New file**: `app/src/test/java/com/felipelucio/lugrav/AudioRecordingRepositoryTest.kt`
- **Dependencies**: MockK, JUnit 4 (already configured)
- **No production code changes**: Tests only, no modifications to repository or interfaces
- **Coverage**: Improves test coverage for the data layer
