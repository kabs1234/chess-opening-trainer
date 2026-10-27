# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

Chess opening trainer for Android (Kotlin, Jetpack Compose, Material 3). It's a university midterm project, already submitted at https://github.com/kabs1234/chess-opening-trainer. The grading requirements are in `Midterm Project - Android.pdf`: 3+ screens with Navigation Compose, a ViewModel with StateFlow collected lifecycle-aware, `remember`/`rememberSaveable`/state hoisting, correct behaviour on rotation, and a README with features, run instructions and 3+ screenshots from `screenshots/`. Changes must keep all of these true.

## Commands

JDK 17+ is required (the Android Studio JBR works; an older `java` on PATH fails the build).

```bash
./gradlew :app:assembleDebug                 # build
./gradlew :app:installDebug                  # build and install on a connected device/emulator
./gradlew :app:testDebugUnitTest             # all unit tests
./gradlew :app:testDebugUnitTest --tests "chess.openings.data.OpeningCatalogTest"   # one class
./gradlew :app:lintDebug                     # Android lint
```

minSdk 24, compileSdk/targetSdk 37. Package and namespace: `chess.openings`.

## Architecture

Three screens, routes `list`, `study/{openingId}`, `practice/{openingId}` (`ui/AppNavHost.kt`). The id reaches each ViewModel through `SavedStateHandle`; `ui/OpeningArguments.kt` has `requireOpening()` for that.

- **Rules engine:** only `model/Chess.kt` (`ChessPosition`) talks to chesslib. Moves are coordinate strings (`"e2e4"`, castling `"e1g1"`), the board is `Map<String, Piece>`. Opening lines are written in SAN in the catalog and converted with `ChessPosition.movesFromSan`.
- **Openings are trees.** An `Opening` has a main line (`line`) plus `sidelines: List<OpeningLine>`; `lines` is both. The player's moves are identical in every line of an opening, and lines may only branch on the opponent's move. `OpeningCatalogTest.linesOfAnOpeningOnlyBranchOnTheOpponentsMove` enforces this, so a new sideline that branches on the player's move fails the unit tests.
- **Practice flow (`PracticeViewModel`):** each run follows one `OpeningLine`, chosen by `nextLineToPractice` (unplayed lines first, then cycle by attempt count). A player move is graded against that line's next move: book move → advance and the opponent replies after 600 ms in `viewModelScope`; a move that continues another opening in the catalog → `MoveFeedback.OtherOpening`, not a mistake; anything else → `NotInBook`, counted as a mistake. When the opponent plays the move where the line leaves the main line, the feedback is `OpponentSideline`. Results go to the repository as `PracticeResult`.
- **State:** every screen collects one immutable UI state `StateFlow` with `collectAsStateWithLifecycle`. Derived values (`position`, `expectedMove`, `isFinished`) are computed properties on the state class, not stored. Only UI-local state lives in composables (`rememberSaveable` for the selected square and the move-list toggle).
- **Data:** `OpeningRepository` is an interface; `InMemoryOpeningRepository` holds progress in a `StateFlow`, so progress resets when the process dies. `AppContainer` is the single manual DI point.
- **UI:** `BoardWithPanel` lays out board + panel vertically in portrait and side by side in landscape, with an optional footer that stays visible. `ChessBoard` is stateless: board, orientation, highlights and an `onSquareClick` in, nothing kept. Pieces are vector drawables (`res/drawable/piece_*.xml`, the cburnett set from Lichess, GPLv2+, credited in the README). Colors live only in `ui/theme/Theme.kt` and the board constants in `ChessBoard.kt`.

## Conventions

- No comments or KDoc in code; names carry the meaning.
- Conventional Commits (`feat(practice): …`, `docs: …`, `build(deps): …`), no AI attribution trailers. Commits go on `main`.
- When the UI changes, refresh the affected screenshots in `screenshots/` and the README text that describes them.

## Future plans

1. **Engine evaluation for off-book moves (next).** When the player's move is `NotInBook`, fetch Stockfish scores from Lichess cloud eval for the position after the player's move and after the book move, and show both in the banner, e.g. "2.d3 is playable: 0.00, the book move 2.Nf3 keeps +0.29".
   - Endpoint: `GET https://lichess.org/api/cloud-eval?fen=<FEN>`. Checked on 2026-10-27: it works without a token and returns `pvs[0].cp` (centipawns from White's point of view) or `mate`. It returns 404 for positions Lichess hasn't analysed.
   - The move still counts as a mistake; the score only explains how far off it is.
   - Offline or 404 must fall back to the current `NotInBook` text.
   - Needs the `INTERNET` permission and a FEN from `ChessPosition` (chesslib's `Board.fen`; not exposed yet). Plan: `HttpURLConnection` + `org.json` (no new dependency), behind an interface with an injected dispatcher so the ViewModel stays testable.
2. **Sidelines for the other six openings.** Only the Italian, the Najdorf and the London have them. Every new line must be real theory, not invented.
3. **Part 2 of the course: Room.** Persist progress by adding a Room-backed `OpeningRepository`; nothing else should need to change.
