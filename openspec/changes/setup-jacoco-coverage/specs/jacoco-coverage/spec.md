## ADDED Requirements

### Requirement: Jacoco plugin configuration
The build system SHALL have the Jacoco plugin applied to enable code coverage reporting.

#### Scenario: Plugin applied successfully
- **WHEN** the project is built
- **THEN** the Jacoco plugin is applied to the app module

### Requirement: Unit test coverage enabled
The debug build type SHALL have unit test coverage enabled.

#### Scenario: Coverage enabled for debug builds
- **WHEN** the debug build type is configured
- **THEN** `enableUnitTestCoverage` is set to `true`

### Requirement: Coverage report generation task
The project SHALL have a `jacocoTestReport` task that generates coverage reports after unit tests.

#### Scenario: Generate HTML and XML reports
- **WHEN** `jacocoTestReport` task is executed
- **THEN** HTML report is generated at `app/build/reports/jacoco/jacocoTestReport/html/`
- **AND** XML report is generated at `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

### Requirement: Exclude non-relevant classes from coverage
The coverage report SHALL exclude generated classes, build config, manifest files, and Android framework classes.

#### Scenario: Correctly exclude generated classes
- **WHEN** coverage report is generated
- **THEN** classes matching patterns like `**/R.class`, `**/R$*.class`, `**/BuildConfig.*`, `**/Manifest*.*`, `**/*Test*.*`, `android/**/*.*` are excluded from the report
