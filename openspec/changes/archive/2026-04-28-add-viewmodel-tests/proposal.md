## Why

The AudioRecordingViewModel currently has no unit tests, making it difficult to verify correct behavior and catch regressions. Implementing comprehensive tests using MockK and kotlinx-coroutines-test will ensure the ViewModel's audio recording, playback, and state management work correctly.

## What Changes

- Add unit test class `AudioRecordingViewModelTest` with comprehensive test cases
- Configure test dependencies using Gradle Version Catalogs (`libs.versions.toml`)
- Implement tests for recording, playback, deletion, and state management scenarios
- Use `runTest` from kotlinx-coroutines-test for proper coroutine testing
- Use MockK for mocking `AudioRecordingRepository`

## Capabilities

### New Capabilities
- `viewmodel-unit-tests`: Unit tests for AudioRecordingViewModel covering recording, playback, deletion, and state management

### Modified Capabilities

## Impact

- **New files**: `app/src/test/java/com/felipelucio/lugrav/AudioRecordingViewModelTest.kt`
- **Modified files**: `gradle/libs.versions.toml` (add test dependencies), `app/build.gradle.kts` (add test implementations)
- **Dependencies**: MockK 1.14.2, kotlinx-coroutines-test 1.10.2, Turbine 1.2.0
