}Design a native Android voice recording app screen called "Lugrav".
Use Material Design 3 guidelines strictly. Light mode only.

Primary color: Sky blue (#29B6F6, Material Blue Lighten-1).
Background: Material M3 Surface (#FFFBFE).
On-primary text: dark (#0D47A1) for contrast.

--- LAYOUT ---

TOP: Medium Top App Bar (M3 spec).
- Title: "Lugrav" — left aligned, headline font.
- Background: sky blue primary color.
- Title and icon color: white.

BODY: Full-width scrollable list of recording cards.
Each card (M3 ElevatedCard, elevation 1):
- Left side: circular filled icon button with a "play_arrow" icon (sky blue).
- Center column: recording title in bold (e.g. "Gravação 01"),
  subtitle with date and duration (e.g. "04 abr 2026 · 2m 31s").
- Right side: icon button with "more_vert" (three dots vertical).
- Subtle divider between cards.
  Show at least 5 cards in the list to illustrate scrollable content.

BOTTOM: Extended FAB (M3 Extended FAB spec), bottom-end anchored (bottom-right), floating above content.
- Icon: "mic" (microphone).
- Label: "Gravar".
- Background: sky blue (#29B6F6).
- Text and icon color: white.

--- STATE: RECORDING ACTIVE (show as a second screen variant) ---
When recording is active, the FAB changes to:
- Icon: "stop" (square stop icon).
- Label: "00:02:47" (elapsed time in HH:MM:SS format, monospaced digits).
- Background: Material M3 error color (#B3261E, deep red) to signal active recording.
- Text and icon color: white.
  A subtle animated red pulsing dot appears in the top app bar right side,
  replacing the overflow icon, to reinforce recording status.

--- TYPOGRAPHY ---
Display/Headline font: "Space Grotesk" — used for app title and card titles.
Body/Label font: "Inter" — used for subtitles, timestamps, FAB label, and list metadata.
Monospaced font (elapsed time only): "JetBrains Mono" — used exclusively for the HH:MM:SS timer in the FAB.

--- DESIGN TOKENS ---
- Corner radius: 16dp for cards, 28dp for FAB (M3 defaults).
- FAB shadow elevation: 6dp.
- Card padding: 16dp internal, 8dp vertical gap between cards.
- List has 16dp horizontal margin from screen edges.
- Status bar color matches Top App Bar (sky blue).