package chess.openings.ui.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import chess.openings.AppContainer
import chess.openings.model.PieceColor
import chess.openings.ui.components.AppTopBar
import chess.openings.ui.components.BoardWithPanel
import chess.openings.ui.components.ChessBoard
import chess.openings.ui.components.MoveList
import chess.openings.ui.components.SquareHighlight
import chess.openings.ui.components.displayName
import chess.openings.ui.components.lastMoveHighlights

private val GOOD_FEEDBACK = Color(0xFF2E7D32)

@Composable
fun PracticeScreen(
    onBack: () -> Unit,
    onBackToList: () -> Unit,
    viewModel: PracticeViewModel = viewModel { PracticeViewModel(createSavedStateHandle(), AppContainer.repository) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedSquare by rememberSaveable(state.moveIndex) { mutableStateOf<String?>(null) }
    val highlights = remember(state, selectedSquare) { practiceHighlights(state, selectedSquare) }

    fun onSquareClick(square: String) {
        val selected = selectedSquare
        when {
            !state.canPlayerMove -> Unit
            square == selected -> selectedSquare = null
            state.position.pieces[square]?.color == state.opening.side -> {
                selectedSquare = square
                viewModel.clearMistakeMark()
            }
            selected != null && square in state.position.targetsFrom(selected) -> {
                selectedSquare = null
                viewModel.onMoveAttempt(selected, square)
            }
            else -> selectedSquare = null
        }
    }

    Scaffold(topBar = { AppTopBar("Practice: ${state.opening.name}", onBack) }) { padding ->
        BoardWithPanel(
            modifier = Modifier.padding(padding),
            board = { boardModifier ->
                ChessBoard(
                    board = state.position.pieces,
                    modifier = boardModifier,
                    isFlipped = state.opening.side == PieceColor.BLACK,
                    highlights = highlights,
                    onSquareClick = ::onSquareClick,
                )
            },
        ) {
            if (state.isFinished) {
                ResultCard(state = state, onAgain = viewModel::restart, onBackToList = onBackToList)
            } else {
                StatusText(state)
                PracticeButtons(onHint = viewModel::onHint, onRestart = viewModel::restart)
            }
            Text("Mistakes: ${state.mistakes} · Hints: ${state.hintsUsed}", style = MaterialTheme.typography.labelLarge)
            MoveList(
                moves = state.opening.displayMoves.take(state.moveIndex),
                highlightedIndex = state.moveIndex - 1,
            )
        }
    }
}

@Composable
private fun StatusText(state: PracticeUiState) {
    val turnText = when {
        state.isOpponentThinking -> "Opponent is thinking…"
        else -> "Your move as ${state.opening.side.displayName()}"
    }
    Text(turnText, style = MaterialTheme.typography.titleMedium)
    state.feedback?.let { feedback ->
        Text(
            text = feedbackText(feedback, state.opening.name),
            color = if (feedback == MoveFeedback.NotInBook) MaterialTheme.colorScheme.error else GOOD_FEEDBACK,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun feedbackText(feedback: MoveFeedback, drillName: String): String = when (feedback) {
    MoveFeedback.BookMove -> "✓ Book move"
    is MoveFeedback.OtherOpening -> "Good move. It leads to the ${feedback.openingName}, but this drill is the $drillName. Try again."
    MoveFeedback.NotInBook -> "✗ Legal, but not a book move here. Try again or take a hint."
}

@Composable
private fun PracticeButtons(onHint: () -> Unit, onRestart: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = onHint, modifier = Modifier.weight(1f)) { Text("Hint") }
        OutlinedButton(onClick = onRestart, modifier = Modifier.weight(1f)) { Text("Restart") }
    }
}

@Composable
private fun ResultCard(state: PracticeUiState, onAgain: () -> Unit, onBackToList: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Line complete!", style = MaterialTheme.typography.titleLarge)
            Text(resultText(state.mistakes), style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAgain) { Text("Again") }
                OutlinedButton(onClick = onBackToList) { Text("Back to list") }
            }
        }
    }
}

private fun resultText(mistakes: Int): String = when (mistakes) {
    0 -> "Perfect, no mistakes."
    1 -> "1 mistake. One more run?"
    else -> "$mistakes mistakes. One more run?"
}

private fun practiceHighlights(state: PracticeUiState, selectedSquare: String?): Map<String, SquareHighlight> = buildMap {
    putAll(lastMoveHighlights(state.lastMove))
    selectedSquare?.let { selected ->
        state.position.targetsFrom(selected).forEach { put(it, SquareHighlight.TARGET) }
        put(selected, SquareHighlight.SELECTED)
    }
    state.hintSquare?.let { put(it, SquareHighlight.HINT) }
    state.wrongSquare?.let { put(it, SquareHighlight.WRONG) }
}
