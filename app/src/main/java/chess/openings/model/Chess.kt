package chess.openings.model

import com.github.bhlangonijr.chesslib.Board as EngineBoard
import com.github.bhlangonijr.chesslib.Piece as EnginePiece
import com.github.bhlangonijr.chesslib.PieceType as EnginePieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import com.github.bhlangonijr.chesslib.move.MoveList

private const val FILES = "abcdefgh"

enum class PieceColor { WHITE, BLACK }

enum class PieceType { KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN }

data class Piece(val type: PieceType, val color: PieceColor)

typealias Board = Map<String, Piece>

fun squareName(file: Int, rank: Int): String = "${FILES[file]}$rank"

class ChessPosition private constructor(private val engineBoard: EngineBoard) {
    val pieces: Board = Square.entries
        .filter { it != Square.NONE }
        .mapNotNull { square -> engineBoard.getPiece(square).toModel()?.let { square.toName() to it } }
        .toMap()

    fun targetsFrom(square: String): Set<String> = engineBoard.legalMoves()
        .filter { it.from.toName() == square }
        .map { it.to.toName() }
        .toSet()

    fun isLegal(move: String): Boolean = engineBoard.legalMoves().any { it.toString().startsWith(move) }

    companion object {
        fun after(moves: List<String>): ChessPosition {
            val engineBoard = EngineBoard()
            moves.forEach { engineBoard.doMove(Move(it, engineBoard.sideToMove)) }
            return ChessPosition(engineBoard)
        }

        fun movesFromSan(line: String): List<String> =
            MoveList().apply { loadFromSan(line) }.map { it.toString() }
    }
}

private fun Square.toName(): String = value().lowercase()

private fun EnginePiece.toModel(): Piece? {
    val type = when (pieceType) {
        EnginePieceType.KING -> PieceType.KING
        EnginePieceType.QUEEN -> PieceType.QUEEN
        EnginePieceType.ROOK -> PieceType.ROOK
        EnginePieceType.BISHOP -> PieceType.BISHOP
        EnginePieceType.KNIGHT -> PieceType.KNIGHT
        EnginePieceType.PAWN -> PieceType.PAWN
        else -> return null
    }
    val color = if (pieceSide == Side.WHITE) PieceColor.WHITE else PieceColor.BLACK
    return Piece(type, color)
}
