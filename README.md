# Opening Trainer

An Android app for learning chess openings. Pick an opening, step through its main line, then
practice it against the app: you play your side, the app answers with the book replies, and every
move you make is graded against the line.

## Features

- Opening list: nine openings (1.e4, 1.d4 and flank). Search by name or by a move in the line
  ("Najdorf", "Nf3"), and filter by first move. Each card shows the first moves, the side you
  train as and your practice record.
- Study: step through the line with « ‹ › » or tap any move in the move list. The last move is
  highlighted, and the board flips when you train as Black.
- Practice: tap a piece to see its legal squares, then tap where it goes. Any legal move is
  allowed, and the app grades it:
  - The book move gets ✓, and the opponent replies 0.6 s later.
  - A move that leads into another opening from the catalog isn't a mistake, but the drill asks
    for its own line.
  - Any other move gets ✗, counts as a mistake and turns the square red.

  Hint highlights the piece to move. When the line ends, a result card shows your mistakes and
  the list saves your record.

## Architecture

```
model/        Chess.kt            Piece, PieceColor, ChessPosition (wraps chesslib)
data/         Opening.kt          Opening, OpeningProgress
              OpeningCatalog.kt   opening lines in standard notation
              OpeningRepository.kt interface + in-memory implementation
ui/components ChessBoard, BoardWithPanel, MoveList, OpeningCard, AppTopBar
ui/list       OpeningListScreen + OpeningListViewModel
ui/study      StudyScreen + StudyViewModel
ui/practice   PracticeScreen + PracticeViewModel
ui/           AppNavHost, OpeningArguments
AppContainer  the single repository instance shared by every screen
```

- State: screens hold no game state. Each ViewModel owns a `StateFlow` of an immutable UI state,
  and the screens collect it with `collectAsStateWithLifecycle`. The search text is a
  `TextFieldState` held by `OpeningListViewModel`, which combines it with the filter and the saved
  progress.
- State hoisting: `ChessBoard` takes the board, orientation, highlights and an `onSquareClick`
  callback, and keeps nothing of its own.
- Rotation: the move position, mistakes and the opponent's pending reply live in the ViewModel,
  which survives configuration changes, and the reply runs in `viewModelScope`. The selected
  square and the move-list toggle use `rememberSaveable`; `remember` caches the board highlights
  in Practice.
- Navigation: routes `list`, `study/{openingId}` and `practice/{openingId}`. The id reaches the
  ViewModel through `SavedStateHandle`.
- Rules: [chesslib](https://github.com/bhlangonijr/chesslib) parses the opening lines and
  generates legal moves. Only `ChessPosition` talks to it, so the UI sees a plain
  `Map<String, Piece>`.
- Part 2: saving progress to Room means adding a new `OpeningRepository` implementation. Nothing
  else changes.

## How to run

You need Android Studio, JDK 17 or newer, and an emulator or phone running Android 7.0 (API 24)
or higher. The app compiles against API 37.

1. Open the repository folder in Android Studio and wait for the Gradle sync.
2. Pick an emulator or a connected phone and press **Run**.

From the command line, with a device connected:

```bash
./gradlew :app:installDebug
./gradlew :app:testDebugUnitTest
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Screenshots

| Opening list | Study | Practice feedback | Result |
|---|---|---|---|
| ![Opening list with saved progress](screenshots/01-list.png) | ![Study screen stepping through the French](screenshots/02-study.png) | ![Practice grading an alternative move](screenshots/03-practice-feedback.png) | ![Result card after finishing the Italian](screenshots/04-result.png) |

| Search |
|---|
| ![Searching for Nf3 among 1.d4 openings](screenshots/05-search.png) |
