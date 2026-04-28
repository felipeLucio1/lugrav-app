## 1. Configure Test Dependencies

- [x] 1.1 Add test dependency versions to `gradle/libs.versions.toml` (mockk = "1.14.2", kotlinx-coroutines-test = "1.10.2", turbine = "1.2.0")
- [x] 1.2 Add test libraries to `gradle/libs.versions.toml` (mockk, kotlinx-coroutines-test, turbine)
- [x] 1.3 Add test implementation dependencies to `app/build.gradle.kts` (testImplementation for mockk, kotlinx-coroutines-test, turbine)

## 2. Create Test Class Structure

- [x] 2.1 Create directory `app/src/test/java/com/felipelucio/lugrav/`
- [x] 2.2 Create `AudioRecordingViewModelTest.kt` with class declaration and imports
- [x] 2.3 Setup test class with `@Before` method configuring `UnconfinedTestDispatcher` with shared `TestCoroutineScheduler`
- [x] 2.4 Add `@After` method to reset `Dispatchers.main`
- [x] 2.5 Initialize mock repository and ViewModel in setup

## 3. Implement Recording State Tests

- [x] 3.1 Implement `whenRecordingAudio_isRecordingStateShouldBeTrue` test
- [x] 3.2 Implement test for recording time increments during recording
- [x] 3.3 Implement `whenStartRecording_failure_shouldSetRecordingSuccessError` test

## 4. Implement Playback State Tests

- [x] 4.1 Implement test for `playAudio` sets isPlaying to true
- [x] 4.2 Implement `whenStopRproducingAudio_playingAudioStateSHouldBeSettedToNull` test
- [x] 4.3 Implement test for `pauseAudio` sets isPlaying to false
- [x] 4.4 Implement test for `resumeAudio` sets isPlaying to true
- [x] 4.5 Implement test for playback completion resets state

## 5. Implement Toggle Play/Pause Tests

- [x] 5.1 Implement `givenSameAudioPaused_whenTogglePlayPause_shouldResume` test
- [x] 5.2 Implement `whenTogglePlayPause_differentAudioPlaying_shouldStopPreviousAndPlayNew` test
- [x] 5.3 Implement test for toggle when no audio is playing

## 6. Implement Deletion Behavior Tests

- [x] 6.1 Implement `givenSomeAudioIsPlaying_whenDeleteAnAudio_shouldStopPlayingAudio` test
- [x] 6.2 Implement `whenDeleteAudio_audioListShouldBeReload` test
- [x] 6.3 Implement `whenDeleteRecording_failure_shouldSetDeleteResultError` test

## 7. Implement Recording and Playback Interaction Tests

- [x] 7.1 Implement `givenSomeAudioIsBeenReproduced_whenRecordAudio_shouldStopAudioExecution` test
- [x] 7.2 Implement `givenSomeAudioIsPlaying_whenPlayOtherAudio_shouldStopFirstOne` test

## 8. Implement Finalize Recording Tests

- [x] 8.1 Implement test for `finalizeRecording` resets state and reloads list
- [x] 8.2 Implement test for failed finalize resets state and sets error

## 9. Implement Cleanup and Utility Tests

- [x] 9.1 Implement `whenOnCleared_shouldStopAudio` test
- [x] 9.2 Implement `whenClearSelectedAudio_shouldSetToNull` test
- [x] 9.3 Implement `whenClearDeleteResult_shouldSetToNull` test

## 10. Verify Tests

- [x] 10.1 Run tests and verify all pass
- [x] 10.2 Check test coverage for AudioRecordingViewModel
