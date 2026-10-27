package chess.openings.ui.study

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import chess.openings.AppContainer
import chess.openings.R
import chess.openings.model.PieceColor
import chess.openings.ui.components.AppTopBar
import chess.openings.ui.components.BoardWithPanel
import chess.openings.ui.components.ChessBoard
import chess.openings.ui.components.MoveList
import chess.openings.ui.components.SideBadge
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
            footer = {
                Button(
                    onClick = { onPractice(state.opening.id) },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Text("Practice this line", modifier = Modifier.padding(start = ButtonDefaults.IconSpacing))
                }
            },
        ) {
            NavigationButtons(state = state, viewModel = viewModel)
            MovesHeader(isExpanded = isMoveListVisible, onToggle = { isMoveListVisible = !isMoveListVisible })
            if (isMoveListVisible) {
                MoveList(
                    moves = state.opening.displayMoves,
                    highlightedIndex = state.moveIndex - 1,
                    onMoveClick = { index -> viewModel.goTo(index + 1) },
                )
            }
            OpeningSummary(description = state.opening.description, side = state.opening.side)
        }
    }
}

@Composable
private fun OpeningSummary(description: String, side: PieceColor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SideBadge(side)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun NavigationButtons(state: StudyUiState, viewModel: StudyViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StepButton(painterResource(R.drawable.ic_first_move), "First move", state.canGoBack, viewModel::goToStart)
        StepButton(rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowLeft), "Previous move", state.canGoBack, viewModel::goBack)
        Text(
            "Move ${state.moveIndex} / ${state.opening.moves.size}",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        StepButton(rememberVectorPainter(Icons.AutoMirrored.Filled.KeyboardArrowRight), "Next move", state.canGoForward, viewModel::goForward)
        StepButton(painterResource(R.drawable.ic_last_move), "Last move", state.canGoForward, viewModel::goToEnd)
    }
}

@Composable
private fun StepButton(icon: Painter, description: String, isEnabled: Boolean, onClick: () -> Unit) {
    FilledTonalIconButton(onClick = onClick, enabled = isEnabled) {
        Icon(icon, contentDescription = description)
    }
}

@Composable
private fun MovesHeader(isExpanded: Boolean, onToggle: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("Moves", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
        TextButton(onClick = onToggle) {
            Text(if (isExpanded) "Hide" else "Show")
            Icon(
                if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
        }
    }
}
