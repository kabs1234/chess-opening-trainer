package chess.openings.data

import chess.openings.model.ChessPosition
import org.junit.Assert.assertEquals
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
        OpeningCatalog.OPENINGS.forEach { opening ->
            assertEquals(opening.id, opening.displayMoves.size, opening.moves.size)
            ChessPosition.after(opening.moves)
        }
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
