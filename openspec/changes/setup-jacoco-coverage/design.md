## Context

The Android project `ProjTeste` currently has unit tests that pass successfully, but lacks code coverage reporting. The project uses Gradle with Kotlin DSL (`build.gradle.kts`) and has unit tests using MockK and Coroutines test libraries. There is no visibility into which parts of the codebase are covered by tests.

## Goals / Non-Goals

**Goals:**
- Enable Jacoco code coverage for unit tests in debug builds
- Generate HTML reports for local developer viewing
- Generate XML reports for potential CI/CD integration
- Exclude generated and non-relevant classes from coverage reports

**Non-Goals:**
- Instrumented test coverage (only unit tests)
- Integration with external coverage services (Codecov, SonarQube, etc.)
- Coverage enforcement rules (minimum coverage thresholds)

## Decisions

### Use Gradle's built-in Jacoco plugin
**Decision**: Apply the `jacoco` plugin via `alias(libs.plugins.jacoco)` in `build.gradle.kts`.
**Rationale**: Jacoco is the standard coverage tool for JVM/Android projects and is built into Gradle. No external dependencies needed.
**Alternatives considered**: None - Jacoco is the de facto standard for Android projects.

### Enable coverage only for debug builds
**Decision**: Set `enableUnitTestCoverage = true` only in the debug build type.
**Rationale**: Coverage instrumentation slows down builds. Debug builds are for development/testing, while release builds should be optimized.
**Alternatives considered**: Enable for all build types - rejected due to build time impact.

### Create custom `jacocoTestReport` task
**Decision**: Register a `JacocoReport` task named `jacocoTestReport` that depends on `testDebugUnitTest`.
**Rationale**: Provides a simple, memorable command (`./gradlew jacocoTestReport`) to generate reports. The default Jacoco tasks have less predictable naming.
**Alternatives considered**: Relying on default Jacoco tasks - rejected for usability.

### Exclude generated and framework classes
**Decision**: Exclude patterns like `**/R.class`, `**/R$*.class`, `**/BuildConfig.*`, `**/Manifest*.*`, `**/*Test*.*`, `android/**/*.*`.
**Rationale**: These classes are generated or part of the framework - they shouldn't count toward coverage metrics as they're not user-written code.
**Alternatives considered**: Minimal exclusions - rejected as it would inflate coverage numbers artificially.

## Risks / Trade-offs

**[Longer build times]** → Mitigation: Only enable in debug builds; developers can run tests without coverage when speed is needed.

**[Large coverage files]** → Mitigation: Add `build/` to `.gitignore`; coverage files are generated artifacts.

**[Incomplete coverage data]** → Mitigation: HTML report clearly shows which classes/methods lack coverage; use this to identify gaps.
