## Context

The `AudioRecordingRepository.kt` currently has direct dependencies on Android classes:
- `MediaRecorder` (recording audio)
- `MediaPlayer` (playing audio)
- `MediaMetadataRetriever` (extracting audio duration)
- `Context` (file operations via `cacheDir`, `getExternalFilesDir()`)

This makes unit testing extremely difficult - requires Robolectric or complex mocking. The goal is to extract interfaces following SOLID's Dependency Inversion Principle (DIP), making the Repository 100% testable with MockK alone.

**Current architecture:**
```
AudioRecordingRepository
    └── MediaRecorder (Android)
    └── MediaPlayer (Android)
    └── MediaMetadataRetriever (Android)
    └── Context (Android)
```

**Target architecture:**
```
AudioRecordingRepository
    └── AudioRecorder (interface) → AudioRecorderImpl
    └── AudioPlayer (interface) → AudioPlayerImpl
    └── MetadataRetriever (interface) → MetadataRetrieverImpl
    └── FileProvider (interface) → FileProviderImpl
```

## Goals / Non-Goals

**Goals:**
- Extract 4 interfaces from Android dependencies
- Create 4 implementation classes wrapping Android APIs
- Refactor `AudioRecordingRepository` to depend on interfaces
- Preserve 100% of original behavior
- Make Repository fully testable with MockK (no Robolectric needed)

**Non-Goals:**
- Changing business logic or audio formats
- Modifying `AudioRecordingViewModel` (it already uses Repository via constructor)
- Adding new features (file format, compression, etc.)
- Changing the Koin DI setup beyond adding new modules

## Decisions

### Decision 1: Extract interfaces vs. keep Repository as-is

**Decision**: Extract interfaces following DIP.

**Rationale**: 
- Current: Testing requires Robolectric (slow, complex setup)
- With interfaces: Testing with `mockk<AudioRecorder>()` is trivial
- Aligns with Android recommendation: "In most cases, only use Robolectric for unit testing as a last resort"

**Alternatives considered:**
- Keep as-is + use Robolectric: Rejected (defeats purpose of fast unit tests)
- Use wrapper class without interface: Rejected (can't mock without interface)

### Decision 2: Number of interfaces (4 total)

**Decision**: Create 4 separate interfaces:
1. `AudioRecorder` - wraps `MediaRecorder`
2. `AudioPlayer` - wraps `MediaPlayer`
3. `MetadataRetriever` - wraps `MediaMetadataRetriever`
4. `FileProvider` - wraps `Context` file operations

**Rationale**: Single Responsibility Principle - each interface has one reason to change.

**Alternatives considered:**
- Single `AudioManager` interface: Rejected (violates ISP - Interface Segregation Principle)
- 2 interfaces (recording + playback): Rejected (still mixes concerns)

### Decision 3: Interface method signatures

**Decision**: Mirror Android API methods exactly.

**Rationale**: Minimizes changes to Repository logic. The `AudioRecorderImpl` will directly delegate to `MediaRecorder`.

**Example:**
```kotlin
// Interface
interface AudioRecorder {
    fun setup(audioSource: Int, outputFormat: Int, ...)
    fun prepare()
    fun start()
    fun stop()
    fun release()
}

// Implementation
class AudioRecorderImpl(private val context: Context) : AudioRecorder {
    private var recorder: MediaRecorder? = null
    
    override fun setup(...) {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else { MediaRecorder() }
        recorder?.apply { /* set properties */ }
    }
    // ...
}
```

### Decision 4: Package structure

**Decision**: Create `com.felipelucio.lugrav.interfaces` package.

**Rationale**: Clear separation from main code. Follows standard Java/Kotlin conventions.

**Structure:**
```
com.felipelucio.lugrav/
├── interfaces/
│   ├── AudioRecorder.kt
│   ├── AudioRecorderImpl.kt
│   ├── AudioPlayer.kt
│   ├── AudioPlayerImpl.kt
│   ├── MetadataRetriever.kt
│   ├── MetadataRetrieverImpl.kt
│   ├── FileProvider.kt
│   └── FileProviderImpl.kt
├── AudioRecordingRepository.kt (modified)
└── di/AppModule.kt (modified)
```

### Decision 5: Maintaining conditional MediaRecorder creation

**Decision**: Keep SDK version check inside `AudioRecorderImpl.setup()`.

**Rationale**: This is platform-specific logic that belongs in the implementation, not the interface.

## Risks / Trade-offs

**[Risk] More files (8 new files)** → **Mitigation**: Clear package structure; each file is small and focused.

**[Risk] Learning curve for team** → **Mitigation**: Standard SOLID pattern; well-documented interfaces.

**[Risk] Possible DI configuration issues** → **Mitigation**: Test compilation after each step; follow Koin documentation.

**[Risk] Breaking change for ViewModel tests** → **Mitigation**: ViewModel receives same Repository type; no changes needed in ViewModel or its tests.

**[Trade-off] More abstraction vs. testability**: The 8 new files are worth the trade-off for 100% testability without Robolectric.

## Migration Plan

### Step 1: Create interfaces (no breaking changes yet)
- Create `interfaces/` package
- Add 4 interface files
- Add 4 implementation files
- **Verify**: `./gradlew compileDebugKotlin` passes

### Step 2: Refactor Repository
- Modify constructor to accept interfaces
- Replace Android class usage with interface calls
- Keep all business logic identical (file naming, duration formatting, etc.)
- **Verify**: `./gradlew compileDebugKotlin` passes

### Step 3: Update DI (Koin)
- Modify `AppModule.kt` to provide interface implementations
- **Verify**: `./gradlew compileDebugKotlin` passes

### Step 4: Test
- Create `AudioRecordingRepositoryTest.kt` with MockK
- **Verify**: `./gradlew testDebugUnitTest` passes

### Rollback Strategy
If issues arise, revert in reverse order:
1. Revert `AppModule.kt`
2. Revert `AudioRecordingRepository.kt`
3. Delete `interfaces/` package

All changes are compile-time safe - no runtime surprises expected.

## Open Questions

1. **Should `AudioRecorderImpl` hold state (`tempFile`, `mediaRecorder`) or should Repository manage it?**
   - Decision: Keep state in Impl classes (encapsulation). Repository doesn't need to know about `tempFile`.

2. **Should we make interfaces `fun` interfaces (SAM) instead of regular interfaces?**
   - Decision: No, they have multiple methods. SAM is for single-method interfaces.

3. **Should `FileProvider` expose `Context` or just file-related methods?**
   - Decision: Just file-related methods (`getCacheDir()`, `getExternalFilesDir()`) to avoid leaking Android dependencies.
