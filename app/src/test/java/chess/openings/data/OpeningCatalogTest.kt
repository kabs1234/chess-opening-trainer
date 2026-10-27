package chess.openings.data

import chess.openings.model.ChessPosition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OpeningCatalogTest {
    @Test
    fun sanIsConvertedToCoordinateMoves() {
        assertEquals(listOf("e2e4", "e7e5", "g1f3"), ChessPosition.movesFromSan("e4 e5 Nf3"))
    }

    @Test
    fun castlingMovesTheKingTwoSquaresAndTheRook() {
        val italian = OpeningCatalog.OPENINGS.first { it.id == "italian" }
        val position = ChessPosition.after(italian.moves)

        assertEquals("e1g1", italian.moves[10])
        assertEquals("ROOK", position.pieces["f1"]?.type?.name)
        assertEquals("KING", position.pieces["g8"]?.type?.name)
    }

    @Test
    fun everyLineParsesAndMatchesItsDisplayMoves() {
        OpeningCatalog.OPENINGS.flatMap { it.lines }.forEach { line ->
            assertEquals(line.name, line.displayMoves.size, line.moves.size)
            ChessPosition.after(line.moves)
        }
    }

    @Test
    fun linesOfAnOpeningOnlyBranchOnTheOpponentsMove() {
        OpeningCatalog.OPENINGS.forEach { opening ->
            opening.lines.forEach { first ->
                opening.lines.filter { it != first }.forEach { second ->
                    val branch = checkNotNull(first.deviationIndexFrom(second)) { "${first.name} repeats ${second.name}" }
                    assertFalse("${opening.id}: ${first.name} vs ${second.name}", opening.isPlayerMove(branch))
                }
            }
        }
    }

    @Test
    fun lineNamesAreUniqueWithinAnOpening() {
        OpeningCatalog.OPENINGS.forEach { opening ->
            val names = opening.lines.map { it.name }
            assertEquals(opening.id, names.size, names.toSet().size)
        }
    }

    @Test
    fun practiceVisitsUnpracticedLinesFirstThenCycles() {
        val italian = OpeningCatalog.OPENINGS.first { it.id == "italian" }
        val afterMain = OpeningProgress.firstResult(italian.mainLine.name, mistakes = 0)
        val afterAll = italian.sidelines.fold(afterMain) { progress, line -> progress.withResult(line.name, 0) }

        assertEquals(italian.mainLine, italian.nextLineToPractice(progress = null))
        assertEquals(italian.sidelines.first(), italian.nextLineToPractice(afterMain))
        assertEquals(italian.lines[afterAll.attempts % italian.lines.size], italian.nextLineToPractice(afterAll))
    }

    @Test
    fun openingIdsAreUnique() {
        val ids = OpeningCatalog.OPENINGS.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun legalTargetsComeFromTheRules() {
        val start = ChessPosition.after(emptyList())

        assertEquals(setOf("a3", "c3"), start.targetsFrom("b1"))
        assertTrue(start.isLegal("e2e4"))
    }
}
