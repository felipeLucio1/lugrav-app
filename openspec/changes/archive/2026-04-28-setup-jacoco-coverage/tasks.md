## 1. Build Configuration

- [x] 1.1 Add Jacoco plugin to `libs.versions.toml` (if using version catalog) or directly in `build.gradle.kts`
- [x] 1.2 Apply Jacoco plugin in `app/build.gradle.kts` using `alias(libs.plugins.jacoco)` or `id("jacoco")`
- [x] 1.3 Enable unit test coverage for debug build type by setting `enableUnitTestCoverage = true` in `android.buildTypes.debug`

## 2. Coverage Report Task

- [x] 2.1 Create `jacocoTestReport` task of type `JacocoReport` in `app/build.gradle.kts`
- [x] 2.2 Configure task dependencies: `dependsOn("testDebugUnitTest")`
- [x] 2.3 Enable HTML report: `reports.html.required.set(true)`
- [x] 2.4 Enable XML report: `reports.xml.required.set(true)`
- [x] 2.5 Configure source directories pointing to `src/main/java`
- [x] 2.6 Configure class directories pointing to `build/tmp/kotlin-classes/debug`
- [x] 2.7 Configure execution data to include `**/*.exec` and `**/*.ec` files
- [x] 3.1 Define file filter list excluding `**/R.class`, `**/R$*.class`, `**/BuildConfig.*`, `**/Manifest*.*`, `**/*Test*.*`, `android/**/*.*`
- [x] 3.2 Apply exclusions to class directories in the `jacocoTestReport` task

## 4. Verification

- [x] 4.1 Run `./gradlew testDebugUnitTest jacocoTestReport --no-daemon` to verify setup
- [x] 4.2 Check HTML report exists at `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- [x] 4.3 Check XML report exists at `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`
- [x] 4.4 Open HTML report and verify coverage data is displayed for `AudioRecordingViewModel`
