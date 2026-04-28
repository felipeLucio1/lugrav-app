## Context

The `AudioRecordingRepository` was refactored to depend on 4 interfaces (`AudioRecorder`, `AudioPlayer`, `MetadataRetriever`, `FileProvider`) instead of concrete Android classes. This enables unit testing with mocked dependencies. Currently, no unit tests exist for the repository. The project already uses MockK for mocking and JUnit 4 for testing.

## Goals / Non-Goals

**Goals:**
- Create comprehensive unit tests for all `AudioRecordingRepository` public methods
- Mock all 4 interfaces using MockK relaxed mocks
- Verify correct interactions between repository and dependencies
- Test error scenarios and edge cases
- Follow existing test naming convention (`when [action] then [expected]`)

**Non-Goals:**
- No integration tests or instrumented tests
- No testing of interface implementations (`*Impl.kt` files)
- No modifications to production code
- No Robolectric or Android dependency testing

## Decisions

### 1. Use MockK relaxed mocks
**Decision**: Use `mockk(relaxed = true)` for all 4 interfaces
**Rationale**: Relaxed mocks return default values for methods not explicitly stubbed, reducing boilerplate
**Alternative considered**: Strict mocks requiring explicit stubbing - rejected due to verbosity

### 2. No `runTest` wrapper needed
**Decision**: Do not wrap tests with `runTest {}`
**Rationale**: `AudioRecordingRepository` has no suspend functions, so coroutine test infrastructure is unnecessary
**Alternative considered**: Using `runTest` anyway - rejected as unnecessary complexity

### 3. Test naming convention
**Decision**: Follow existing pattern `when [action] then [expected]`
**Rationale**: Consistency with existing `AudioRecordingViewModelTest.kt`
**Example**: `when start recording then should setup audio recorder with correct parameters`

### 4. Verify interactions using MockK verification
**Decision**: Use `verify { }` blocks to check method call order and parameters
**Rationale**: Important to verify the repository correctly coordinates multiple interface calls
**Example**: Verify `setup()` called before `prepare()` before `start()` in `startRecording()`

## Risks / Trade-offs

- **Risk**: Tests may be tightly coupled to implementation details (method call order)
  → **Mitigation**: Focus on verifying state changes and important interactions, not every method call
  
- **Risk**: Relaxed mocks may hide issues by returning defaults
  → **Mitigation**: Explicitly stub and verify critical return values (e.g., file paths, durations)

- **Trade-off**: 30 tests is comprehensive but may be slow
  → **Mitigation**: Unit tests are fast; no Android instrumentation needed
