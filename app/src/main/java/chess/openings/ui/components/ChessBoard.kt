package chess.openings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import chess.openings.model.Board
import chess.openings.model.Piece
import chess.openings.model.PieceColor
import chess.openings.model.squareName

private const val BOARD_SIZE = 8
private const val PIECE_SIZE_RATIO = 0.78f
private const val LABEL_SIZE_RATIO = 0.2f
private val LIGHT_SQUARE = Color(0xFFF0D9B5)
private val DARK_SQUARE = Color(0xFFB58863)
private val WHITE_PIECE_SHADOW = Shadow(color = Color.Black, offset = Offset(1f, 1f), blurRadius = 3f)

enum class SquareHighlight(val color: Color) {
    LAST_MOVE(Color(0x66F6F669)),
    TARGET(Color(0x5529A329)),
    HINT(Color(0x884FA3E0)),
    SELECTED(Color(0x8829A329)),
    WRONG(Color(0x99E04F4F)),
}

fun lastMoveHighlights(move: String?): Map<String, SquareHighlight> =
    move?.let { mapOf(it.take(2) to SquareHighlight.LAST_MOVE, it.substring(2, 4) to SquareHighlight.LAST_MOVE) }
        ?: emptyMap()

@Composable
fun ChessBoard(
    board: Board,
    modifier: Modifier = Modifier,
    isFlipped: Boolean = false,
    highlights: Map<String, SquareHighlight> = emptyMap(),
    onSquareClick: ((String) -> Unit)? = null,
) {
    val ranks = if (isFlipped) (1..BOARD_SIZE).toList() else (BOARD_SIZE downTo 1).toList()
    val files = if (isFlipped) (BOARD_SIZE - 1 downTo 0).toList() else (0 until BOARD_SIZE).toList()

    BoxWithConstraints(modifier.aspectRatio(1f)) {
        val squareSize = maxWidth / BOARD_SIZE
        Column(Modifier.fillMaxSize()) {
            ranks.forEach { rank ->
                Row(Modifier.weight(1f)) {
                    files.forEach { file ->
                        val square = squareName(file, rank)
                        BoardSquare(
                            piece = board[square],
                            background = if ((file + rank) % 2 == 0) LIGHT_SQUARE else DARK_SQUARE,
                            highlight = highlights[square],
                            squareSize = squareSize,
                            rankLabel = if (file == files.first()) rank.toString() else null,
                            fileLabel = if (rank == ranks.last()) square.take(1) else null,
                            modifier = Modifier
                                .weight(1f)
                                .then(if (onSquareClick == null) Modifier else Modifier.clickable { onSquareClick(square) }),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardSquare(
    piece: Piece?,
    background: Color,
    highlight: SquareHighlight?,
    squareSize: Dp,
    rankLabel: String?,
    fileLabel: String?,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val labelColor = if (background == LIGHT_SQUARE) DARK_SQUARE else LIGHT_SQUARE
    val labelSize = with(density) { (squareSize * LABEL_SIZE_RATIO).toSp() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        highlight?.let { Box(Modifier.fillMaxSize().background(it.color)) }
        rankLabel?.let {
            Text(it, color = labelColor, fontSize = labelSize, modifier = Modifier.align(Alignment.TopStart).padding(2.dp))
        }
        fileLabel?.let {
            Text(it, color = labelColor, fontSize = labelSize, modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp))
        }
        piece?.let {
            Text(
                text = it.type.symbol,
                fontSize = with(density) { (squareSize * PIECE_SIZE_RATIO).toSp() },
                style = pieceStyle(it.color),
            )
        }
    }
}

private fun pieceStyle(color: PieceColor): TextStyle = when (color) {
    PieceColor.WHITE -> TextStyle(color = Color.White, shadow = WHITE_PIECE_SHADOW)
    PieceColor.BLACK -> TextStyle(color = Color.Black)
}
