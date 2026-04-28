## Context

The project uses Kotlin 2.2.10 with coroutines, ViewModel architecture, and Gradle Version Catalogs (`libs.versions.toml`). The `AudioRecordingViewModel` handles audio recording, playback, and file management but lacks unit tests. The test setup must follow official Kotlin coroutines testing guidelines and use MockK for mocking.

## Goals / Non-Goals

**Goals:**
- Add comprehensive unit tests for `AudioRecordingViewModel`
- Configure test dependencies using Gradle Version Catalogs
- Use `runTest` from kotlinx-coroutines-test with shared `TestCoroutineScheduler`
- Use MockK for repository mocking with `coEvery`/`coVerify` for suspend functions

**Non-Goals:**
- Integration tests or instrumented tests (androidTest)
- Testing the `AudioRecordingRepository` implementation
- UI tests or Compose tests

## Decisions

### Decision 1: Use `UnconfinedTestDispatcher` with shared `TestCoroutineScheduler`
**Rationale**: Official Kotlin docs recommend sharing the scheduler between all test dispatchers. `UnconfinedTestDispatcher` runs coroutines eagerly, making tests easier to write while maintaining determinism.

**Alternative considered**: `StandardTestDispatcher` - requires explicit `advanceUntilIdle()` calls, more verbose.

### Decision 2: Configure dependencies in `libs.versions.toml`
**Rationale**: Project already uses Version Catalogs. Adding `mockk`, `kotlinx-coroutines-test`, and `turbine` versions and libraries there maintains consistency.

**Versions chosen**:
- MockK 1.14.2 (compatible with Kotlin 2.2.x)
- kotlinx-coroutines-test 1.10.2 (compatible with Kotlin 2.2.x)
- Turbine 1.2.0 (for Flow testing, optional but recommended)

### Decision 3: Use `Dispatchers.setMain(testDispatcher)` in setup
**Rationale**: Official Android coroutines testing guide recommends injecting test dispatchers. This allows `viewModelScope` to use the test dispatcher, making coroutines deterministic in tests.

### Decision 4: MockK `relaxed = true` for repository
**Rationale**: Reduces boilerplate for methods not relevant to each test. Explicit `coEvery` stubs will be added only where behavior verification is needed.

## Risks / Trade-offs

- **Risk**: `UnconfinedTestDispatcher` may hide race conditions → **Mitigation**: Use `StandardTestDispatcher` for tests that specifically verify concurrency
- **Risk**: Relaxed mocking may hide missing stubs → **Mitigation**: Explicit `coVerify` for critical interactions
- **Trade-off**: Turbine adds dependency but simplifies Flow assertions → Acceptable for better test readability
