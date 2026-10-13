# UI milestone verification — 1 October 2026

## Passed

- `:app:assembleDebug --offline --no-configuration-cache`: BUILD SUCCESSFUL, including the final scrolling-layout and Java formatting changes.
- Installed the generated debug APK on the existing Pixel 9a emulator and launched the app.
- Visually reviewed Pantry, Add Ingredient, Recipe List, Recipe Detail and Settings.
- Opened the explicitly labelled empty recipe preview.
- Submitted an empty ingredient name and observed validation feedback.
- Added Rice with 250 g, opened its edit form, confirmed deletion and verified its removal from the list.
- Submitted a zero quantity and visually confirmed the error; changed Tomatoes to 6 pcs and verified the refreshed list.
- Rebuilt and reinstalled the final APK. At 360 × 640 dp and 1.3 font scale, scrolled the recipe screen to its last card with navigation still reachable.
- AndroidRuntime error log was empty after the main interaction checks.

## Limitations

- Full Android lint could not finish: Google Maven DNS resolution failed for the existing Material dependency's `androidx.graphics:graphics-shapes-desktop:1.0.1` lint dependency. Rerun lint when connectivity is restored.
- The emulator briefly showed a System UI not responding dialog during initial startup. Dismissing that system dialog allowed app checks to proceed; no app crash appeared in the runtime error log.
- Rotation, invalid dates, TalkBack, landscape and process-death recovery were not exhaustively tested. The README includes follow-up manual checks.
- All pantry operations exercised here are session-only previews. Database persistence and recipe eligibility are not implemented or claimed.

Screenshots in `screenshots/` are actual emulator captures from this UI milestone, not final assignment evidence of database or matching functionality.
