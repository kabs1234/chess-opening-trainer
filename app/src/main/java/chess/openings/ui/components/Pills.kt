package chess.openings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import chess.openings.model.Piece
import chess.openings.model.PieceColor
import chess.openings.model.PieceType

data class PillColors(val container: Color, val content: Color)

@Composable
fun InfoPill(
    text: String,
    colors: PillColors,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Surface(modifier = modifier, shape = CircleShape, color = colors.container, contentColor = colors.content) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let { Icon(it, contentDescription = null, modifier = Modifier.size(16.dp)) }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun SideBadge(side: PieceColor, modifier: Modifier = Modifier) {
    InfoPill(
        text = "Play as ${side.displayName()}",
        colors = PillColors(
            container = MaterialTheme.colorScheme.surfaceContainerHighest,
            content = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = modifier,
    )
}

@Composable
fun SideMarker(side: PieceColor, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.size(44.dp), shape = CircleShape, color = LIGHT_SQUARE) {
        PieceImage(Piece(PieceType.KING, side), Modifier.padding(4.dp))
    }
}
