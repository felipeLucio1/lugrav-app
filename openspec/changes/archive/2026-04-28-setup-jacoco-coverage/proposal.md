## Why

The project lacks code coverage reporting for unit tests, making it impossible to measure test effectiveness. Setting up Jacoco will provide visibility into which parts of the codebase are covered by tests, helping identify untested code paths.

## What Changes

- Add Jacoco plugin to `app/build.gradle.kts`
- Configure unit test coverage for debug build type
- Create custom `jacocoTestReport` task to generate coverage reports
- Configure report formats (HTML, XML) for CI integration
- Exclude generated and non-relevant classes from coverage reports

## Capabilities

### New Capabilities
- `jacoco-coverage`: Configure Jacoco plugin to generate code coverage reports for unit tests in HTML and XML formats

### Modified Capabilities

## Impact

- **Build configuration**: `app/build.gradle.kts` will be modified
- **Dependencies**: Jacoco plugin added (no external dependencies, built into Gradle)
- **Output**: Coverage reports generated at `app/build/reports/jacoco/`
- **CI/CD**: XML reports can be integrated with coverage tracking tools
