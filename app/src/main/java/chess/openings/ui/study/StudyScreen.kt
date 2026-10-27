package chess.openings.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import chess.openings.ui.components.displayName
import chess.openings.ui.components.lastMoveHighlights

@Composable
fun StudyScreen(
    onBack: () -> Unit,
    onPractice: (String) -> Unit,
    viewModel: StudyViewModel = viewModel { StudyViewModel(createSavedStateHandle(), AppContainer.repository) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var isMoveListVisible by rememberSaveable { mutableStateOf(value = true) }

    Scaffold(topBar = { AppTopBar(state.opening.name, onBack) }) { padding ->
        BoardWithPanel(
            modifier = Modifier.padding(padding),
            board = { boardModifier ->
                ChessBoard(
                    board = state.position.pieces,
                    modifier = boardModifier,
                    isFlipped = state.opening.side == PieceColor.BLACK,
                    highlights = lastMoveHighlights(state.lastMove),
                )
            },
        ) {
            Text(state.opening.description, style = MaterialTheme.typography.bodyMedium)
            Text(
                "You play ${state.opening.side.displayName()} · move ${state.moveIndex} of ${state.opening.moves.size}",
                style = MaterialTheme.typography.labelLarge,
            )
            NavigationButtons(state = state, viewModel = viewModel)
            TextButton(onClick = { isMoveListVisible = !isMoveListVisible }) {
                Text(if (isMoveListVisible) "Hide moves" else "Show moves")
            }
            if (isMoveListVisible) {
                MoveList(
                    moves = state.opening.displayMoves,
                    highlightedIndex = state.moveIndex - 1,
                    onMoveClick = { index -> viewModel.goTo(index + 1) },
                )
            }
            Button(onClick = { onPractice(state.opening.id) }, modifier = Modifier.fillMaxWidth()) {
                Text("Practice this line")
            }
        }
    }
}

@Composable
private fun NavigationButtons(state: StudyUiState, viewModel: StudyViewModel) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = viewModel::goToStart, enabled = state.canGoBack, modifier = Modifier.weight(1f)) { Text("«") }
        OutlinedButton(onClick = viewModel::goBack, enabled = state.canGoBack, modifier = Modifier.weight(1f)) { Text("‹") }
        OutlinedButton(onClick = viewModel::goForward, enabled = state.canGoForward, modifier = Modifier.weight(1f)) { Text("›") }
        OutlinedButton(onClick = viewModel::goToEnd, enabled = state.canGoForward, modifier = Modifier.weight(1f)) { Text("»") }
    }
}
