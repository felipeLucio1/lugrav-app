# Tasks

## 1. Rules and Repository

- [x] 1.1 Add `RecordingFileRules.isValidRecordingName(raw)` that trims input, rejects blank/control/overlong names and accepts a name equal to the suggested recording name; verify with `.\gradlew.bat :app:compileDebugKotlin --no-daemon --console=plain`.
- [x] 1.2 Add `AudioRecordingRepository.saveRecordingReview(filePath, newName, tagNames)` that renames only when needed, persists tags on the final path and rolls the rename back when tag persistence fails; verify with the new repository tests in task 1.3.
- [x] 1.3 Add `AudioRecordingRepositoryTest` cases for same-name save, renamed save, empty tags, and rollback on tag persistence failure; verify with `.\gradlew.bat :app:testDebugUnitTest --tests "*AudioRecordingRepositoryTest" --no-daemon --console=plain`.

## 2. ViewModel

- [x] 2.1 Add `recordingReviewPath`, `isRecordingReviewInProgress` and `recordingReviewResult` state to `AudioRecordingViewmodel`; verify with `.\gradlew.bat :app:compileDebugKotlin --no-daemon --console=plain`.
- [x] 2.2 Change `finalizeRecording()` to publish the saved path for review instead of emitting the immediate success snackbar; verify with the new ViewModel finalization tests in task 2.5.
- [x] 2.3 Add `saveRecordingReview(name, tags)`, `discardRecordingReview()`, `keepRecordingReview()` and `clearRecordingReviewResult()` with validation, reload-after-action and error handling; verify with the new ViewModel review tests in task 2.5.
- [x] 2.4 Ensure save/discard/keep clear the review only on success or dismissal and keep it open on failure; verify with the failure-path ViewModel tests in task 2.5.
- [x] 2.5 Add `AudioRecordingViewModelTest` cases for finalize success/failure, save same/new name, invalid name, save failure, discard success/failure and keep; verify with `.\gradlew.bat :app:testDebugUnitTest --tests "*AudioRecordingViewModelTest" --no-daemon --console=plain`.

## 3. UI and Strings

- [x] 3.1 Add review strings to `app/src/main/res/values/strings.xml`; verify with `.\gradlew.bat :app:compileDebugKotlin --no-daemon --console=plain`.
- [x] 3.2 Create `RecordingReviewDialog` with prefilled selected name, tag checkboxes, `Salvar` enabled only for a valid name and `Descartar` action; verify with `.\gradlew.bat :app:compileDebugKotlin --no-daemon --console=plain`.
- [x] 3.3 Wire `LugravScreen` to open the review dialog from `recordingReviewPath`, handle save/discard/dismiss and show success/error feedback; verify with `.\gradlew.bat :app:compileDebugKotlin --no-daemon --console=plain`.

## 4. Integration Verification

- [x] 4.1 Run `.\gradlew.bat :app:testDebugUnitTest :app:lintDebug --no-daemon --console=plain` and confirm unit tests and lint pass.
- [x] 4.2 Run `openspec validate "add-recording-review-dialog"` and confirm the change validates.
- [x] 4.3 Run `git diff --check` and inspect the diff for scope creep.

## Workflow follow-up

- Archive the change after review and validation are complete.
