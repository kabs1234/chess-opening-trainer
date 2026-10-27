package chess.openings.data

import chess.openings.model.ChessPosition
import chess.openings.model.PieceColor

data class Opening(
    val id: String,
    val name: String,
    val description: String,
    val side: PieceColor,
    val line: String,
) {
    val displayMoves: List<String> = line.split(" ")
    val moves: List<String> = ChessPosition.movesFromSan(line)

    val firstMove: String get() = moves.first()

    fun isPlayerMove(index: Int): Boolean =
        (index % 2 == 0) == (side == PieceColor.WHITE)

    fun continuesWith(playedMoves: List<String>): Boolean =
        moves.size >= playedMoves.size && moves.take(playedMoves.size) == playedMoves
}

data class OpeningProgress(
    val attempts: Int,
    val bestMistakes: Int,
    val lastMistakes: Int,
) {
    fun withResult(mistakes: Int): OpeningProgress = copy(
        attempts = attempts + 1,
        bestMistakes = minOf(bestMistakes, mistakes),
        lastMistakes = mistakes,
    )

    companion object {
        fun firstResult(mistakes: Int) = OpeningProgress(attempts = 1, bestMistakes = mistakes, lastMistakes = mistakes)
    }
}
