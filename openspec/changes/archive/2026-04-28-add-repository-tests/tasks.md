## 1. Setup Test Class

- [x] 1.1 Create `AudioRecordingRepositoryTest.kt` in `app/src/test/java/com/felipelucio/lugrav/`
- [x] 1.2 Add imports: MockK, JUnit 4, java.io.File, java.io.FileInputStream
- [x] 1.3 Declare lateinit properties: `fileProvider`, `audioRecorder`, `audioPlayer`, `metadataRetriever`, `repository`
- [x] 1.4 Setup `@Before` method: create relaxed mocks for all 4 interfaces, initialize repository with mocks

## 2. Implement startRecording() Tests

- [x] 2.1 Implement `when start recording then should create temp file in cache directory` test
- [x] 2.2 Implement `when start recording then should setup audio recorder with correct parameters` test
- [x] 2.3 Implement `when start recording then should call prepare and start on recorder` test

## 3. Implement stopRecording() Tests

- [x] 3.1 Implement `when stop recording then should stop and release audio recorder` test
- [x] 3.2 Implement `when stop recording then should read bytes from temp file` test
- [x] 3.3 Implement `when stop recording then should delete temp file after reading` test
- [x] 3.4 Implement `when stop recording with null temp file then should return empty byte array` test

## 4. Implement stopAndSave() Tests

- [x] 4.1 Implement `when stop and save then should create app folder if not exists` test
- [x] 4.2 Implement `when stop and save then should generate file name with correct pattern` test
- [x] 4.3 Implement `when stop and save then should write audio bytes to file` test
- [x] 4.4 Implement `when stop and save then should return absolute path of saved file` test

## 5. Implement getRecordingsList() Tests

- [x] 5.1 Implement `when get recordings list and folder not exists then should return empty list` test
- [x] 5.2 Implement `when get recordings list then should return list of audio recording models` test
- [x] 5.3 Implement `when get recordings list then should filter only aac files` test
- [x] 5.4 Implement `when get recordings list then should extract duration for each file` test

## 6. Implement playAudio() Tests

- [x] 6.1 Implement `when play audio then should stop current playback before playing` test
- [x] 6.2 Implement `when play audio then should set data source to file path` test
- [x] 6.3 Implement `when play audio then should prepare and start player` test
- [x] 6.4 Implement `when play audio then should set completion listener` test

## 7. Implement pauseAudio() and resumeAudio() Tests

- [x] 7.1 Implement `when pause audio then should call pause on audio player` test
- [x] 7.2 Implement `when resume audio then should call start on audio player` test

## 8. Implement stopAudio() Test

- [x] 8.1 Implement `when stop audio then should stop and release audio player` test

## 9. Implement deleteRecording() Tests

- [x] 9.1 Implement `when delete recording then should stop audio playback first` test
- [x] 9.2 Implement `when delete recording then should delete the file` test
- [x] 9.3 Implement `when delete recording with non-existent file then should throw IOException` test
- [x] 9.4 Implement `when delete recording fails then should throw IOException` test

## 10. Implement getCurrentPosition() and getDuration() Tests

- [x] 10.1 Implement `when get current position then should return position from player` test
- [x] 10.2 Implement `when get duration then should return duration from player` test

## 11. Implement Error Handling Tests

- [x] 11.1 Implement `when get audio duration fails then should return default 00:00:00` test
- [x] 11.2 Implement `when get recordings list with null file array then should return empty list` test

## 12. Verify Tests

- [x] 12.1 Run tests and verify all 30 tests pass using `./gradlew testDebugUnitTest`
- [x] 12.2 Check test coverage for AudioRecordingRepository using Jacoco report
