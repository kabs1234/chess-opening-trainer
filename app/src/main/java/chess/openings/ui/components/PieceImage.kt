package chess.openings.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import chess.openings.R
import chess.openings.model.Piece
import chess.openings.model.PieceColor
import chess.openings.model.PieceType

fun PieceColor.displayName(): String = when (this) {
    PieceColor.WHITE -> "White"
    PieceColor.BLACK -> "Black"
}

@Composable
fun PieceImage(piece: Piece, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(piece.drawable()),
        contentDescription = "${piece.color.displayName()} ${piece.type.name.lowercase()}",
        modifier = modifier,
    )
}

@DrawableRes
private fun Piece.drawable(): Int = when (color) {
    PieceColor.WHITE -> whiteDrawable(type)
    PieceColor.BLACK -> blackDrawable(type)
}

private fun whiteDrawable(type: PieceType): Int = when (type) {
    PieceType.KING -> R.drawable.piece_white_king
    PieceType.QUEEN -> R.drawable.piece_white_queen
    PieceType.ROOK -> R.drawable.piece_white_rook
    PieceType.BISHOP -> R.drawable.piece_white_bishop
    PieceType.KNIGHT -> R.drawable.piece_white_knight
    PieceType.PAWN -> R.drawable.piece_white_pawn
}

private fun blackDrawable(type: PieceType): Int = when (type) {
    PieceType.KING -> R.drawable.piece_black_king
    PieceType.QUEEN -> R.drawable.piece_black_queen
    PieceType.ROOK -> R.drawable.piece_black_rook
    PieceType.BISHOP -> R.drawable.piece_black_bishop
    PieceType.KNIGHT -> R.drawable.piece_black_knight
    PieceType.PAWN -> R.drawable.piece_black_pawn
}
