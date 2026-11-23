## ADDED Requirements

### Requirement: Official SplashScreen API
The app SHALL use the official AndroidX SplashScreen compatibility library to display the launch splash screen.

#### Scenario: Splash is installed before activity creation
- **WHEN** `MainActivity` starts
- **THEN** `installSplashScreen()` SHALL be called before `super.onCreate()`
- **AND** it SHALL be called before any content view or Compose content is set

#### Scenario: Post-splash theme is applied
- **WHEN** the splash screen is dismissed
- **THEN** the activity SHALL use `@style/Theme.ProjTeste`
- **AND** `postSplashScreenTheme` SHALL reference that theme

### Requirement: Splash waits for initial recordings load
The system SHALL keep the splash screen visible until the initial recordings list load completes.

#### Scenario: Cold or warm start waits for recordings
- **WHEN** the app starts and `isLoadingRecordings` is `true`
- **THEN** the splash screen SHALL remain visible
- **AND** it SHALL be dismissed only after the initial load finishes

#### Scenario: Empty recordings list dismisses splash
- **WHEN** the initial load completes with an empty list
- **THEN** the splash screen SHALL be dismissed
- **AND** the main screen SHALL display the empty state

#### Scenario: Failed load dismisses splash
- **WHEN** the initial load fails
- **THEN** `isLoadingRecordings` SHALL become `false`
- **AND** the splash screen SHALL be dismissed
- **AND** the app SHALL remain usable

#### Scenario: Hot start does not show splash
- **WHEN** the app is resumed from a hot start
- **THEN** the official SplashScreen API SHALL not display the splash screen
- **AND** the already loaded recordings SHALL remain available

### Requirement: Single initial load trigger
The system SHALL trigger the initial recordings load only once per activity creation.

#### Scenario: Activity triggers initial load
- **WHEN** `MainActivity` is created
- **THEN** `loadRecordings()` SHALL be called before `setContent`

#### Scenario: Screen does not duplicate initial load
- **WHEN** `LugravScreen` is composed for the first time
- **THEN** it SHALL NOT call `loadRecordings()` for the initial load

### Requirement: Splash visual tokens
The splash screen SHALL follow the app design tokens.

#### Scenario: Splash uses app background and primary icon background
- **WHEN** the splash screen is displayed
- **THEN** `windowSplashScreenBackground` SHALL be `@color/surface_color` (`#FFFBFE`)
- **AND** `windowSplashScreenIconBackgroundColor` SHALL be `@color/sky_blue` (`#29B6F6`)
- **AND** `windowSplashScreenAnimatedIcon` SHALL be `@drawable/ic_launcher_foreground`

### Requirement: Non-blocking initial load
The initial recordings load SHALL execute outside the main thread.

#### Scenario: List load runs on IO dispatcher
- **WHEN** `loadRecordings()` executes
- **THEN** `repository.getRecordingsList()` SHALL run with the injected IO dispatcher
- **AND** the splash condition SHALL read only the current loading state
