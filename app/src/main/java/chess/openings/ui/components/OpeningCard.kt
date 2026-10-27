package chess.openings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import chess.openings.data.Opening
import chess.openings.data.OpeningProgress

private const val PREVIEW_PLY_COUNT = 6

@Composable
fun OpeningCard(
    opening: Opening,
    progress: OpeningProgress?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SideMarker(opening.side)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(opening.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    sideAndLinesText(opening),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    movePreview(opening.displayMoves, PREVIEW_PLY_COUNT) + " …",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                ProgressRow(progress, Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun ProgressRow(progress: OpeningProgress?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (progress == null) {
            InfoPill(
                text = "Not practiced yet",
                colors = PillColors(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.colorScheme.onSurfaceVariant),
            )
        } else {
            BestResultPill(progress.bestMistakes)
            Text(
                "Practiced ${progress.attempts}× · last: ${progress.lastMistakes}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BestResultPill(bestMistakes: Int) {
    if (bestMistakes == 0) {
        InfoPill(
            text = "Best: no mistakes",
            colors = PillColors(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer),
            icon = Icons.Filled.Star,
        )
    } else {
        InfoPill(
            text = "Best: ${mistakeCount(bestMistakes)}",
            colors = PillColors(MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer),
        )
    }
}

private fun sideAndLinesText(opening: Opening): String {
    val side = "Play as ${opening.side.displayName()}"
    return if (opening.lines.size > 1) "$side · ${opening.lines.size} lines" else side
}

fun mistakeCount(mistakes: Int): String = if (mistakes == 1) "1 mistake" else "$mistakes mistakes"
