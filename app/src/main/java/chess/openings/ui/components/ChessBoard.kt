package chess.openings.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import chess.openings.model.Board
import chess.openings.model.Piece
import chess.openings.model.squareName

private const val BOARD_SIZE = 8
private const val LABEL_SIZE_RATIO = 0.2f
private const val MOVE_DOT_RADIUS_RATIO = 0.16f
private const val CAPTURE_RING_RADIUS_RATIO = 0.46f
private const val CAPTURE_RING_WIDTH_RATIO = 0.08f
internal val LIGHT_SQUARE = Color(0xFFEBECD0)
private val DARK_SQUARE = Color(0xFF739552)
private val TARGET_MARKER = Color(0x33000000)
private val BOARD_SHAPE = RoundedCornerShape(8.dp)
private val BOARD_ELEVATION = 4.dp

enum class SquareHighlight(val color: Color) {
    LAST_MOVE(Color(0x80F5F682)),
    TARGET(Color.Transparent),
    HINT(Color(0x884FA3E0)),
    SELECTED(Color(0xB3F5F682)),
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

    BoxWithConstraints(
        modifier
            .aspectRatio(1f)
            .shadow(BOARD_ELEVATION, BOARD_SHAPE)
            .clip(BOARD_SHAPE),
    ) {
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
    val labelColor = if (background == LIGHT_SQUARE) DARK_SQUARE else LIGHT_SQUARE
    val labelSize = with(LocalDensity.current) { (squareSize * LABEL_SIZE_RATIO).toSp() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center,
    ) {
        highlight?.let { Box(Modifier.fillMaxSize().background(it.color)) }
        piece?.let { PieceImage(it, Modifier.fillMaxSize()) }
        if (highlight == SquareHighlight.TARGET) {
            if (piece == null) MoveDot() else CaptureRing()
        }
        rankLabel?.let {
            Text(
                it,
                color = labelColor,
                fontSize = labelSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 3.dp, top = 1.dp),
            )
        }
        fileLabel?.let {
            Text(
                it,
                color = labelColor,
                fontSize = labelSize,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 3.dp),
            )
        }
    }
}

@Composable
private fun MoveDot() {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(color = TARGET_MARKER, radius = size.minDimension * MOVE_DOT_RADIUS_RATIO)
    }
}

@Composable
private fun CaptureRing() {
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(
            color = TARGET_MARKER,
            radius = size.minDimension * CAPTURE_RING_RADIUS_RATIO,
            style = Stroke(width = size.minDimension * CAPTURE_RING_WIDTH_RATIO),
        )
    }
}
