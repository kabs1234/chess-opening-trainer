package chess.openings.ui.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
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
import chess.openings.ui.components.InfoPill
import chess.openings.ui.components.MoveList
import chess.openings.ui.components.PillColors
import chess.openings.ui.components.SquareHighlight
import chess.openings.ui.components.displayName
import chess.openings.ui.components.lastMoveHighlights
import chess.openings.ui.components.mistakeCount

private val FEEDBACK_MIN_HEIGHT = 88.dp

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
                StatusLine(state)
                FeedbackBanner(state.feedback, state.opening.name)
                PracticeButtons(onHint = viewModel::onHint, onRestart = viewModel::restart)
                ScoreRow(mistakes = state.mistakes, hintsUsed = state.hintsUsed)
            }
            if (state.moveIndex > 0) {
                MoveList(
                    moves = state.opening.displayMoves.take(state.moveIndex),
                    highlightedIndex = state.moveIndex - 1,
                )
            }
        }
    }
}

@Composable
private fun StatusLine(state: PracticeUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        if (state.isOpponentThinking) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Text("Opponent is thinking…", style = MaterialTheme.typography.titleMedium)
        } else {
            Text("Your move as ${state.opening.side.displayName()}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

private data class BannerStyle(val icon: ImageVector, val container: Color, val content: Color)

@Composable
private fun FeedbackBanner(feedback: MoveFeedback?, drillName: String) {
    val style = bannerStyle(feedback)
    Surface(
        modifier = Modifier.fillMaxWidth().heightIn(min = FEEDBACK_MIN_HEIGHT),
        shape = RoundedCornerShape(12.dp),
        color = style.container,
        contentColor = style.content,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(style.icon, contentDescription = null)
            Text(feedbackText(feedback, drillName), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun bannerStyle(feedback: MoveFeedback?): BannerStyle {
    val colors = MaterialTheme.colorScheme
    return when (feedback) {
        null -> BannerStyle(Icons.Filled.Info, colors.surfaceContainer, colors.onSurfaceVariant)
        MoveFeedback.BookMove -> BannerStyle(Icons.Filled.CheckCircle, colors.primaryContainer, colors.onPrimaryContainer)
        is MoveFeedback.OtherOpening -> BannerStyle(Icons.Filled.Info, colors.tertiaryContainer, colors.onTertiaryContainer)
        MoveFeedback.NotInBook -> BannerStyle(Icons.Filled.Warning, colors.errorContainer, colors.onErrorContainer)
    }
}

private fun feedbackText(feedback: MoveFeedback?, drillName: String): String = when (feedback) {
    null -> "Tap one of your pieces, then a marked square to move it."
    MoveFeedback.BookMove -> "Book move."
    is MoveFeedback.OtherOpening -> "Good move. It leads to the ${feedback.openingName}, but this drill is the $drillName. Try again."
    MoveFeedback.NotInBook -> "Legal, but not a book move here. Try again or take a hint."
}

@Composable
private fun PracticeButtons(onHint: () -> Unit, onRestart: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconTextButton(painterResource(R.drawable.ic_hint), "Hint", onHint, Modifier.weight(1f))
        IconTextButton(rememberVectorPainter(Icons.Filled.Refresh), "Restart", onRestart, Modifier.weight(1f))
    }
}

@Composable
private fun IconTextButton(icon: Painter, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier, contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
        Text(text, modifier = Modifier.padding(start = ButtonDefaults.IconSpacing))
    }
}

@Composable
private fun ScoreRow(mistakes: Int, hintsUsed: Int) {
    val colors = MaterialTheme.colorScheme
    val mistakeColors = if (mistakes == 0) {
        PillColors(colors.surfaceContainerHighest, colors.onSurfaceVariant)
    } else {
        PillColors(colors.errorContainer, colors.onErrorContainer)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        InfoPill("Mistakes: $mistakes", mistakeColors)
        InfoPill("Hints: $hintsUsed", PillColors(colors.surfaceContainerHighest, colors.onSurfaceVariant))
    }
}

@Composable
private fun ResultCard(state: PracticeUiState, onAgain: () -> Unit, onBackToList: () -> Unit) {
    val isPerfect = state.mistakes == 0
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (isPerfect) Icons.Filled.Star else Icons.Filled.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
                Text("Line complete!", style = MaterialTheme.typography.headlineSmall)
            }
            Text(resultText(state.mistakes), style = MaterialTheme.typography.bodyLarge)
            Text("Hints used: ${state.hintsUsed}", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onAgain, contentPadding = ButtonDefaults.ButtonWithIconContentPadding) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Text("Again", modifier = Modifier.padding(start = ButtonDefaults.IconSpacing))
                }
                OutlinedButton(onClick = onBackToList) { Text("Back to list") }
            }
        }
    }
}

private fun resultText(mistakes: Int): String =
    if (mistakes == 0) "Perfect, no mistakes." else "${mistakeCount(mistakes)}. One more run?"

private fun practiceHighlights(state: PracticeUiState, selectedSquare: String?): Map<String, SquareHighlight> = buildMap {
    putAll(lastMoveHighlights(state.lastMove))
    selectedSquare?.let { selected ->
        state.position.targetsFrom(selected).forEach { put(it, SquareHighlight.TARGET) }
        put(selected, SquareHighlight.SELECTED)
    }
    state.hintSquare?.let { put(it, SquareHighlight.HINT) }
    state.wrongSquare?.let { put(it, SquareHighlight.WRONG) }
}
