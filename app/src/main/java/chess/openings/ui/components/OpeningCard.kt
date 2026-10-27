package chess.openings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chess.openings.data.Opening
import chess.openings.data.OpeningProgress
import chess.openings.model.PieceColor

private const val PREVIEW_PLY_COUNT = 6

fun PieceColor.displayName(): String = when (this) {
    PieceColor.WHITE -> "White"
    PieceColor.BLACK -> "Black"
}

@Composable
fun OpeningCard(
    opening: Opening,
    progress: OpeningProgress?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(opening.name, style = MaterialTheme.typography.titleMedium)
            Text(
                movePreview(opening.displayMoves, PREVIEW_PLY_COUNT) + " …",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text("Play as ${opening.side.displayName()}", style = MaterialTheme.typography.labelLarge)
            Text(progressText(progress), style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun progressText(progress: OpeningProgress?): String =
    if (progress == null) {
        "Not practiced yet"
    } else {
        "Practiced ${progress.attempts}× · best: ${mistakeCount(progress.bestMistakes)} · last: ${progress.lastMistakes}"
    }

private fun mistakeCount(mistakes: Int): String = if (mistakes == 1) "1 mistake" else "$mistakes mistakes"
