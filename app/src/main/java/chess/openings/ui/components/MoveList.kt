package chess.openings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun moveLabel(index: Int, move: String): String =
    if (index % 2 == 0) "${index / 2 + 1}. $move" else move

fun movePreview(displayMoves: List<String>, plyCount: Int): String =
    displayMoves.take(plyCount).mapIndexed(::moveLabel).joinToString(" ")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoveList(
    moves: List<String>,
    modifier: Modifier = Modifier,
    highlightedIndex: Int? = null,
    onMoveClick: ((Int) -> Unit)? = null,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        moves.forEachIndexed { index, move ->
            val isHighlighted = index == highlightedIndex
            Text(
                text = moveLabel(index, move),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isHighlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .background(
                        color = if (isHighlighted) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .then(if (onMoveClick == null) Modifier else Modifier.clickable { onMoveClick(index) })
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
}
