package chess.openings.data

import chess.openings.model.ChessPosition
import chess.openings.model.PieceColor

private const val MAIN_LINE_NAME = "Main line"

data class OpeningLine(val name: String, val san: String) {
    val displayMoves: List<String> = san.split(" ")
    val moves: List<String> = ChessPosition.movesFromSan(san)

    fun continuesWith(playedMoves: List<String>): Boolean =
        moves.size >= playedMoves.size && moves.take(playedMoves.size) == playedMoves

    fun deviationIndexFrom(other: OpeningLine): Int? =
        moves.indices.firstOrNull { it >= other.moves.size || moves[it] != other.moves[it] }
}

data class Opening(
    val id: String,
    val name: String,
    val description: String,
    val side: PieceColor,
    val line: String,
    val sidelines: List<OpeningLine> = emptyList(),
) {
    val mainLine: OpeningLine = OpeningLine(MAIN_LINE_NAME, line)
    val lines: List<OpeningLine> = listOf(mainLine) + sidelines
    val displayMoves: List<String> get() = mainLine.displayMoves
    val moves: List<String> get() = mainLine.moves

    val firstMove: String get() = moves.first()

    fun isPlayerMove(index: Int): Boolean =
        (index % 2 == 0) == (side == PieceColor.WHITE)

    fun continuesWith(playedMoves: List<String>): Boolean = lines.any { it.continuesWith(playedMoves) }
}

data class OpeningProgress(
    val attempts: Int,
    val bestMistakes: Int,
    val lastMistakes: Int,
    val practicedLines: Set<String>,
) {
    fun withResult(lineName: String, mistakes: Int): OpeningProgress = copy(
        attempts = attempts + 1,
        bestMistakes = minOf(bestMistakes, mistakes),
        lastMistakes = mistakes,
        practicedLines = practicedLines + lineName,
    )

    companion object {
        fun firstResult(lineName: String, mistakes: Int) = OpeningProgress(
            attempts = 1,
            bestMistakes = mistakes,
            lastMistakes = mistakes,
            practicedLines = setOf(lineName),
        )
    }
}

fun Opening.nextLineToPractice(progress: OpeningProgress?): OpeningLine {
    val practiced = progress?.practicedLines.orEmpty()
    return lines.firstOrNull { it.name !in practiced } ?: lines[(progress?.attempts ?: 0) % lines.size]
}
