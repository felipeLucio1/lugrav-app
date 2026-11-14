## ADDED Requirements

### Requirement: Light-only application theme
The application SHALL expose a single light theme and SHALL NOT provide a dark theme.

#### Scenario: Light theme is applied
- **WHEN** the app content is composed
- **THEN** it SHALL use `LugravLightTheme`
- **AND** the color scheme SHALL be created with `lightColorScheme`
- **AND** no dark color scheme SHALL be selected from system settings

### Requirement: Core color tokens
The light theme SHALL define a consistent set of core color tokens for primary, surfaces, cards, text, outline and error states.

#### Scenario: Primary action color
- **WHEN** primary actions are rendered
- **THEN** they SHALL use `SkyBlue` (`#29B6F6`) as the primary color
- **AND** content on primary surfaces SHALL use white

#### Scenario: Surface hierarchy
- **WHEN** screen, card and dialog surfaces are rendered
- **THEN** the screen SHALL use the light background token
- **AND** cards SHALL use a tonal surface distinct from the background
- **AND** primary text SHALL use a dark neutral token
- **AND** secondary text SHALL use a muted neutral token

### Requirement: Typography with Space Grotesk and Inter
The application SHALL use Space Grotesk for titles and headlines and Inter for body text, metadata and labels.

#### Scenario: Headline typography
- **WHEN** a headline or title is rendered
- **THEN** it SHALL use the Space Grotesk font family

#### Scenario: Body typography
- **WHEN** body text, metadata or labels are rendered
- **THEN** they SHALL use the Inter font family

#### Scenario: Font provider unavailable
- **WHEN** the Google Fonts provider cannot load a font
- **THEN** the app SHALL fall back to the system font family without crashing

### Requirement: Shape and spacing scale
The application SHALL use a consistent shape and spacing scale across the main screen and its components.

#### Scenario: Card shape
- **WHEN** a recording card is rendered
- **THEN** it SHALL use a `16dp` corner radius

#### Scenario: Pill shape
- **WHEN** a tag badge or filter chip is rendered
- **THEN** it SHALL use a fully rounded pill shape

#### Scenario: Recording FAB shape
- **WHEN** the recording FAB is rendered
- **THEN** it SHALL use a `28dp` corner radius

#### Scenario: Screen spacing
- **WHEN** screen content is laid out
- **THEN** horizontal margins SHALL be `16dp`
- **AND** spacing between recording cards SHALL be `12dp`

### Requirement: Status bar styling
The status bar SHALL match the primary top bar color and use light content icons.

#### Scenario: Status bar uses primary color
- **WHEN** the main screen is displayed
- **THEN** the status bar SHALL use `SkyBlue`
- **AND** the status bar icons SHALL be light
